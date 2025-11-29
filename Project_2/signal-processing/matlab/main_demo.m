%% MAIN DEMONSTRATION SCRIPT
% Signal Processing Package - Complete Demo
%
% This script demonstrates all functionalities of the signal processing package:
% 1. Signal Generation (sine, chirp, composite, noise)
% 2. Spectrum Analysis using FFT
% 3. Digital Filter Design (IIR and FIR filters)
% 4. Modulation Schemes (AM, FM, BPSK, QPSK, QAM)
% 5. Demodulation and BER Analysis
%
% Author: Signal Processing Package
% Date: 2025
% Reference: Oppenheim & Schafer (2010), Proakis & Salehi (2008)

clear; close all; clc;

fprintf('========================================\n');
fprintf('Signal Processing Package Demonstration\n');
fprintf('========================================\n\n');

%% 1. SIGNAL GENERATION
fprintf('1. Signal Generation\n');
fprintf('--------------------\n');

% Parameters
fs = 10000;          % Sampling frequency (Hz)
duration = 1;        % Duration (seconds)

% Generate various signals
[sine_signal, t] = generate_signal('sine', fs, duration, 'frequency', 50, 'amplitude', 1);
[chirp_signal, ~] = generate_signal('chirp', fs, duration, 'f0', 0, 'f1', 1000, 'amplitude', 1);
[composite_signal, ~] = generate_signal('composite', fs, duration, ...
    'composite_freqs', [50, 120, 300], 'amplitude', 1);
[noise_signal, ~] = generate_signal('white_noise', fs, duration, 'amplitude', 0.5);

% Add noise to sine signal
[noisy_sine, ~] = generate_signal('sine', fs, duration, 'frequency', 50, 'snr', 20);

fprintf('Generated signals:\n');
fprintf('  - Pure sine wave (50 Hz)\n');
fprintf('  - Chirp signal (0-1000 Hz)\n');
fprintf('  - Composite signal (50, 120, 300 Hz)\n');
fprintf('  - White noise\n');
fprintf('  - Noisy sine (SNR = 20 dB)\n\n');

%% 2. SPECTRUM ANALYSIS
fprintf('2. Spectrum Analysis (FFT)\n');
fprintf('--------------------------\n');

% Analyze spectrum of composite signal
[fft_result, frequencies, magnitude_db] = analyze_spectrum(composite_signal, fs, ...
    'window', 'hann', 'plot', true);

fprintf('Spectrum analysis completed for composite signal\n');
fprintf('  Window: Hann\n');
fprintf('  Frequency resolution: %.2f Hz\n\n', fs/length(composite_signal));

%% 3. DIGITAL FILTER DESIGN
fprintf('3. Digital Filter Design\n');
fprintf('------------------------\n');

% Design Butterworth lowpass filter
fc_low = 150; % Cutoff frequency
order = 6;
[b_butter, a_butter] = design_filter('lowpass', order, fc_low, fs, ...
    'method', 'butterworth', 'plot', true);

fprintf('Designed Butterworth lowpass filter:\n');
fprintf('  Order: %d\n', order);
fprintf('  Cutoff frequency: %d Hz\n', fc_low);
fprintf('  Method: Butterworth\n\n');

% Apply filter to composite signal
filtered_signal = filter(b_butter, a_butter, composite_signal);

% Design bandpass filter
fc_band = [80, 200];
[b_band, a_band] = design_filter('bandpass', 4, fc_band, fs, ...
    'method', 'chebyshev1', 'ripple', 0.5, 'plot', true);

fprintf('Designed Chebyshev Type I bandpass filter:\n');
fprintf('  Order: 4\n');
fprintf('  Passband: %d-%d Hz\n', fc_band(1), fc_band(2));
fprintf('  Ripple: 0.5 dB\n\n');

%% 4. MODULATION SCHEMES
fprintf('4. Modulation Schemes\n');
fprintf('---------------------\n');

% Message signal
fm = 10; % Message frequency (Hz)
[message, t_mod] = generate_signal('sine', fs, 0.1, 'frequency', fm);

% Carrier frequency
fc = 1000; % Carrier frequency (Hz)

% AM Modulation
[am_signal, ~, ~] = modulate_signal(message, fs, fc, 'AM', ...
    'mod_index', 0.8, 'plot', true);
fprintf('AM modulation: mod_index = 0.8\n');

% FM Modulation
[fm_signal, ~, ~] = modulate_signal(message, fs, fc, 'FM', ...
    'freq_deviation', 100, 'plot', true);
fprintf('FM modulation: freq_deviation = 100 Hz\n');

% Generate random bits for digital modulation
num_bits = 100;
bits = randi([0, 1], 1, num_bits);

% BPSK Modulation
[bpsk_signal, ~, ~] = modulate_signal(bits, fs, fc, 'BPSK', 'plot', true);
fprintf('BPSK modulation: %d bits\n', num_bits);

% QPSK Modulation
[qpsk_signal, ~, ~] = modulate_signal(bits, fs, fc, 'QPSK', 'plot', true);
fprintf('QPSK modulation: %d bits\n', num_bits);

% 16-QAM Modulation
[qam16_signal, ~, ~] = modulate_signal(bits, fs, fc, '16QAM', 'plot', true);
fprintf('16-QAM modulation: %d bits\n\n', num_bits);

%% 5. DEMODULATION AND BER ANALYSIS
fprintf('5. Demodulation and BER Analysis\n');
fprintf('--------------------------------\n');

% Add noise to BPSK signal
snr_db = 15;
noise = randn(size(bpsk_signal));
signal_power = mean(bpsk_signal.^2);
noise_power = signal_power / (10^(snr_db/10));
bpsk_noisy = bpsk_signal + sqrt(noise_power) * noise;

% Demodulate BPSK
[demod_bits_bpsk, ber_bpsk] = demodulate_signal(bpsk_noisy, fs, fc, 'BPSK', bits, 'plot', true);
fprintf('BPSK Demodulation:\n');
fprintf('  SNR: %d dB\n', snr_db);
fprintf('  BER: %.4f\n', ber_bpsk);
fprintf('  Errors: %d / %d bits\n\n', sum(bits ~= demod_bits_bpsk), length(bits));

% Add noise to QPSK signal
qpsk_noisy = qpsk_signal + sqrt(noise_power) * randn(size(qpsk_signal));

% Demodulate QPSK
[demod_bits_qpsk, ber_qpsk] = demodulate_signal(qpsk_noisy, fs, fc, 'QPSK', bits, 'plot', true);
fprintf('QPSK Demodulation:\n');
fprintf('  SNR: %d dB\n', snr_db);
fprintf('  BER: %.4f\n', ber_qpsk);
fprintf('  Errors: %d / %d bits\n\n', sum(bits ~= demod_bits_qpsk), length(bits));

% Add noise to 16-QAM signal
qam16_noisy = qam16_signal + sqrt(noise_power) * randn(size(qam16_signal));

% Demodulate 16-QAM
[demod_bits_qam, ber_qam] = demodulate_signal(qam16_noisy, fs, fc, '16QAM', bits, 'plot', true);
fprintf('16-QAM Demodulation:\n');
fprintf('  SNR: %d dB\n', snr_db);
fprintf('  BER: %.4f\n', ber_qam);
fprintf('  Errors: %d / %d bits\n\n', sum(bits ~= demod_bits_qam), length(bits));

%% 6. COMPLETE COMMUNICATION SYSTEM DEMO
fprintf('6. Complete Communication System\n');
fprintf('--------------------------------\n');

% Generate longer bit sequence
num_bits_system = 1000;
tx_bits = randi([0, 1], 1, num_bits_system);

% Modulate with QPSK
[tx_signal, ~, ~] = modulate_signal(tx_bits, fs, fc, 'QPSK');

% Simulate channel with different SNR levels
snr_levels = [0, 5, 10, 15, 20, 25, 30];
ber_results = zeros(size(snr_levels));

fprintf('Testing BER performance at different SNR levels:\n');
for i = 1:length(snr_levels)
    % Add noise
    signal_power = mean(tx_signal.^2);
    noise_power = signal_power / (10^(snr_levels(i)/10));
    rx_signal = tx_signal + sqrt(noise_power) * randn(size(tx_signal));
    
    % Demodulate
    [rx_bits, ~] = demodulate_signal(rx_signal, fs, fc, 'QPSK', tx_bits);
    
    % Calculate BER
    min_len = min(length(tx_bits), length(rx_bits));
    ber_results(i) = sum(tx_bits(1:min_len) ~= rx_bits(1:min_len)) / min_len;
    
    fprintf('  SNR = %2d dB: BER = %.6f\n', snr_levels(i), ber_results(i));
end

% Plot BER vs SNR
figure('Name', 'BER Performance');
semilogy(snr_levels, ber_results, 'b-o', 'LineWidth', 2, 'MarkerSize', 8);
grid on;
xlabel('SNR (dB)');
ylabel('Bit Error Rate (BER)');
title('QPSK BER Performance vs SNR');
ylim([1e-5, 1]);

fprintf('\n========================================\n');
fprintf('Demonstration Complete!\n');
fprintf('========================================\n');
fprintf('\nAll modules successfully tested:\n');
fprintf('  ✓ Signal Generation\n');
fprintf('  ✓ Spectrum Analysis (FFT)\n');
fprintf('  ✓ Digital Filter Design\n');
fprintf('  ✓ Modulation (AM, FM, BPSK, QPSK, 16-QAM)\n');
fprintf('  ✓ Demodulation and BER Analysis\n');
fprintf('  ✓ Complete Communication System\n\n');
