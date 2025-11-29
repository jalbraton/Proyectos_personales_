function [modulated_signal, t, carrier] = modulate_signal(message, fs, fc, mod_type, options)
% MODULATE_SIGNAL Apply various modulation schemes to a message signal
%
% Description:
%   Implements analog and digital modulation techniques including AM, FM,
%   PSK, QAM. Returns modulated signal ready for transmission.
%
% Syntax:
%   [modulated_signal, t, carrier] = modulate_signal(message, fs, fc, mod_type)
%   [modulated_signal, t, carrier] = modulate_signal(message, fs, fc, mod_type, options)
%
% Inputs:
%   message - Message signal (baseband)
%   fs - Sampling frequency in Hz
%   fc - Carrier frequency in Hz
%   mod_type - Modulation type: 'AM', 'FM', 'BPSK', 'QPSK', '16QAM', '64QAM'
%   options - Optional structure with fields:
%       .mod_index - Modulation index for AM/FM (default: 1.0)
%       .freq_deviation - Frequency deviation for FM in Hz (default: fc/10)
%       .plot - Boolean to generate constellation/time plot (default: false)
%
% Outputs:
%   modulated_signal - Modulated signal
%   t - Time vector
%   carrier - Carrier signal
%
% Example:
%   fs = 10000; fc = 1000;
%   t = 0:1/fs:0.1;
%   message = sin(2*pi*50*t);
%   [mod_sig, t, carrier] = modulate_signal(message, fs, fc, 'AM', 'mod_index', 0.5, 'plot', true);
%
% Reference:
%   Proakis, J. G., & Salehi, M. (2008). Digital Communications (5th ed.).
%   McGraw-Hill.

% Input validation
arguments
    message (1,:) double
    fs (1,1) double {mustBePositive}
    fc (1,1) double {mustBePositive}
    mod_type (1,:) char
    options.mod_index (1,1) double = 1.0
    options.freq_deviation (1,1) double = fc/10
    options.plot (1,1) logical = false
end

% Generate time vector
N = length(message);
t = (0:N-1) / fs;

% Generate carrier
carrier = cos(2*pi*fc*t);

% Modulate based on type
switch upper(mod_type)
    case 'AM'
        modulated_signal = modulate_am(message, carrier, options.mod_index);
        
    case 'FM'
        modulated_signal = modulate_fm(message, fs, fc, options.freq_deviation);
        
    case 'BPSK'
        modulated_signal = modulate_bpsk(message, carrier);
        
    case 'QPSK'
        modulated_signal = modulate_qpsk(message, carrier, fs, fc);
        
    case '16QAM'
        modulated_signal = modulate_qam(message, carrier, fs, fc, 16);
        
    case '64QAM'
        modulated_signal = modulate_qam(message, carrier, fs, fc, 64);
        
    otherwise
        error('Unknown modulation type: %s', mod_type);
end

% Plot if requested
if options.plot
    plot_modulation(message, modulated_signal, t, mod_type, carrier);
end

end


function modulated = modulate_am(message, carrier, mod_index)
% Amplitude Modulation: s(t) = Ac[1 + m*m(t)]cos(2*pi*fc*t)

% Normalize message to [-1, 1] (avoid division by zero)
max_val = max(abs(message));
if max_val > 0
    message_norm = message / max_val;
else
    message_norm = message;
end

% Apply AM
modulated = (1 + mod_index * message_norm) .* carrier;

end


function modulated = modulate_fm(message, fs, fc, freq_dev)
% Frequency Modulation: s(t) = Ac*cos(2*pi*fc*t + 2*pi*kf*integral(m(t)))

% Normalize message (avoid division by zero)
max_val = max(abs(message));
if max_val > 0
    message_norm = message / max_val;
else
    message_norm = message;
end

% Compute instantaneous phase
kf = freq_dev; % Frequency sensitivity
phase = 2*pi*kf*cumsum(message_norm)/fs;

% Generate FM signal
t = (0:length(message)-1) / fs;
modulated = cos(2*pi*fc*t + phase);

end


function modulated = modulate_bpsk(bits, carrier)
% Binary Phase Shift Keying: 0 -> +1, 1 -> -1

% Convert bits to symbols
bits_binary = bits > mean(bits); % Threshold to binary
symbols = 2*bits_binary - 1; % Map to {-1, +1}

% Modulate
modulated = symbols .* carrier;

end


function modulated = modulate_qpsk(bits, carrier, fs, fc)
% Quadrature Phase Shift Keying: 4 symbols, 2 bits per symbol

% Upsample bits to match carrier length
bits_binary = bits > mean(bits);
symbols_per_bit = floor(length(carrier) / length(bits));
bits_upsampled = repelem(bits_binary, symbols_per_bit);
bits_upsampled = bits_upsampled(1:length(carrier));

% Group bits into pairs (if odd length, pad)
if mod(length(bits_upsampled), 2) ~= 0
    bits_upsampled = [bits_upsampled, 0];
end

% Map bit pairs to QPSK symbols: 00->1+j, 01->-1+j, 11->-1-j, 10->1-j
I = zeros(1, length(bits_upsampled)/2);
Q = zeros(1, length(bits_upsampled)/2);

for i = 1:2:length(bits_upsampled)-1
    idx = (i+1)/2;
    if bits_upsampled(i) == 0 && bits_upsampled(i+1) == 0
        I(idx) = 1; Q(idx) = 1;
    elseif bits_upsampled(i) == 0 && bits_upsampled(i+1) == 1
        I(idx) = -1; Q(idx) = 1;
    elseif bits_upsampled(i) == 1 && bits_upsampled(i+1) == 1
        I(idx) = -1; Q(idx) = -1;
    else
        I(idx) = 1; Q(idx) = -1;
    end
end

% Upsample symbols to carrier rate
I_upsampled = repelem(I, symbols_per_bit*2);
Q_upsampled = repelem(Q, symbols_per_bit*2);
I_upsampled = I_upsampled(1:length(carrier));
Q_upsampled = Q_upsampled(1:length(carrier));

% Generate quadrature carrier
t = (0:length(carrier)-1) / fs;
carrier_Q = -sin(2*pi*fc*t);

% Modulate
modulated = I_upsampled .* carrier + Q_upsampled .* carrier_Q;
modulated = modulated / sqrt(2); % Normalize power

end


function modulated = modulate_qam(bits, carrier, fs, fc, M)
% Quadrature Amplitude Modulation: M-QAM (M=16, 64, etc.)

% QAM constellation for 16-QAM: symbols on grid {-3,-1,+1,+3}
if M == 16
    constellation = [-3-3j, -3-1j, -3+1j, -3+3j, ...
                     -1-3j, -1-1j, -1+1j, -1+3j, ...
                      1-3j,  1-1j,  1+1j,  1+3j, ...
                      3-3j,  3-1j,  3+1j,  3+3j] / sqrt(10);
    bits_per_symbol = 4;
elseif M == 64
    % 64-QAM constellation (simplified)
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

% Upsample and group bits
bits_binary = bits > mean(bits);
symbols_per_bit = max(1, floor(length(carrier) / length(bits)));
bits_upsampled = repelem(bits_binary, symbols_per_bit);
bits_upsampled = bits_upsampled(1:length(carrier));

% Pad to multiple of bits_per_symbol
remainder = mod(length(bits_upsampled), bits_per_symbol);
if remainder ~= 0
    bits_upsampled = [bits_upsampled, zeros(1, bits_per_symbol - remainder)];
end

% Map bits to symbols
num_symbols = length(bits_upsampled) / bits_per_symbol;
symbols = zeros(1, num_symbols);

for i = 1:num_symbols
    bit_group = bits_upsampled((i-1)*bits_per_symbol+1 : i*bits_per_symbol);
    symbol_idx = bi2de(bit_group, 'left-msb') + 1;
    symbols(i) = constellation(symbol_idx);
end

% Upsample symbols
samples_per_symbol = max(1, floor(length(carrier) / num_symbols));
I = real(symbols);
Q = imag(symbols);
I_upsampled = repelem(I, samples_per_symbol);
Q_upsampled = repelem(Q, samples_per_symbol);
I_upsampled = I_upsampled(1:min(length(I_upsampled), length(carrier)));
Q_upsampled = Q_upsampled(1:min(length(Q_upsampled), length(carrier)));

% Generate quadrature carrier
t = (0:length(carrier)-1) / fs;
carrier_Q = -sin(2*pi*fc*t);

% Modulate
modulated = I_upsampled .* carrier + Q_upsampled .* carrier_Q;

end


function plot_modulation(message, modulated, t, mod_type, carrier)
% Plot modulation results

figure('Name', sprintf('%s Modulation', mod_type));

% Plot first 500 samples for clarity
n_samples = min(500, length(t));
t_plot = t(1:n_samples);

subplot(3, 1, 1);
plot(t_plot, message(1:n_samples), 'LineWidth', 1.5);
grid on;
xlabel('Time (s)');
ylabel('Amplitude');
title('Message Signal');

subplot(3, 1, 2);
plot(t_plot, carrier(1:n_samples), 'LineWidth', 1);
grid on;
xlabel('Time (s)');
ylabel('Amplitude');
title('Carrier Signal');

subplot(3, 1, 3);
plot(t_plot, modulated(1:n_samples), 'LineWidth', 1);
grid on;
xlabel('Time (s)');
ylabel('Amplitude');
title(sprintf('%s Modulated Signal', mod_type));

end
