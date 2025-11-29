# Signal Processing Package

Professional signal processing library implementing FFT analysis, digital filter design, and communication system modulation/demodulation schemes in both MATLAB and Python.

## Overview

This project provides a comprehensive toolkit for digital signal processing and communications engineering. It includes implementations of:

- **Spectrum Analysis**: FFT-based frequency domain analysis with windowing functions
- **Digital Filter Design**: IIR (Butterworth, Chebyshev, Elliptic) and FIR filter design
- **Signal Generation**: Test signal generators (sine, chirp, noise, composite signals)
- **Analog Modulation**: AM and FM modulation/demodulation
- **Digital Modulation**: BPSK, QPSK, 16-QAM, and 64-QAM with BER analysis
- **Complete Communication Systems**: End-to-end transmission with noise and performance analysis

## Features

### MATLAB Implementation
- Object-oriented architecture with modular functions
- Comprehensive plotting and visualization
- Optimized for performance with vectorized operations
- Complete documentation with mathematical formulas

### Python Implementation
- NumPy/SciPy-based efficient computations
- Matplotlib visualizations
- Type hints for better code clarity
- Compatible with Python 3.8+

## Project Structure

```
signal-processing/
├── matlab/
│   ├── spectrum/
│   │   └── analyze_spectrum.m          # FFT analysis
│   ├── filters/
│   │   └── design_filter.m             # Digital filter design
│   ├── modulation/
│   │   ├── modulate_signal.m           # Modulation schemes
│   │   └── demodulate_signal.m         # Demodulation with BER
│   ├── utils/
│   │   └── generate_signal.m           # Test signal generation
│   └── main_demo.m                     # Complete demonstration
├── python/
│   ├── signal_processing/
│   │   ├── __init__.py
│   │   ├── spectrum.py                 # FFT analysis
│   │   ├── filters.py                  # Filter design
│   │   ├── modulation.py               # Modulation schemes
│   │   ├── demodulation.py             # Demodulation with BER
│   │   └── signal_generator.py         # Test signals
│   ├── demo.py                         # Python demonstration
│   └── requirements.txt
└── README.md
```

## Installation

### MATLAB Requirements
- MATLAB R2019b or later
- Signal Processing Toolbox

### Python Requirements
```bash
pip install -r python/requirements.txt
```

Dependencies:
- numpy >= 1.20.0
- scipy >= 1.7.0
- matplotlib >= 3.4.0

## Usage

### MATLAB Quick Start

```matlab
% Run complete demonstration
cd matlab
main_demo

% Individual examples

% 1. Spectrum Analysis
[signal, t] = generate_signal('composite', 1000, 1, 'composite_freqs', [50, 120, 300]);
[fft_result, freq, mag_db] = analyze_spectrum(signal, 1000, 'window', 'hann', 'plot', true);

% 2. Filter Design
[b, a] = design_filter('lowpass', 6, 150, 1000, 'method', 'butterworth', 'plot', true);
filtered = filter(b, a, signal);

% 3. Modulation
[message, t] = generate_signal('sine', 10000, 0.1, 'frequency', 10);
[mod_sig, t, carrier] = modulate_signal(message, 10000, 1000, 'AM', 'mod_index', 0.8, 'plot', true);

% 4. Demodulation with BER
bits = randi([0, 1], 1, 100);
[bpsk_sig, ~, ~] = modulate_signal(bits, 10000, 1000, 'BPSK');
[demod_bits, ber] = demodulate_signal(bpsk_sig, 10000, 1000, 'BPSK', bits, 'plot', true);
```

### Python Quick Start

```python
from signal_processing import *
import numpy as np

# 1. Spectrum Analysis
signal, t = generate_signal('composite', fs=1000, duration=1, composite_freqs=[50, 120, 300])
fft_result, freq, mag_db = analyze_spectrum(signal, fs=1000, window='hann', plot=True)

# 2. Filter Design
b, a = design_filter('lowpass', order=6, cutoff=150, fs=1000, method='butterworth', plot=True)
filtered = apply_filter(signal, b, a)

# 3. Modulation
message, t = generate_signal('sine', fs=10000, duration=0.1, frequency=10)
mod_sig, t, carrier = modulate_signal(message, fs=10000, fc=1000, mod_type='AM', mod_index=0.8, plot=True)

# 4. Demodulation with BER
bits = np.random.randint(0, 2, 100)
bpsk_sig, _, _ = modulate_signal(bits, fs=10000, fc=1000, mod_type='BPSK')
demod_bits, ber = demodulate_signal(bpsk_sig, fs=10000, fc=1000, mod_type='BPSK', 
                                     reference_bits=bits, plot=True)
print(f"BER: {ber:.4f}")
```

## Mathematical Background

### FFT Spectrum Analysis

The Discrete Fourier Transform (DFT) is computed using FFT algorithm:

$$X[k] = \sum_{n=0}^{N-1} x[n] e^{-j2\pi kn/N}$$

Power Spectrum Density in dB:

$$P_{dB}[k] = 20 \log_{10}(|X[k]|)$$

### Digital Filter Design

**Butterworth Filter**: Maximally flat magnitude response

$$|H(j\omega)|^2 = \frac{1}{1 + \left(\frac{\omega}{\omega_c}\right)^{2n}}$$

**Chebyshev Type I**: Equiripple passband, monotonic stopband

**Chebyshev Type II**: Monotonic passband, equiripple stopband

**Elliptic (Cauer)**: Equiripple both passband and stopband

### Modulation Schemes

**AM (Amplitude Modulation)**:
$$s(t) = A_c[1 + m \cdot m(t)]\cos(2\pi f_c t)$$

**FM (Frequency Modulation)**:
$$s(t) = A_c\cos(2\pi f_c t + 2\pi k_f \int m(t)dt)$$

**BPSK (Binary Phase Shift Keying)**:
- Bit 0: $s(t) = A\cos(2\pi f_c t)$
- Bit 1: $s(t) = -A\cos(2\pi f_c t)$

**QPSK (Quadrature Phase Shift Keying)**:
$$s(t) = I(t)\cos(2\pi f_c t) + Q(t)\sin(2\pi f_c t)$$

**M-QAM (Quadrature Amplitude Modulation)**:
$$s(t) = A_I(t)\cos(2\pi f_c t) + A_Q(t)\sin(2\pi f_c t)$$

### Bit Error Rate (BER)

Theoretical BER for BPSK in AWGN channel:

$$BER_{BPSK} = \frac{1}{2}\text{erfc}\left(\sqrt{\frac{E_b}{N_0}}\right)$$

For QPSK:

$$BER_{QPSK} \approx \text{erfc}\left(\sqrt{\frac{E_b}{N_0}}\right)$$

## Examples

### Complete Communication System (MATLAB)

```matlab
% Complete QPSK Communication System with BER vs SNR Analysis
fs = 10000;
fc = 1000;
num_bits = 1000;
tx_bits = randi([0, 1], 1, num_bits);

% Modulate
[tx_signal, ~, ~] = modulate_signal(tx_bits, fs, fc, 'QPSK');

% Test different SNR levels
snr_levels = [0, 5, 10, 15, 20, 25, 30];
ber_results = zeros(size(snr_levels));

for i = 1:length(snr_levels)
    signal_power = mean(tx_signal.^2);
    noise_power = signal_power / (10^(snr_levels(i)/10));
    rx_signal = tx_signal + sqrt(noise_power) * randn(size(tx_signal));
    
    [rx_bits, ~] = demodulate_signal(rx_signal, fs, fc, 'QPSK', tx_bits);
    
    min_len = min(length(tx_bits), length(rx_bits));
    ber_results(i) = sum(tx_bits(1:min_len) ~= rx_bits(1:min_len)) / min_len;
end

% Plot BER performance
semilogy(snr_levels, ber_results, 'b-o', 'LineWidth', 2);
grid on;
xlabel('SNR (dB)');
ylabel('Bit Error Rate (BER)');
title('QPSK BER Performance vs SNR');
```

### Digital Filter Application (Python)

```python
import numpy as np
import matplotlib.pyplot as plt
from signal_processing import generate_signal, design_filter, apply_filter, analyze_spectrum

# Generate noisy composite signal
fs = 1000
signal, t = generate_signal('composite', fs, 1, composite_freqs=[50, 120, 300])
noise, _ = generate_signal('white_noise', fs, 1, amplitude=0.3)
noisy_signal = signal + noise

# Design lowpass filter to keep only 50 Hz component
b, a = design_filter('lowpass', order=6, cutoff=80, fs=fs, method='butterworth', plot=True)

# Apply filter
filtered_signal = apply_filter(noisy_signal, b, a)

# Compare spectra
fig, axes = plt.subplots(2, 1, figsize=(12, 8))

# Original noisy signal spectrum
_, freq_noisy, mag_noisy = analyze_spectrum(noisy_signal, fs, window='hann')
axes[0].plot(freq_noisy[freq_noisy >= 0], mag_noisy[freq_noisy >= 0])
axes[0].set_title('Noisy Signal Spectrum')
axes[0].grid(True)

# Filtered signal spectrum
_, freq_clean, mag_clean = analyze_spectrum(filtered_signal, fs, window='hann')
axes[1].plot(freq_clean[freq_clean >= 0], mag_clean[freq_clean >= 0])
axes[1].set_title('Filtered Signal Spectrum (Lowpass 80 Hz)')
axes[1].grid(True)

plt.tight_layout()
plt.show()
```

## Performance Analysis

### Filter Characteristics

| Method | Passband | Stopband | Transition | Phase |
|--------|----------|----------|------------|-------|
| Butterworth | Maximally flat | Monotonic | Moderate | Near-linear |
| Chebyshev I | Equiripple | Monotonic | Sharper | Nonlinear |
| Chebyshev II | Monotonic | Equiripple | Sharper | Nonlinear |
| Elliptic | Equiripple | Equiripple | Sharpest | Most nonlinear |
| FIR | Flexible | Flexible | Depends on order | Linear |

### Modulation Spectral Efficiency

| Modulation | Bits/Symbol | Spectral Efficiency | Complexity |
|------------|-------------|---------------------|------------|
| BPSK | 1 | Low | Simple |
| QPSK | 2 | Medium | Moderate |
| 16-QAM | 4 | High | Complex |
| 64-QAM | 6 | Very High | Very Complex |

## References

1. Oppenheim, A. V., & Schafer, R. W. (2010). *Discrete-Time Signal Processing* (3rd ed.). Pearson Education.

2. Proakis, J. G., & Salehi, M. (2008). *Digital Communications* (5th ed.). McGraw-Hill.

3. Parks, T. W., & Burrus, C. S. (1987). *Digital Filter Design*. John Wiley & Sons.

4. Sklar, B. (2001). *Digital Communications: Fundamentals and Applications* (2nd ed.). Prentice Hall.

5. IEEE Standard 181-2003: *IEEE Standard on Transitions, Pulses, and Related Waveforms*.

## License

MIT License - See LICENSE file for details

## Author

Signal Processing Package - 2025

## Contributing

Contributions are welcome. Please ensure code follows existing style conventions and includes appropriate documentation and tests.

## Limitations

- **Synchronization**: Demodulators assume perfect carrier synchronization
- **Channel Models**: Only AWGN channel considered
- **Timing Recovery**: Symbol timing assumed perfect
- **Equalization**: No channel equalization implemented
- **Coding**: No error correction coding (FEC) included

For production systems, additional components (carrier recovery, timing synchronization, channel estimation, FEC) are required.

## Future Enhancements

- [ ] Carrier and timing synchronization algorithms
- [ ] Channel estimation and equalization
- [ ] Forward Error Correction (FEC) codes
- [ ] Multi-carrier modulation (OFDM)
- [ ] Adaptive filtering
- [ ] Real-time processing capabilities
