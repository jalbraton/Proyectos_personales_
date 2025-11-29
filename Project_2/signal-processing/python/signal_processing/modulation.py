"""
Modulation Module
=================

Analog and digital modulation schemes implementation.

Functions:
    modulate_signal: Apply modulation (AM, FM, BPSK, QPSK, QAM)

Example:
    >>> from signal_processing.modulation import modulate_signal
    >>> modulated, t, carrier = modulate_signal(message, fs=10000, fc=1000, mod_type='AM')
"""

import numpy as np
import matplotlib.pyplot as plt
from typing import Tuple, Optional


def modulate_signal(
    message: np.ndarray,
    fs: float,
    fc: float,
    mod_type: str,
    mod_index: float = 1.0,
    freq_deviation: Optional[float] = None,
    plot: bool = False
) -> Tuple[np.ndarray, np.ndarray, np.ndarray]:
    """
    Apply various modulation schemes to message signal.
    
    Args:
        message: Message signal (baseband)
        fs: Sampling frequency in Hz
        fc: Carrier frequency in Hz
        mod_type: 'AM', 'FM', 'BPSK', 'QPSK', '16QAM', or '64QAM'
        mod_index: Modulation index for AM/FM
        freq_deviation: Frequency deviation for FM in Hz
        plot: Generate modulation plot
        
    Returns:
        Tuple (modulated_signal, t, carrier):
            - modulated_signal: Modulated signal
            - t: Time vector
            - carrier: Carrier signal
            
    Example:
        >>> message = np.sin(2*np.pi*10*np.arange(0, 0.1, 1/10000))
        >>> mod_sig, t, carrier = modulate_signal(message, 10000, 1000, 'AM', mod_index=0.5)
    """
    if freq_deviation is None:
        freq_deviation = fc / 10
    
    # Generate time vector
    N = len(message)
    t = np.arange(N) / fs
    
    # Generate carrier
    carrier = np.cos(2 * np.pi * fc * t)
    
    # Modulate based on type
    mod_type = mod_type.upper()
    
    if mod_type == 'AM':
        modulated = _modulate_am(message, carrier, mod_index)
        
    elif mod_type == 'FM':
        modulated = _modulate_fm(message, fs, fc, freq_deviation)
        
    elif mod_type == 'BPSK':
        modulated = _modulate_bpsk(message, carrier)
        
    elif mod_type == 'QPSK':
        modulated = _modulate_qpsk(message, carrier, fs, fc)
        
    elif mod_type == '16QAM':
        modulated = _modulate_qam(message, carrier, fs, fc, M=16)
        
    elif mod_type == '64QAM':
        modulated = _modulate_qam(message, carrier, fs, fc, M=64)
        
    else:
        raise ValueError(f"Unknown modulation type: {mod_type}")
    
    # Plot if requested
    if plot:
        _plot_modulation(message, modulated, t, mod_type, carrier)
    
    return modulated, t, carrier


def _modulate_am(message: np.ndarray, carrier: np.ndarray, mod_index: float) -> np.ndarray:
    """Amplitude Modulation."""
    # Normalize message to [-1, 1] (avoid division by zero)
    max_val = np.max(np.abs(message))
    if max_val > 0:
        message_norm = message / max_val
    else:
        message_norm = message
    
    # Apply AM: s(t) = Ac[1 + m*m(t)]cos(2*pi*fc*t)
    return (1 + mod_index * message_norm) * carrier


def _modulate_fm(message: np.ndarray, fs: float, fc: float, freq_dev: float) -> np.ndarray:
    """Frequency Modulation."""
    # Normalize message (avoid division by zero)
    max_val = np.max(np.abs(message))
    if max_val > 0:
        message_norm = message / max_val
    else:
        message_norm = message
    
    # Compute instantaneous phase
    kf = freq_dev  # Frequency sensitivity
    phase = 2 * np.pi * kf * np.cumsum(message_norm) / fs
    
    # Generate FM signal
    t = np.arange(len(message)) / fs
    return np.cos(2 * np.pi * fc * t + phase)


def _modulate_bpsk(bits: np.ndarray, carrier: np.ndarray) -> np.ndarray:
    """Binary Phase Shift Keying."""
    # Convert to binary
    bits_binary = (bits > np.mean(bits)).astype(int)
    
    # Map to {-1, +1}
    symbols = 2 * bits_binary - 1
    
    # Modulate
    return symbols * carrier


def _modulate_qpsk(bits: np.ndarray, carrier: np.ndarray, fs: float, fc: float) -> np.ndarray:
    """Quadrature Phase Shift Keying."""
    # Convert to binary
    bits_binary = (bits > np.mean(bits)).astype(int)
    
    # Upsample to carrier length
    symbols_per_bit = max(1, len(carrier) // len(bits))
    bits_upsampled = np.repeat(bits_binary, symbols_per_bit)[:len(carrier)]
    
    # Pad to even length
    if len(bits_upsampled) % 2 != 0:
        bits_upsampled = np.append(bits_upsampled, 0)
    
    # Map bit pairs to QPSK symbols
    I = np.zeros(len(bits_upsampled) // 2)
    Q = np.zeros(len(bits_upsampled) // 2)
    
    for i in range(0, len(bits_upsampled) - 1, 2):
        idx = i // 2
        if bits_upsampled[i] == 0 and bits_upsampled[i+1] == 0:
            I[idx], Q[idx] = 1, 1
        elif bits_upsampled[i] == 0 and bits_upsampled[i+1] == 1:
            I[idx], Q[idx] = -1, 1
        elif bits_upsampled[i] == 1 and bits_upsampled[i+1] == 1:
            I[idx], Q[idx] = -1, -1
        else:
            I[idx], Q[idx] = 1, -1
    
    # Upsample symbols to carrier rate
    I_upsampled = np.repeat(I, symbols_per_bit * 2)[:len(carrier)]
    Q_upsampled = np.repeat(Q, symbols_per_bit * 2)[:len(carrier)]
    
    # Generate quadrature carrier
    t = np.arange(len(carrier)) / fs
    carrier_Q = -np.sin(2 * np.pi * fc * t)
    
    # Modulate
    modulated = I_upsampled * carrier + Q_upsampled * carrier_Q
    return modulated / np.sqrt(2)


def _modulate_qam(bits: np.ndarray, carrier: np.ndarray, fs: float, fc: float, M: int) -> np.ndarray:
    """Quadrature Amplitude Modulation (M-QAM)."""
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
        # 64-QAM constellation
        constellation_1d = np.array([-7, -5, -3, -1, 1, 3, 5, 7]) / np.sqrt(42)
        constellation = np.array([i + 1j*q for i in constellation_1d for q in constellation_1d])
        bits_per_symbol = 6
    else:
        raise ValueError(f"Unsupported QAM order: {M}")
    
    # Convert to binary
    bits_binary = (bits > np.mean(bits)).astype(int)
    
    # Upsample
    symbols_per_bit = max(1, len(carrier) // len(bits))
    bits_upsampled = np.repeat(bits_binary, symbols_per_bit)[:len(carrier)]
    
    # Pad to multiple of bits_per_symbol
    remainder = len(bits_upsampled) % bits_per_symbol
    if remainder != 0:
        bits_upsampled = np.append(bits_upsampled, np.zeros(bits_per_symbol - remainder))
    
    # Map bits to symbols
    num_symbols = len(bits_upsampled) // bits_per_symbol
    symbols = np.zeros(num_symbols, dtype=complex)
    
    for i in range(num_symbols):
        bit_group = bits_upsampled[i*bits_per_symbol:(i+1)*bits_per_symbol]
        symbol_idx = int(''.join(map(str, bit_group.astype(int))), 2)
        symbols[i] = constellation[symbol_idx]
    
    # Upsample symbols
    samples_per_symbol = max(1, len(carrier) // num_symbols)
    I = np.real(symbols)
    Q = np.imag(symbols)
    I_upsampled = np.repeat(I, samples_per_symbol)[:len(carrier)]
    Q_upsampled = np.repeat(Q, samples_per_symbol)[:len(carrier)]
    
    # Pad if upsampled arrays are shorter than carrier
    if len(I_upsampled) < len(carrier):
        I_upsampled = np.pad(I_upsampled, (0, len(carrier) - len(I_upsampled)), mode='edge')
    if len(Q_upsampled) < len(carrier):
        Q_upsampled = np.pad(Q_upsampled, (0, len(carrier) - len(Q_upsampled)), mode='edge')
    
    # Generate quadrature carrier
    t = np.arange(len(carrier)) / fs
    carrier_Q = -np.sin(2 * np.pi * fc * t)
    
    # Modulate
    return I_upsampled * carrier + Q_upsampled * carrier_Q


def _plot_modulation(message: np.ndarray, modulated: np.ndarray, t: np.ndarray, 
                     mod_type: str, carrier: np.ndarray) -> None:
    """Plot modulation results."""
    n_samples = min(500, len(t))
    
    fig, axes = plt.subplots(3, 1, figsize=(12, 8))
    
    axes[0].plot(t[:n_samples], message[:n_samples], linewidth=1.5)
    axes[0].grid(True, alpha=0.3)
    axes[0].set_ylabel('Amplitude')
    axes[0].set_title('Message Signal', fontweight='bold')
    
    axes[1].plot(t[:n_samples], carrier[:n_samples], linewidth=1)
    axes[1].grid(True, alpha=0.3)
    axes[1].set_ylabel('Amplitude')
    axes[1].set_title('Carrier Signal', fontweight='bold')
    
    axes[2].plot(t[:n_samples], modulated[:n_samples], linewidth=1)
    axes[2].grid(True, alpha=0.3)
    axes[2].set_xlabel('Time (s)')
    axes[2].set_ylabel('Amplitude')
    axes[2].set_title(f'{mod_type} Modulated Signal', fontweight='bold')
    
    plt.tight_layout()
    plt.show()


if __name__ == "__main__":
    # Demo
    fs = 10000
    t = np.arange(0, 0.1, 1/fs)
    message = np.sin(2*np.pi*10*t)
    
    # Test AM
    mod_am, t_am, carrier = modulate_signal(message, fs, 1000, 'AM', mod_index=0.8, plot=True)
    print(f"AM modulation: {len(mod_am)} samples generated")
    
    # Test BPSK
    bits = np.random.randint(0, 2, 100)
    mod_bpsk, _, _ = modulate_signal(bits, fs, 1000, 'BPSK', plot=True)
    print(f"BPSK modulation: {len(mod_bpsk)} samples generated")
