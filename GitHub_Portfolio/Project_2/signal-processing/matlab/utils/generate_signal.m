function [signal, t] = generate_signal(signal_type, fs, duration, options)
% GENERATE_SIGNAL Generate various test signals for signal processing
%
% Description:
%   Generates common test signals including sinusoids, square waves,
%   chirps, noise, and composite signals. Useful for testing filters,
%   modulation schemes, and spectrum analysis.
%
% Syntax:
%   [signal, t] = generate_signal(signal_type, fs, duration)
%   [signal, t] = generate_signal(signal_type, fs, duration, options)
%
% Inputs:
%   signal_type - Type: 'sine', 'square', 'sawtooth', 'triangle', 'chirp',
%                       'white_noise', 'pink_noise', 'impulse', 'step', 'composite'
%   fs - Sampling frequency in Hz
%   duration - Signal duration in seconds
%   options - Optional structure with fields:
%       .frequency - Frequency for periodic signals in Hz (default: 100)
%       .amplitude - Signal amplitude (default: 1.0)
%       .phase - Initial phase in radians (default: 0)
%       .f0 - Start frequency for chirp in Hz (default: 0)
%       .f1 - End frequency for chirp in Hz (default: fs/4)
%       .snr - Signal-to-Noise Ratio in dB for noise (default: Inf)
%       .duty_cycle - Duty cycle for square wave (default: 0.5)
%       .composite_freqs - Array of frequencies for composite signal (default: [50, 120, 300])
%
% Outputs:
%   signal - Generated signal vector
%   t - Time vector in seconds
%
% Example:
%   [sig, t] = generate_signal('sine', 1000, 1, 'frequency', 50, 'amplitude', 2);
%   [chirp_sig, t] = generate_signal('chirp', 10000, 2, 'f0', 0, 'f1', 2000);
%
% Reference:
%   IEEE Standard 181-2003: Transitions, Pulses, and Related Waveforms

% Input validation
arguments
    signal_type (1,:) char
    fs (1,1) double {mustBePositive}
    duration (1,1) double {mustBePositive}
    options.frequency (1,1) double = 100
    options.amplitude (1,1) double = 1.0
    options.phase (1,1) double = 0
    options.f0 (1,1) double = 0
    options.f1 (1,1) double = fs/4
    options.snr (1,1) double = Inf
    options.duty_cycle (1,1) double = 0.5
    options.composite_freqs (1,:) double = [50, 120, 300]
end

% Generate time vector
t = 0:1/fs:duration-1/fs;

% Generate signal based on type
switch lower(signal_type)
    case 'sine'
        signal = generate_sine(t, options.frequency, options.amplitude, options.phase);
        
    case 'square'
        signal = generate_square(t, options.frequency, options.amplitude, options.duty_cycle);
        
    case 'sawtooth'
        signal = generate_sawtooth(t, options.frequency, options.amplitude);
        
    case 'triangle'
        signal = generate_triangle(t, options.frequency, options.amplitude);
        
    case 'chirp'
        signal = generate_chirp(t, options.f0, options.f1, options.amplitude);
        
    case 'white_noise'
        signal = generate_white_noise(length(t), options.amplitude);
        
    case 'pink_noise'
        signal = generate_pink_noise(length(t), options.amplitude);
        
    case 'impulse'
        signal = generate_impulse(length(t), options.amplitude);
        
    case 'step'
        signal = generate_step(length(t), options.amplitude);
        
    case 'composite'
        signal = generate_composite(t, options.composite_freqs, options.amplitude);
        
    otherwise
        error('Unknown signal type: %s', signal_type);
end

% Add noise if SNR specified
if isfinite(options.snr)
    signal = add_noise(signal, options.snr);
end

end


function signal = generate_sine(t, freq, amp, phase)
% Generate sinusoidal signal

signal = amp * sin(2*pi*freq*t + phase);

end


function signal = generate_square(t, freq, amp, duty_cycle)
% Generate square wave

signal = amp * square(2*pi*freq*t, duty_cycle*100);

end


function signal = generate_sawtooth(t, freq, amp)
% Generate sawtooth wave

signal = amp * sawtooth(2*pi*freq*t);

end


function signal = generate_triangle(t, freq, amp)
% Generate triangle wave

signal = amp * sawtooth(2*pi*freq*t, 0.5);

end


function signal = generate_chirp(t, f0, f1, amp)
% Generate linear chirp (sweep from f0 to f1)

signal = amp * chirp(t, f0, t(end), f1);

end


function signal = generate_white_noise(N, amp)
% Generate white Gaussian noise

signal = amp * randn(1, N);

end


function signal = generate_pink_noise(N, amp)
% Generate pink noise (1/f noise)

% Generate white noise
white = randn(1, N);

% Apply 1/f filter in frequency domain
X = fft(white);
freqs = 1:floor(length(X)/2);
% Create 1/sqrt(f) characteristic
filter_resp = [1, 1./sqrt(freqs)];
if length(X) > length(filter_resp)
    filter_resp = [filter_resp, fliplr(filter_resp(2:end))];
end
if length(filter_resp) < length(X)
    filter_resp = [filter_resp, ones(1, length(X) - length(filter_resp))];
end

% Apply filter
X_filtered = X .* filter_resp;
signal = real(ifft(X_filtered));

% Normalize and scale (avoid division by zero)
max_val = max(abs(signal));
if max_val > 0
    signal = signal / max_val * amp;
else
    signal = signal * amp;
end

end


function signal = generate_impulse(N, amp)
% Generate impulse signal (delta function)

signal = zeros(1, N);
signal(round(N/2)) = amp;

end


function signal = generate_step(N, amp)
% Generate step function

signal = zeros(1, N);
signal(round(N/2):end) = amp;

end


function signal = generate_composite(t, freqs, amp)
% Generate composite signal (sum of sinusoids)

signal = zeros(size(t));
for freq = freqs
    signal = signal + sin(2*pi*freq*t);
end

% Normalize and scale (avoid division by zero)
max_val = max(abs(signal));
if max_val > 0
    signal = signal / max_val * amp;
else
    signal = signal * amp;
end

end


function noisy_signal = add_noise(signal, snr_db)
% Add white Gaussian noise to achieve specified SNR

% Calculate signal power
signal_power = mean(signal.^2);

% Calculate noise power for desired SNR
snr_linear = 10^(snr_db/10);
noise_power = signal_power / snr_linear;

% Generate noise
noise = sqrt(noise_power) * randn(size(signal));

% Add noise to signal
noisy_signal = signal + noise;

end
