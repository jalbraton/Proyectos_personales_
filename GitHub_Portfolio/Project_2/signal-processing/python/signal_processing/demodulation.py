"""
Demodulation Module
===================

Demodulation for analog and digital modulation schemes with BER analysis.

Functions:
    demodulate_signal: Demodulate modulated signals and calculate BER

Example:
    >>> from signal_processing.demodulation import demodulate_signal
    >>> demod_bits, ber = demodulate_signal(rx_signal, fs=10000, fc=1000, 
    ...                                      mod_type='BPSK', reference_bits=original_bits)
"""

import numpy as np
from scipy import signal as sp_signal
import matplotlib.pyplot as plt
from typing import Tuple, Optional


def demodulate_signal(
    modulated_signal: np.ndarray,
    fs: float,
    fc: float,
    mod_type: str,
    reference_bits: Optional[np.ndarray] = None,
    freq_deviation: Optional[float] = None,
    plot: bool = False
) -> Tuple[np.ndarray, float]:
    """
    Demodulate various modulated signals.
    
    Args:
        modulated_signal: Received modulated signal
        fs: Sampling frequency in Hz
        fc: Carrier frequency in Hz
        mod_type: 'AM', 'FM', 'BPSK', 'QPSK', '16QAM', or '64QAM'
        reference_bits: Original bits for BER calculation (optional)
        freq_deviation: Frequency deviation for FM in Hz
        plot: Generate constellation diagram
        
    Returns:
        Tuple (demodulated, ber):
            - demodulated: Demodulated signal or bits
            - ber: Bit Error Rate (NaN if no reference)
            
    Example:
        >>> demod_bits, ber = demodulate_signal(rx_signal, 10000, 1000, 'BPSK', original_bits)
    """
    if freq_deviation is None:
        freq_deviation = fc / 10
    
    symbols = None
    mod_type = mod_type.upper()
    
    # Demodulate based on type
    if mod_type == 'AM':
        demodulated = _demodulate_am(modulated_signal, fs, fc)
        
    elif mod_type == 'FM':
        demodulated = _demodulate_fm(modulated_signal, fs, fc, freq_deviation)
        
    elif mod_type == 'BPSK':
        demodulated, symbols = _demodulate_bpsk(modulated_signal, fs, fc)
        
    elif mod_type == 'QPSK':
        demodulated, symbols = _demodulate_qpsk(modulated_signal, fs, fc)
        
    elif mod_type == '16QAM':
        demodulated, symbols = _demodulate_qam(modulated_signal, fs, fc, M=16)
        
    elif mod_type == '64QAM':
        demodulated, symbols = _demodulate_qam(modulated_signal, fs, fc, M=64)
        
    else:
        raise ValueError(f"Unknown modulation type: {mod_type}")
    
    # Calculate BER if reference provided
    if reference_bits is not None and mod_type not in ['AM', 'FM']:
        min_len = min(len(reference_bits), len(demodulated))
        ref_binary = (reference_bits[:min_len] > np.mean(reference_bits)).astype(int)
        demod_binary = demodulated[:min_len].astype(int)
        ber = np.sum(ref_binary != demod_binary) / min_len
    else:
        ber = np.nan
    
    # Plot constellation if applicable
    if plot and symbols is not None:
        _plot_constellation(symbols, mod_type)
    
    return demodulated, ber


def _demodulate_am(modulated_signal: np.ndarray, fs: float, fc: float) -> np.ndarray:
    """AM Demodulation using envelope detection."""
    # Rectification
    rectified = np.abs(modulated_signal)
    
    # Low-pass filter to extract envelope
    cutoff = fc / 10
    b, a = sp_signal.butter(6, cutoff / (fs/2), btype='low')
    envelope = sp_signal.lfilter(b, a, rectified)
    
    # Remove DC component
    return envelope - np.mean(envelope)


def _demodulate_fm(modulated_signal: np.ndarray, fs: float, fc: float, freq_dev: float) -> np.ndarray:
    """FM Demodulation using differentiation and envelope detection."""
    # Differentiate
    differentiated = np.diff(modulated_signal, prepend=0)
    
    # Envelope detection using Hilbert transform
    analytic = sp_signal.hilbert(differentiated)
    envelope = np.abs(analytic)
    
    # Low-pass filter
    cutoff = freq_dev * 2
    b, a = sp_signal.butter(6, cutoff / (fs/2), btype='low')
    demodulated = sp_signal.lfilter(b, a, envelope)
    
    # Remove DC and normalize (avoid division by zero)
    demodulated = demodulated - np.mean(demodulated)
    max_val = np.max(np.abs(demodulated))
    if max_val > 0:
        demodulated = demodulated / max_val
    
    return demodulated


def _demodulate_bpsk(modulated_signal: np.ndarray, fs: float, fc: float) -> Tuple[np.ndarray, np.ndarray]:
    """BPSK Demodulation using coherent detection."""
    # Generate local carrier
    t = np.arange(len(modulated_signal)) / fs
    carrier = np.cos(2 * np.pi * fc * t)
    
    # Multiply by carrier
    product = modulated_signal * carrier
    
    # Low-pass filter
    b, a = sp_signal.butter(6, fc / (fs/2), btype='low')
    filtered = sp_signal.lfilter(b, a, product)
    
    # Downsample to symbol rate
    symbols_per_bit = max(1, int(fs / (fc * 0.1)))
    downsampled = filtered[::symbols_per_bit]
    
    symbols = downsampled
    bits = (downsampled > 0).astype(int)
    
    return bits, symbols


def _demodulate_qpsk(modulated_signal: np.ndarray, fs: float, fc: float) -> Tuple[np.ndarray, np.ndarray]:
    """QPSK Demodulation using coherent detection."""
    # Generate local carriers
    t = np.arange(len(modulated_signal)) / fs
    carrier_I = np.cos(2 * np.pi * fc * t)
    carrier_Q = -np.sin(2 * np.pi * fc * t)
    
    # Multiply by carriers
    I_product = modulated_signal * carrier_I
    Q_product = modulated_signal * carrier_Q
    
    # Low-pass filter
    b, a = sp_signal.butter(6, fc / (fs/2), btype='low')
    I_filtered = sp_signal.lfilter(b, a, I_product)
    Q_filtered = sp_signal.lfilter(b, a, Q_product)
    
    # Downsample
    symbols_per_bit = max(1, int(fs / (fc * 0.2)))
    I_down = I_filtered[::symbols_per_bit]
    Q_down = Q_filtered[::symbols_per_bit]
    
    symbols = I_down + 1j * Q_down
    
    # Decision for each symbol
    bits = np.zeros(2 * len(I_down), dtype=int)
    for i in range(len(I_down)):
        bits[2*i] = int(I_down[i] < 0)
        bits[2*i + 1] = int(Q_down[i] < 0)
    
    return bits, symbols


def _demodulate_qam(modulated_signal: np.ndarray, fs: float, fc: float, M: int) -> Tuple[np.ndarray, np.ndarray]:
    """QAM Demodulation for M-QAM."""
    # Generate local carriers
    t = np.arange(len(modulated_signal)) / fs
    carrier_I = np.cos(2 * np.pi * fc * t)
    carrier_Q = -np.sin(2 * np.pi * fc * t)
    
    # Multiply by carriers
    I_product = modulated_signal * carrier_I
    Q_product = modulated_signal * carrier_Q
    
    # Low-pass filter
    b, a = sp_signal.butter(6, fc / (fs/2), btype='low')
    I_filtered = sp_signal.lfilter(b, a, I_product)
    Q_filtered = sp_signal.lfilter(b, a, Q_product)
    
    # Downsample
    symbols_per_bit = max(1, int(fs / (fc * 0.2)))
    I_down = I_filtered[::symbols_per_bit]
    Q_down = Q_filtered[::symbols_per_bit]
    
    symbols = I_down + 1j * Q_down
    
    # Define constellation
    if M == 16:
        constellation = np.array([
            -3-3j, -3-1j, -3+1j, -3+3j,
            -1-3j, -1-1j, -1+1j, -1+3j,
             1-3j,  1-1j,  1+1j,  1+3j,
             3-3j,  3-1j,  3+1j,  3+3j
        ]) / np.sqrt(10)
        bits_per_symbol = 4
    elif M == 64:
        constellation_1d = np.array([-7, -5, -3, -1, 1, 3, 5, 7]) / np.sqrt(42)
        constellation = np.array([i + 1j*q for i in constellation_1d for q in constellation_1d])
        bits_per_symbol = 6
    else:
        raise ValueError(f"Unsupported QAM order: {M}")
    
    # Decode symbols to bits
    if len(symbols) == 0:
        return np.array([], dtype=int), symbols
    
    bits = np.zeros(len(symbols) * bits_per_symbol, dtype=int)
    
    for i, symbol in enumerate(symbols):
        # Find closest constellation point
        distances = np.abs(symbol - constellation)
        idx = np.argmin(distances)
        
        # Convert index to bits
        bit_string = format(idx, f'0{bits_per_symbol}b')
        bits[i*bits_per_symbol:(i+1)*bits_per_symbol] = [int(b) for b in bit_string]
    
    return bits, symbols


def _plot_constellation(symbols: np.ndarray, mod_type: str) -> None:
    """Plot constellation diagram."""
    plt.figure(figsize=(8, 8))
    
    # Plot received symbols
    plt.plot(np.real(symbols), np.imag(symbols), 'b.', markersize=8, alpha=0.6, label='Received Symbols')
    
    # Plot ideal constellation
    if mod_type == 'QPSK':
        ideal = np.array([1+1j, -1+1j, -1-1j, 1-1j]) / np.sqrt(2)
        plt.plot(np.real(ideal), np.imag(ideal), 'r*', markersize=15, linewidth=2, label='Ideal Constellation')
        
    elif mod_type == '16QAM':
        ideal = np.array([
            -3-3j, -3-1j, -3+1j, -3+3j,
            -1-3j, -1-1j, -1+1j, -1+3j,
             1-3j,  1-1j,  1+1j,  1+3j,
             3-3j,  3-1j,  3+1j,  3+3j
        ]) / np.sqrt(10)
        plt.plot(np.real(ideal), np.imag(ideal), 'r*', markersize=15, linewidth=2, label='Ideal Constellation')
    
    plt.grid(True, alpha=0.3)
    plt.axis('equal')
    plt.xlabel('In-Phase', fontsize=12)
    plt.ylabel('Quadrature', fontsize=12)
    plt.title(f'{mod_type} Constellation Diagram', fontsize=14, fontweight='bold')
    plt.legend()
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    # Demo
    from signal_processing.modulation import modulate_signal
    
    fs = 10000
    fc = 1000
    
    # Generate bits
    bits = np.random.randint(0, 2, 100)
    
    # Modulate
    mod_signal, _, _ = modulate_signal(bits, fs, fc, 'BPSK')
    
    # Add noise
    snr_db = 15
    signal_power = np.mean(mod_signal ** 2)
    noise_power = signal_power / (10 ** (snr_db / 10))
    noisy_signal = mod_signal + np.sqrt(noise_power) * np.random.randn(len(mod_signal))
    
    # Demodulate
    demod_bits, ber = demodulate_signal(noisy_signal, fs, fc, 'BPSK', bits, plot=True)
    
    print(f"BPSK Demodulation Test:")
    print(f"  SNR: {snr_db} dB")
    print(f"  BER: {ber:.4f}")
    print(f"  Errors: {np.sum(bits != demod_bits[:len(bits)])} / {len(bits)}")
