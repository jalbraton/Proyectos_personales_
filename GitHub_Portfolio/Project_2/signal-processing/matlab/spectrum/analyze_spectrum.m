function [fft_result, frequencies, magnitude_db] = analyze_spectrum(signal, fs, options)
% ANALYZE_SPECTRUM Perform FFT-based spectrum analysis
%
% Description:
%   Computes the Fast Fourier Transform of a signal and returns
%   frequency domain representation with magnitude and phase information.
%
% Syntax:
%   [fft_result, frequencies, magnitude_db] = analyze_spectrum(signal, fs)
%   [fft_result, frequencies, magnitude_db] = analyze_spectrum(signal, fs, options)
%
% Inputs:
%   signal - Input signal (time domain)
%   fs - Sampling frequency in Hz
%   options - Optional structure with fields:
%       .window - Window function ('hann', 'hamming', 'blackman', 'none')
%       .nfft - Number of FFT points (default: length(signal))
%       .plot - Boolean to generate plot (default: false)
%
% Outputs:
%   fft_result - Complex FFT result
%   frequencies - Frequency vector in Hz
%   magnitude_db - Magnitude spectrum in dB
%
% Example:
%   fs = 1000;
%   t = 0:1/fs:1-1/fs;
%   signal = sin(2*pi*50*t) + 0.5*sin(2*pi*120*t);
%   [fft_result, freq, mag_db] = analyze_spectrum(signal, fs);
%
% Reference:
%   Oppenheim, A. V., & Schafer, R. W. (2010). Discrete-Time Signal Processing.
%   Pearson Education.

% Input validation
arguments
    signal (1,:) double
    fs (1,1) double {mustBePositive}
    options.window (1,:) char = 'hann'
    options.nfft (1,1) double {mustBePositive} = length(signal)
    options.plot (1,1) logical = false
end

% Signal length
N = length(signal);

if N == 0
    error('Signal cannot be empty');
end

% Apply window function
switch lower(options.window)
    case 'hann'
        window = hann(N)';
    case 'hamming'
        window = hamming(N)';
    case 'blackman'
        window = blackman(N)';
    case 'none'
        window = ones(1, N);
    otherwise
        error('Unknown window function: %s', options.window);
end

windowed_signal = signal .* window;

% Compute FFT
fft_result = fft(windowed_signal, options.nfft);

% Generate frequency vector
frequencies = (0:options.nfft-1) * (fs / options.nfft);

% Compute magnitude in dB
magnitude = abs(fft_result);
magnitude_db = 20 * log10(magnitude + eps); % Add eps to avoid log(0)

% Normalize to 0 dB maximum
magnitude_db = magnitude_db - max(magnitude_db);

% Plot if requested
if options.plot
    plot_spectrum(frequencies, magnitude_db, fs);
end

end


function plot_spectrum(frequencies, magnitude_db, fs)
% Helper function to plot spectrum

figure('Name', 'Frequency Spectrum Analysis');

% Plot only positive frequencies
idx = frequencies <= fs/2;
plot(frequencies(idx), magnitude_db(idx), 'LineWidth', 1.5);

grid on;
xlabel('Frequency (Hz)');
ylabel('Magnitude (dB)');
title('Power Spectrum Density');
xlim([0 fs/2]);
ylim([min(magnitude_db(idx))-10, 5]);

end
