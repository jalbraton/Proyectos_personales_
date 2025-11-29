"""
Complete Signal Processing Demonstration
=========================================

Demonstrates all features of the signal processing package:
1. Signal generation
2. Spectrum analysis (FFT)
3. Digital filter design
4. Analog modulation (AM, FM)
5. Digital modulation (BPSK, QPSK, 16-QAM)
6. Demodulation and BER analysis
7. Complete communication system

Author: Signal Processing Package
Date: 2025
"""

import numpy as np
import matplotlib.pyplot as plt
from signal_processing import (
    generate_signal,
    analyze_spectrum,
    design_filter,
    apply_filter,
    modulate_signal,
    demodulate_signal
)

print("=" * 60)
print("Signal Processing Package - Python Demonstration")
print("=" * 60)
print()

# Parameters
fs = 10000  # Sampling frequency (Hz)
fc = 1000   # Carrier frequency (Hz)

# ============================================================
# 1. SIGNAL GENERATION
# ============================================================
print("1. Signal Generation")
print("-" * 60)

# Generate various signals
sine_signal, t = generate_signal('sine', fs, 1, frequency=50, amplitude=1)
chirp_signal, _ = generate_signal('chirp', fs, 1, f0=0, f1=1000)
composite_signal, _ = generate_signal('composite', fs, 1, composite_freqs=[50, 120, 300])
noise_signal, _ = generate_signal('white_noise', fs, 1, amplitude=0.5)

print("✓ Generated signals:")
print("  - Pure sine wave (50 Hz)")
print("  - Chirp signal (0-1000 Hz)")
print("  - Composite signal (50, 120, 300 Hz)")
print("  - White noise")
print()

# ============================================================
# 2. SPECTRUM ANALYSIS
# ============================================================
print("2. Spectrum Analysis (FFT)")
print("-" * 60)

# Analyze spectrum of composite signal
fft_result, frequencies, magnitude_db = analyze_spectrum(
    composite_signal, fs, window='hann', plot=True
)

print("✓ Spectrum analysis completed for composite signal")
print(f"  Window: Hann")
print(f"  Frequency resolution: {fs/len(composite_signal):.2f} Hz")
print()

# ============================================================
# 3. DIGITAL FILTER DESIGN
# ============================================================
print("3. Digital Filter Design")
print("-" * 60)

# Design Butterworth lowpass filter
fc_low = 150
order = 6
b_butter, a_butter = design_filter(
    'lowpass', order, fc_low, fs, method='butterworth', plot=True
)

print("✓ Designed Butterworth lowpass filter:")
print(f"  Order: {order}")
print(f"  Cutoff frequency: {fc_low} Hz")
print(f"  Method: Butterworth")
print()

# Apply filter
filtered_signal = apply_filter(composite_signal, b_butter, a_butter)

# Design bandpass filter
fc_band = (80, 200)
b_band, a_band = design_filter(
    'bandpass', 4, fc_band, fs, method='chebyshev1', ripple=0.5, plot=True
)

print("✓ Designed Chebyshev Type I bandpass filter:")
print(f"  Order: 4")
print(f"  Passband: {fc_band[0]}-{fc_band[1]} Hz")
print(f"  Ripple: 0.5 dB")
print()

# ============================================================
# 4. ANALOG MODULATION
# ============================================================
print("4. Analog Modulation (AM, FM)")
print("-" * 60)

# Message signal
fm = 10  # Message frequency (Hz)
message, t_mod = generate_signal('sine', fs, 0.1, frequency=fm)

# AM Modulation
am_signal, _, _ = modulate_signal(
    message, fs, fc, 'AM', mod_index=0.8, plot=True
)
print("✓ AM modulation: mod_index = 0.8")

# FM Modulation
fm_signal, _, _ = modulate_signal(
    message, fs, fc, 'FM', freq_deviation=100, plot=True
)
print("✓ FM modulation: freq_deviation = 100 Hz")
print()

# ============================================================
# 5. DIGITAL MODULATION
# ============================================================
print("5. Digital Modulation (BPSK, QPSK, 16-QAM)")
print("-" * 60)

# Generate random bits
num_bits = 100
bits = np.random.randint(0, 2, num_bits)

# BPSK Modulation
bpsk_signal, _, _ = modulate_signal(bits, fs, fc, 'BPSK', plot=True)
print(f"✓ BPSK modulation: {num_bits} bits")

# QPSK Modulation
qpsk_signal, _, _ = modulate_signal(bits, fs, fc, 'QPSK', plot=True)
print(f"✓ QPSK modulation: {num_bits} bits")

# 16-QAM Modulation
qam16_signal, _, _ = modulate_signal(bits, fs, fc, '16QAM', plot=True)
print(f"✓ 16-QAM modulation: {num_bits} bits")
print()

# ============================================================
# 6. DEMODULATION AND BER ANALYSIS
# ============================================================
print("6. Demodulation and BER Analysis")
print("-" * 60)

# Add noise to signals
snr_db = 15

# BPSK with noise
signal_power = np.mean(bpsk_signal ** 2)
noise_power = signal_power / (10 ** (snr_db / 10))
bpsk_noisy = bpsk_signal + np.sqrt(noise_power) * np.random.randn(len(bpsk_signal))

# Demodulate BPSK
demod_bits_bpsk, ber_bpsk = demodulate_signal(
    bpsk_noisy, fs, fc, 'BPSK', bits, plot=True
)
print("✓ BPSK Demodulation:")
print(f"  SNR: {snr_db} dB")
print(f"  BER: {ber_bpsk:.4f}")
print(f"  Errors: {np.sum(bits != demod_bits_bpsk[:len(bits)])} / {len(bits)} bits")
print()

# QPSK with noise
qpsk_noisy = qpsk_signal + np.sqrt(noise_power) * np.random.randn(len(qpsk_signal))

# Demodulate QPSK
demod_bits_qpsk, ber_qpsk = demodulate_signal(
    qpsk_noisy, fs, fc, 'QPSK', bits, plot=True
)
print("✓ QPSK Demodulation:")
print(f"  SNR: {snr_db} dB")
print(f"  BER: {ber_qpsk:.4f}")
print(f"  Errors: {np.sum(bits != demod_bits_qpsk[:len(bits)])} / {len(bits)} bits")
print()

# 16-QAM with noise
qam16_noisy = qam16_signal + np.sqrt(noise_power) * np.random.randn(len(qam16_signal))

# Demodulate 16-QAM
demod_bits_qam, ber_qam = demodulate_signal(
    qam16_noisy, fs, fc, '16QAM', bits, plot=True
)
print("✓ 16-QAM Demodulation:")
print(f"  SNR: {snr_db} dB")
print(f"  BER: {ber_qam:.4f}")
print(f"  Errors: {np.sum(bits != demod_bits_qam[:len(bits)])} / {len(bits)} bits")
print()

# ============================================================
# 7. COMPLETE COMMUNICATION SYSTEM
# ============================================================
print("7. Complete Communication System (BER vs SNR)")
print("-" * 60)

# Generate longer bit sequence
num_bits_system = 1000
tx_bits = np.random.randint(0, 2, num_bits_system)

# Modulate with QPSK
tx_signal, _, _ = modulate_signal(tx_bits, fs, fc, 'QPSK')

# Test different SNR levels
snr_levels = np.array([0, 5, 10, 15, 20, 25, 30])
ber_results = np.zeros(len(snr_levels))

print("Testing BER performance at different SNR levels:")
for i, snr in enumerate(snr_levels):
    # Add noise
    signal_power = np.mean(tx_signal ** 2)
    noise_power = signal_power / (10 ** (snr / 10))
    rx_signal = tx_signal + np.sqrt(noise_power) * np.random.randn(len(tx_signal))
    
    # Demodulate
    rx_bits, _ = demodulate_signal(rx_signal, fs, fc, 'QPSK', tx_bits)
    
    # Calculate BER
    min_len = min(len(tx_bits), len(rx_bits))
    ber_results[i] = np.sum(tx_bits[:min_len] != rx_bits[:min_len]) / min_len
    
    print(f"  SNR = {snr:2d} dB: BER = {ber_results[i]:.6f}")

# Plot BER vs SNR
plt.figure(figsize=(10, 6))
plt.semilogy(snr_levels, ber_results, 'b-o', linewidth=2, markersize=8, label='Simulated')
plt.grid(True, alpha=0.3)
plt.xlabel('SNR (dB)', fontsize=12)
plt.ylabel('Bit Error Rate (BER)', fontsize=12)
plt.title('QPSK BER Performance vs SNR', fontsize=14, fontweight='bold')
plt.ylim([1e-5, 1])
plt.legend()
plt.tight_layout()
plt.show()

print()
print("=" * 60)
print("Demonstration Complete!")
print("=" * 60)
print()
print("All modules successfully tested:")
print("  ✓ Signal Generation")
print("  ✓ Spectrum Analysis (FFT)")
print("  ✓ Digital Filter Design")
print("  ✓ Analog Modulation (AM, FM)")
print("  ✓ Digital Modulation (BPSK, QPSK, 16-QAM)")
print("  ✓ Demodulation and BER Analysis")
print("  ✓ Complete Communication System")
print()
