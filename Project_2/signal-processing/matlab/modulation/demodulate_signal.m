function [demodulated, ber] = demodulate_signal(modulated_signal, fs, fc, mod_type, reference_bits, options)
% DEMODULATE_SIGNAL Demodulate various modulated signals
%
% Description:
%   Implements demodulation for AM, FM, PSK, and QAM modulated signals.
%   Calculates Bit Error Rate (BER) if reference bits provided.
%
% Syntax:
%   [demodulated, ber] = demodulate_signal(modulated_signal, fs, fc, mod_type, reference_bits)
%   [demodulated, ber] = demodulate_signal(modulated_signal, fs, fc, mod_type, reference_bits, options)
%
% Inputs:
%   modulated_signal - Received modulated signal
%   fs - Sampling frequency in Hz
%   fc - Carrier frequency in Hz
%   mod_type - Modulation type: 'AM', 'FM', 'BPSK', 'QPSK', '16QAM', '64QAM'
%   reference_bits - Original bits for BER calculation (optional)
%   options - Optional structure with fields:
%       .freq_deviation - Frequency deviation for FM in Hz (default: fc/10)
%       .plot - Boolean to generate constellation diagram (default: false)
%
% Outputs:
%   demodulated - Demodulated signal or bits
%   ber - Bit Error Rate (if reference_bits provided)
%
% Example:
%   [demod, ber] = demodulate_signal(rx_signal, 10000, 1000, 'BPSK', original_bits);
%
% Reference:
%   Sklar, B. (2001). Digital Communications: Fundamentals and Applications (2nd ed.).
%   Prentice Hall.

% Input validation
arguments
    modulated_signal (1,:) double
    fs (1,1) double {mustBePositive}
    fc (1,1) double {mustBePositive}
    mod_type (1,:) char
    reference_bits (1,:) double = []
    options.freq_deviation (1,1) double = fc/10
    options.plot (1,1) logical = false
end

% Demodulate based on type
switch upper(mod_type)
    case 'AM'
        demodulated = demodulate_am(modulated_signal, fs, fc);
        
    case 'FM'
        demodulated = demodulate_fm(modulated_signal, fs, options.freq_deviation);
        
    case 'BPSK'
        [demodulated, symbols] = demodulate_bpsk(modulated_signal, fs, fc);
        
    case 'QPSK'
        [demodulated, symbols] = demodulate_qpsk(modulated_signal, fs, fc);
        
    case '16QAM'
        [demodulated, symbols] = demodulate_qam(modulated_signal, fs, fc, 16);
        
    case '64QAM'
        [demodulated, symbols] = demodulate_qam(modulated_signal, fs, fc, 64);
        
    otherwise
        error('Unknown modulation type: %s', mod_type);
end

% Calculate BER if reference provided
if ~isempty(reference_bits) && ~strcmpi(mod_type, 'AM') && ~strcmpi(mod_type, 'FM')
    % Align lengths
    min_len = min(length(reference_bits), length(demodulated));
    ber = sum(reference_bits(1:min_len) ~= demodulated(1:min_len)) / min_len;
else
    ber = NaN;
end

% Plot constellation if requested and applicable
if options.plot && exist('symbols', 'var')
    plot_constellation(symbols, mod_type);
end

end


function demodulated = demodulate_am(modulated_signal, fs, fc)
% AM Demodulation using envelope detection

% Rectification
rectified = abs(modulated_signal);

% Low-pass filter to extract envelope
cutoff = fc / 10; % Cutoff at 10% of carrier
[b, a] = butter(6, cutoff/(fs/2), 'low');
envelope = filter(b, a, rectified);

% Remove DC component
demodulated = envelope - mean(envelope);

end


function demodulated = demodulate_fm(modulated_signal, fs, freq_dev)
% FM Demodulation using differentiation and envelope detection

% Differentiate the signal
differentiated = [0, diff(modulated_signal)];

% Envelope detection
envelope = abs(hilbert(differentiated));

% Low-pass filter
cutoff = freq_dev * 2;
[b, a] = butter(6, cutoff/(fs/2), 'low');
demodulated = filter(b, a, envelope);

% Remove DC and normalize (avoid division by zero)
demodulated = demodulated - mean(demodulated);
max_val = max(abs(demodulated));
if max_val > 0
    demodulated = demodulated / max_val;
end

end


function [bits, symbols] = demodulate_bpsk(modulated_signal, fs, fc)
% BPSK Demodulation using coherent detection

% Generate local carrier
t = (0:length(modulated_signal)-1) / fs;
carrier = cos(2*pi*fc*t);

% Multiply by carrier (coherent detection)
product = modulated_signal .* carrier;

% Low-pass filter
[b, a] = butter(6, fc/(fs/2), 'low');
filtered = filter(b, a, product);

% Downsample to symbol rate (approximate)
symbols_per_bit = round(fs / (fc * 0.1));
if symbols_per_bit < 1
    symbols_per_bit = 1;
end

downsampled = filtered(1:symbols_per_bit:end);
symbols = downsampled;

% Decision: threshold at 0
bits = double(downsampled > 0);

end


function [bits, symbols] = demodulate_qpsk(modulated_signal, fs, fc)
% QPSK Demodulation using coherent detection

% Generate local carriers
t = (0:length(modulated_signal)-1) / fs;
carrier_I = cos(2*pi*fc*t);
carrier_Q = -sin(2*pi*fc*t);

% Multiply by carriers
I_product = modulated_signal .* carrier_I;
Q_product = modulated_signal .* carrier_Q;

% Low-pass filter
[b, a] = butter(6, fc/(fs/2), 'low');
I_filtered = filter(b, a, I_product);
Q_filtered = filter(b, a, Q_product);

% Downsample
symbols_per_bit = max(1, round(fs / (fc * 0.2)));
I_down = I_filtered(1:symbols_per_bit:end);
Q_down = Q_filtered(1:symbols_per_bit:end);

symbols = I_down + 1j*Q_down;

% Decision for each symbol
bits = zeros(1, 2*length(I_down));
for i = 1:length(I_down)
    % Decode I component
    bits(2*i-1) = double(I_down(i) < 0);
    % Decode Q component
    bits(2*i) = double(Q_down(i) < 0);
end

end


function [bits, symbols] = demodulate_qam(modulated_signal, fs, fc, M)
% QAM Demodulation for M-QAM (M=16, 64)

% Generate local carriers
t = (0:length(modulated_signal)-1) / fs;
carrier_I = cos(2*pi*fc*t);
carrier_Q = -sin(2*pi*fc*t);

% Multiply by carriers
I_product = modulated_signal .* carrier_I;
Q_product = modulated_signal .* carrier_Q;

% Low-pass filter
[b, a] = butter(6, fc/(fs/2), 'low');
I_filtered = filter(b, a, I_product);
Q_filtered = filter(b, a, Q_product);

% Downsample
symbols_per_bit = max(1, round(fs / (fc * 0.2)));
I_down = I_filtered(1:symbols_per_bit:end);
Q_down = Q_filtered(1:symbols_per_bit:end);

symbols = I_down + 1j*Q_down;

% Define constellation
if M == 16
    constellation = [-3-3j, -3-1j, -3+1j, -3+3j, ...
                     -1-3j, -1-1j, -1+1j, -1+3j, ...
                      1-3j,  1-1j,  1+1j,  1+3j, ...
                      3-3j,  3-1j,  3+1j,  3+3j] / sqrt(10);
    bits_per_symbol = 4;
elseif M == 64
    constellation_1d = [-7, -5, -3, -1, 1, 3, 5, 7] / sqrt(42);
    constellation = [];
    for i = 1:8
        for q = 1:8
            constellation = [constellation, constellation_1d(i) + 1j*constellation_1d(q)];
        end
    end
    bits_per_symbol = 6;
else
    error('Unsupported QAM order: %d', M);
end

% Decode symbols to bits
bits = zeros(1, length(symbols) * bits_per_symbol);

for i = 1:length(symbols)
    % Find closest constellation point
    [~, idx] = min(abs(symbols(i) - constellation));
    
    % Convert index to bits
    bit_group = de2bi(idx - 1, bits_per_symbol, 'left-msb');
    bits((i-1)*bits_per_symbol+1 : i*bits_per_symbol) = bit_group;
end

end


function plot_constellation(symbols, mod_type)
% Plot constellation diagram

figure('Name', sprintf('%s Constellation Diagram', mod_type));

% Plot received symbols
plot(real(symbols), imag(symbols), 'b.', 'MarkerSize', 8);
hold on;

% Plot ideal constellation points
switch upper(mod_type)
    case 'QPSK'
        ideal = [1+1j, -1+1j, -1-1j, 1-1j] / sqrt(2);
        plot(real(ideal), imag(ideal), 'r*', 'MarkerSize', 15, 'LineWidth', 2);
        
    case '16QAM'
        ideal = [-3-3j, -3-1j, -3+1j, -3+3j, ...
                 -1-3j, -1-1j, -1+1j, -1+3j, ...
                  1-3j,  1-1j,  1+1j,  1+3j, ...
                  3-3j,  3-1j,  3+1j,  3+3j] / sqrt(10);
        plot(real(ideal), imag(ideal), 'r*', 'MarkerSize', 15, 'LineWidth', 2);
end

grid on;
axis equal;
xlabel('In-Phase');
ylabel('Quadrature');
title(sprintf('%s Constellation Diagram', mod_type));
legend('Received Symbols', 'Ideal Constellation');

end
