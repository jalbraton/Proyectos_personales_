"""
Test Signal Generator Module
=============================

Generate various test signals for signal processing applications.

Functions:
    generate_signal: Generate test signals (sine, square, chirp, noise, etc.)

Example:
    >>> from signal_processing.signal_generator import generate_signal
    >>> signal, t = generate_signal('sine', fs=1000, duration=1, frequency=50)
"""

import numpy as np
from scipy import signal as sp_signal
from typing import Tuple, Optional, List


def generate_signal(
    signal_type: str,
    fs: float,
    duration: float,
    frequency: float = 100,
    amplitude: float = 1.0,
    phase: float = 0.0,
    f0: float = 0,
    f1: Optional[float] = None,
    snr: float = np.inf,
    duty_cycle: float = 0.5,
    composite_freqs: Optional[List[float]] = None
) -> Tuple[np.ndarray, np.ndarray]:
    """
    Generate various test signals.
    
    Args:
        signal_type: Type ('sine', 'square', 'sawtooth', 'triangle', 'chirp',
                     'white_noise', 'pink_noise', 'impulse', 'step', 'composite')
        fs: Sampling frequency in Hz
        duration: Signal duration in seconds
        frequency: Frequency for periodic signals in Hz
        amplitude: Signal amplitude
        phase: Initial phase in radians
        f0: Start frequency for chirp in Hz
        f1: End frequency for chirp in Hz
        snr: Signal-to-Noise Ratio in dB
        duty_cycle: Duty cycle for square wave (0 to 1)
        composite_freqs: List of frequencies for composite signal
        
    Returns:
        Tuple (signal, t):
            - signal: Generated signal
            - t: Time vector in seconds
            
    Raises:
        ValueError: If invalid signal type or parameters
        
    Example:
        >>> signal, t = generate_signal('sine', 1000, 1, frequency=50, amplitude=2)
        >>> chirp_signal, t = generate_signal('chirp', 10000, 2, f0=0, f1=2000)
    """
    if fs <= 0:
        raise ValueError("Sampling frequency must be positive")
    if duration <= 0:
        raise ValueError("Duration must be positive")
    if frequency < 0:
        raise ValueError("Frequency cannot be negative")
    if amplitude < 0:
        raise ValueError("Amplitude cannot be negative")
    
    # Generate time vector
    t = np.arange(0, duration, 1/fs)
    
    # Generate signal based on type
    signal_type = signal_type.lower()
    
    if signal_type == 'sine':
        signal = _generate_sine(t, frequency, amplitude, phase)
        
    elif signal_type == 'square':
        signal = _generate_square(t, frequency, amplitude, duty_cycle)
        
    elif signal_type == 'sawtooth':
        signal = _generate_sawtooth(t, frequency, amplitude)
        
    elif signal_type == 'triangle':
        signal = _generate_triangle(t, frequency, amplitude)
        
    elif signal_type == 'chirp':
        if f1 is None:
            f1 = fs / 4
        if f0 < 0 or f1 < 0:
            raise ValueError("Chirp frequencies must be non-negative")
        if f1 >= fs / 2:
            raise ValueError(f"End frequency {f1} exceeds Nyquist frequency {fs/2}")
        signal = _generate_chirp(t, f0, f1, amplitude)
        
    elif signal_type == 'white_noise':
        signal = _generate_white_noise(len(t), amplitude)
        
    elif signal_type == 'pink_noise':
        signal = _generate_pink_noise(len(t), amplitude)
        
    elif signal_type == 'impulse':
        signal = _generate_impulse(len(t), amplitude)
        
    elif signal_type == 'step':
        signal = _generate_step(len(t), amplitude)
        
    elif signal_type == 'composite':
        if composite_freqs is None:
            composite_freqs = [50, 120, 300]
        signal = _generate_composite(t, composite_freqs, amplitude)
        
    else:
        raise ValueError(f"Unknown signal type: {signal_type}")
    
    # Add noise if SNR specified
    if np.isfinite(snr):
        signal = _add_noise(signal, snr)
    
    return signal, t


def _generate_sine(t: np.ndarray, freq: float, amp: float, phase: float) -> np.ndarray:
    """Generate sinusoidal signal."""
    return amp * np.sin(2 * np.pi * freq * t + phase)


def _generate_square(t: np.ndarray, freq: float, amp: float, duty_cycle: float) -> np.ndarray:
    """Generate square wave."""
    return amp * sp_signal.square(2 * np.pi * freq * t, duty=duty_cycle)


def _generate_sawtooth(t: np.ndarray, freq: float, amp: float) -> np.ndarray:
    """Generate sawtooth wave."""
    return amp * sp_signal.sawtooth(2 * np.pi * freq * t)


def _generate_triangle(t: np.ndarray, freq: float, amp: float) -> np.ndarray:
    """Generate triangle wave."""
    return amp * sp_signal.sawtooth(2 * np.pi * freq * t, width=0.5)


def _generate_chirp(t: np.ndarray, f0: float, f1: float, amp: float) -> np.ndarray:
    """Generate linear chirp (frequency sweep)."""
    return amp * sp_signal.chirp(t, f0, t[-1], f1)


def _generate_white_noise(N: int, amp: float) -> np.ndarray:
    """Generate white Gaussian noise."""
    return amp * np.random.randn(N)


def _generate_pink_noise(N: int, amp: float) -> np.ndarray:
    """Generate pink noise (1/f noise)."""
    # Generate white noise
    white = np.random.randn(N)
    
    # Apply 1/f filter in frequency domain
    X = np.fft.fft(white)
    freqs = np.arange(1, len(X)//2 + 1)
    
    # Create 1/sqrt(f) characteristic
    filter_resp = np.ones(len(X))
    filter_resp[1:len(freqs) + 1] = 1 / np.sqrt(freqs)
    # Mirror for negative frequencies (avoid IndexError)
    if len(X) > len(freqs) + 1:
        filter_resp[len(freqs) + 1:] = filter_resp[1:len(freqs) + 1][::-1]
    
    # Apply filter
    X_filtered = X * filter_resp
    signal = np.real(np.fft.ifft(X_filtered))
    
    # Normalize and scale (avoid division by zero)
    max_val = np.max(np.abs(signal))
    if max_val > 0:
        signal = signal / max_val * amp
    else:
        signal = signal * amp
    return signal


def _generate_impulse(N: int, amp: float) -> np.ndarray:
    """Generate impulse signal (delta function)."""
    signal = np.zeros(N)
    signal[N // 2] = amp
    return signal


def _generate_step(N: int, amp: float) -> np.ndarray:
    """Generate step function."""
    signal = np.zeros(N)
    signal[N // 2:] = amp
    return signal


def _generate_composite(t: np.ndarray, freqs: List[float], amp: float) -> np.ndarray:
    """Generate composite signal (sum of sinusoids)."""
    if not freqs:
        raise ValueError("Composite signal requires at least one frequency")
    
    signal = np.zeros(len(t))
    for freq in freqs:
        if freq < 0:
            raise ValueError(f"Frequency {freq} cannot be negative")
        signal += np.sin(2 * np.pi * freq * t)
    
    # Normalize and scale (avoid division by zero)
    max_val = np.max(np.abs(signal))
    if max_val > 0:
        signal = signal / max_val * amp
    else:
        signal = signal * amp
    return signal


def _add_noise(signal: np.ndarray, snr_db: float) -> np.ndarray:
    """Add white Gaussian noise to achieve specified SNR."""
    # Calculate signal power
    signal_power = np.mean(signal ** 2)
    
    # Calculate noise power for desired SNR
    snr_linear = 10 ** (snr_db / 10)
    noise_power = signal_power / snr_linear
    
    # Generate noise
    noise = np.sqrt(noise_power) * np.random.randn(len(signal))
    
    # Add noise to signal
    return signal + noise


if __name__ == "__main__":
    # Demo
    import matplotlib.pyplot as plt
    
    fs = 1000
    
    # Generate various signals
    sine, t = generate_signal('sine', fs, 1, frequency=50)
    chirp, _ = generate_signal('chirp', fs, 1, f0=0, f1=500)
    composite, _ = generate_signal('composite', fs, 1, composite_freqs=[50, 120, 300])
    
    # Plot
    fig, axes = plt.subplots(3, 1, figsize=(12, 8))
    
    axes[0].plot(t[:500], sine[:500])
    axes[0].set_title('Sine Wave (50 Hz)')
    axes[0].grid(True, alpha=0.3)
    
    axes[1].plot(t[:500], chirp[:500])
    axes[1].set_title('Chirp Signal (0-500 Hz)')
    axes[1].grid(True, alpha=0.3)
    
    axes[2].plot(t[:500], composite[:500])
    axes[2].set_title('Composite Signal (50, 120, 300 Hz)')
    axes[2].grid(True, alpha=0.3)
    axes[2].set_xlabel('Time (s)')
    
    plt.tight_layout()
    plt.show()
    
    print("Signal generation demo complete.")
