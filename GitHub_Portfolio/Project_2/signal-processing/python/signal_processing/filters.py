"""
Digital Filter Design Module
=============================

IIR and FIR filter design with various methods.

Functions:
    design_filter: Design digital filters (Butterworth, Chebyshev, Elliptic, FIR)
    apply_filter: Apply filter to signal
    plot_frequency_response: Plot filter frequency response

Example:
    >>> from signal_processing.filters import design_filter, apply_filter
    >>> b, a = design_filter('lowpass', order=4, cutoff=100, fs=1000, method='butterworth')
    >>> filtered_signal = apply_filter(signal, b, a)
"""

import numpy as np
from scipy import signal as sp_signal
import matplotlib.pyplot as plt
from typing import Tuple, Union, Optional


def design_filter(
    filter_type: str,
    order: int,
    cutoff: Union[float, Tuple[float, float]],
    fs: float,
    method: str = 'butterworth',
    ripple: float = 0.5,
    stopband_atten: float = 40,
    plot: bool = False
) -> Tuple[np.ndarray, np.ndarray]:
    """
    Design digital filter (IIR or FIR).
    
    Args:
        filter_type: 'lowpass', 'highpass', 'bandpass', or 'bandstop'
        order: Filter order
        cutoff: Cutoff frequency in Hz (scalar for LP/HP, tuple for BP/BS)
        fs: Sampling frequency in Hz
        method: 'butterworth', 'chebyshev1', 'chebyshev2', 'elliptic', or 'fir'
        ripple: Passband ripple in dB (for Chebyshev/Elliptic)
        stopband_atten: Stopband attenuation in dB (for Chebyshev2/Elliptic)
        plot: Generate frequency response plot
        
    Returns:
        Tuple (b, a):
            - b: Numerator coefficients
            - a: Denominator coefficients (1 for FIR)
            
    Raises:
        ValueError: If invalid parameters provided
        
    Example:
        >>> b, a = design_filter('lowpass', 4, 100, 1000, method='butterworth', plot=True)
    """
    # Validate inputs
    valid_types = ['lowpass', 'highpass', 'bandpass', 'bandstop']
    if filter_type not in valid_types:
        raise ValueError(f"filter_type must be one of {valid_types}")
    
    if fs <= 0:
        raise ValueError("Sampling frequency must be positive")
    
    if order < 1:
        raise ValueError("Filter order must be positive")
    
    # Normalize cutoff frequency
    nyquist = fs / 2
    if isinstance(cutoff, (list, tuple)):
        wn = [f / nyquist for f in cutoff]
        if any(f >= 0.99 for f in wn):
            raise ValueError("Cutoff frequency must be less than Nyquist frequency (fs/2)")
    else:
        wn = cutoff / nyquist
        if wn >= 0.99:
            raise ValueError("Cutoff frequency must be less than Nyquist frequency (fs/2)")
    
    # Design filter based on method
    method = method.lower()
    
    if method == 'butterworth':
        b, a = sp_signal.butter(order, wn, btype=filter_type, analog=False)
        
    elif method == 'chebyshev1':
        b, a = sp_signal.cheby1(order, ripple, wn, btype=filter_type, analog=False)
        
    elif method == 'chebyshev2':
        b, a = sp_signal.cheby2(order, stopband_atten, wn, btype=filter_type, analog=False)
        
    elif method == 'elliptic':
        b, a = sp_signal.ellip(order, ripple, stopband_atten, wn, btype=filter_type, analog=False)
        
    elif method == 'fir':
        # Use window method for FIR design
        if filter_type in ['lowpass', 'highpass']:
            b = sp_signal.firwin(order + 1, wn, window='hamming', pass_zero=(filter_type == 'lowpass'))
        else:  # bandpass or bandstop
            b = sp_signal.firwin(order + 1, wn, window='hamming', pass_zero=(filter_type == 'bandstop'))
        a = np.array([1.0])
        
    else:
        raise ValueError(f"Unknown filter method: {method}")
    
    # Plot if requested
    if plot:
        plot_frequency_response(b, a, fs, filter_type, method)
    
    return b, a


def apply_filter(signal: np.ndarray, b: np.ndarray, a: np.ndarray) -> np.ndarray:
    """
    Apply filter to signal.
    
    Args:
        signal: Input signal
        b: Numerator coefficients
        a: Denominator coefficients
        
    Returns:
        Filtered signal
        
    Example:
        >>> b, a = design_filter('lowpass', 4, 100, 1000)
        >>> filtered = apply_filter(noisy_signal, b, a)
    """
    return sp_signal.lfilter(b, a, signal)


def plot_frequency_response(
    b: np.ndarray,
    a: np.ndarray,
    fs: float,
    filter_type: str,
    method: str
) -> None:
    """
    Plot filter frequency response.
    
    Args:
        b: Numerator coefficients
        a: Denominator coefficients
        fs: Sampling frequency in Hz
        filter_type: Type of filter
        method: Design method
    """
    fig, (ax1, ax2) = plt.subplots(2, 1, figsize=(10, 8))
    
    # Compute frequency response
    w, h = sp_signal.freqz(b, a, worN=1024, fs=fs)
    
    # Magnitude response
    ax1.plot(w, 20 * np.log10(np.abs(h)), linewidth=2)
    ax1.grid(True, alpha=0.3)
    ax1.set_xlabel('Frequency (Hz)', fontsize=11)
    ax1.set_ylabel('Magnitude (dB)', fontsize=11)
    ax1.set_title(f'{method.capitalize()} {filter_type.capitalize()} Filter - Magnitude Response',
                  fontsize=12, fontweight='bold')
    ax1.set_ylim([-100, 5])
    
    # Phase response
    angles = np.unwrap(np.angle(h))
    ax2.plot(w, np.degrees(angles), linewidth=2)
    ax2.grid(True, alpha=0.3)
    ax2.set_xlabel('Frequency (Hz)', fontsize=11)
    ax2.set_ylabel('Phase (degrees)', fontsize=11)
    ax2.set_title('Phase Response', fontsize=12, fontweight='bold')
    
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    # Demo
    fs = 1000
    
    # Design Butterworth lowpass filter
    b, a = design_filter('lowpass', order=6, cutoff=150, fs=fs, method='butterworth', plot=True)
    
    # Generate test signal
    t = np.arange(0, 1, 1/fs)
    signal = np.sin(2*np.pi*50*t) + np.sin(2*np.pi*300*t)
    
    # Apply filter
    filtered = apply_filter(signal, b, a)
    
    print(f"Filter designed: {len(b)} coefficients (numerator), {len(a)} coefficients (denominator)")
    print(f"Signal filtered: {len(filtered)} samples")
