"""
Spectrum Analysis Module
========================

FFT-based spectrum analysis with windowing functions.

Functions:
    analyze_spectrum: Compute FFT and power spectrum density
    plot_spectrum: Generate spectrum visualization

Example:
    >>> import numpy as np
    >>> from signal_processing.spectrum import analyze_spectrum
    >>> fs = 1000
    >>> t = np.arange(0, 1, 1/fs)
    >>> signal = np.sin(2*np.pi*50*t) + 0.5*np.sin(2*np.pi*120*t)
    >>> fft_result, frequencies, magnitude_db = analyze_spectrum(signal, fs, window='hann')
"""

import numpy as np
import matplotlib.pyplot as plt
from typing import Tuple, Optional


def analyze_spectrum(
    signal: np.ndarray,
    fs: float,
    window: str = 'hann',
    nfft: Optional[int] = None,
    plot: bool = False
) -> Tuple[np.ndarray, np.ndarray, np.ndarray]:
    """
    Perform FFT-based spectrum analysis.
    
    Args:
        signal: Input signal (time domain)
        fs: Sampling frequency in Hz
        window: Window function ('hann', 'hamming', 'blackman', 'none')
        nfft: Number of FFT points (default: length of signal)
        plot: Generate spectrum plot
        
    Returns:
        Tuple containing:
            - fft_result: Complex FFT result
            - frequencies: Frequency vector in Hz
            - magnitude_db: Magnitude spectrum in dB
            
    Raises:
        ValueError: If invalid window type or sampling frequency
        
    Example:
        >>> signal = np.random.randn(1000)
        >>> fft_result, freq, mag_db = analyze_spectrum(signal, 1000, window='hann')
    """
    if fs <= 0:
        raise ValueError("Sampling frequency must be positive")
    
    # Signal length
    N = len(signal)
    if N == 0:
        raise ValueError("Signal cannot be empty")
    
    if nfft is None:
        nfft = N
    
    # Apply window function
    if window == 'hann':
        window_func = np.hanning(N)
    elif window == 'hamming':
        window_func = np.hamming(N)
    elif window == 'blackman':
        window_func = np.blackman(N)
    elif window == 'none':
        window_func = np.ones(N)
    else:
        raise ValueError(f"Unknown window function: {window}")
    
    windowed_signal = signal * window_func
    
    # Compute FFT
    fft_result = np.fft.fft(windowed_signal, n=nfft)
    
    # Generate frequency vector
    frequencies = np.fft.fftfreq(nfft, d=1/fs)
    
    # Compute magnitude in dB
    magnitude = np.abs(fft_result)
    magnitude_db = 20 * np.log10(magnitude + np.finfo(float).eps)
    
    # Normalize to 0 dB maximum
    magnitude_db = magnitude_db - np.max(magnitude_db)
    
    # Plot if requested
    if plot:
        plot_spectrum(frequencies, magnitude_db, fs)
    
    return fft_result, frequencies, magnitude_db


def plot_spectrum(frequencies: np.ndarray, magnitude_db: np.ndarray, fs: float) -> None:
    """
    Plot frequency spectrum.
    
    Args:
        frequencies: Frequency vector in Hz
        magnitude_db: Magnitude spectrum in dB
        fs: Sampling frequency in Hz
    """
    plt.figure(figsize=(10, 6))
    
    # Plot only positive frequencies
    idx = frequencies >= 0
    plt.plot(frequencies[idx], magnitude_db[idx], linewidth=1.5)
    
    plt.grid(True, alpha=0.3)
    plt.xlabel('Frequency (Hz)', fontsize=12)
    plt.ylabel('Magnitude (dB)', fontsize=12)
    plt.title('Power Spectrum Density', fontsize=14, fontweight='bold')
    plt.xlim([0, fs/2])
    plt.ylim([np.min(magnitude_db[idx]) - 10, 5])
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    # Demo
    fs = 1000
    t = np.arange(0, 1, 1/fs)
    signal = np.sin(2*np.pi*50*t) + 0.5*np.sin(2*np.pi*120*t) + 0.2*np.random.randn(len(t))
    
    fft_result, frequencies, magnitude_db = analyze_spectrum(signal, fs, window='hann', plot=True)
    print(f"Spectrum analysis complete. Found {len(fft_result)} frequency bins.")
