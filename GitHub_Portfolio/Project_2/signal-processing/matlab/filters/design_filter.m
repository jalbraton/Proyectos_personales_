function [b, a, h, w] = design_filter(filter_type, order, cutoff_freq, fs, options)
% DESIGN_FILTER Design digital filters (IIR and FIR)
%
% Description:
%   Designs digital filters using various methods including Butterworth,
%   Chebyshev, Elliptic for IIR filters and windowing method for FIR filters.
%
% Syntax:
%   [b, a, h, w] = design_filter(filter_type, order, cutoff_freq, fs)
%   [b, a, h, w] = design_filter(filter_type, order, cutoff_freq, fs, options)
%
% Inputs:
%   filter_type - Filter type: 'lowpass', 'highpass', 'bandpass', 'bandstop'
%   order - Filter order
%   cutoff_freq - Cutoff frequency in Hz (scalar for LP/HP, 2-element vector for BP/BS)
%   fs - Sampling frequency in Hz
%   options - Optional structure with fields:
%       .method - Filter design method: 'butterworth', 'chebyshev1', 'chebyshev2', 
%                 'elliptic', 'fir' (default: 'butterworth')
%       .ripple - Passband ripple in dB for Chebyshev/Elliptic (default: 0.5)
%       .stopband_atten - Stopband attenuation in dB for Chebyshev2/Elliptic (default: 40)
%       .plot - Boolean to generate frequency response plot (default: false)
%
% Outputs:
%   b - Numerator coefficients
%   a - Denominator coefficients (a=1 for FIR filters)
%   h - Frequency response
%   w - Frequency vector (normalized)
%
% Example:
%   fs = 1000;
%   [b, a] = design_filter('lowpass', 4, 100, fs, 'method', 'butterworth', 'plot', true);
%
% Reference:
%   Parks, T. W., & Burrus, C. S. (1987). Digital Filter Design.
%   John Wiley & Sons.

% Input validation
arguments
    filter_type (1,:) char {mustBeMember(filter_type, {'lowpass', 'highpass', 'bandpass', 'bandstop'})}
    order (1,1) double {mustBePositive, mustBeInteger}
    cutoff_freq (1,:) double {mustBePositive}
    fs (1,1) double {mustBePositive}
    options.method (1,:) char = 'butterworth'
    options.ripple (1,1) double = 0.5
    options.stopband_atten (1,1) double = 40
    options.plot (1,1) logical = false
end

% Normalize cutoff frequency (0 to 1, where 1 is Nyquist)
wn = cutoff_freq / (fs/2);

% Validate cutoff frequencies (use 0.99 to avoid edge case numerical issues)
if any(wn >= 0.99)
    error('Cutoff frequency must be less than Nyquist frequency (fs/2)');
end

% Design filter based on method
switch lower(options.method)
    case 'butterworth'
        [b, a] = design_butterworth(filter_type, order, wn);
        
    case 'chebyshev1'
        [b, a] = design_chebyshev1(filter_type, order, wn, options.ripple);
        
    case 'chebyshev2'
        [b, a] = design_chebyshev2(filter_type, order, wn, options.stopband_atten);
        
    case 'elliptic'
        [b, a] = design_elliptic(filter_type, order, wn, options.ripple, options.stopband_atten);
        
    case 'fir'
        [b, a] = design_fir(filter_type, order, wn);
        
    otherwise
        error('Unknown filter design method: %s', options.method);
end

% Compute frequency response
[h, w] = freqz(b, a, 512);

% Plot if requested
if options.plot
    plot_filter_response(b, a, fs, filter_type, options.method);
end

end


function [b, a] = design_butterworth(filter_type, order, wn)
% Design Butterworth filter

switch filter_type
    case 'lowpass'
        [b, a] = butter(order, wn, 'low');
    case 'highpass'
        [b, a] = butter(order, wn, 'high');
    case 'bandpass'
        [b, a] = butter(order, wn, 'bandpass');
    case 'bandstop'
        [b, a] = butter(order, wn, 'stop');
end

end


function [b, a] = design_chebyshev1(filter_type, order, wn, ripple)
% Design Chebyshev Type I filter

switch filter_type
    case 'lowpass'
        [b, a] = cheby1(order, ripple, wn, 'low');
    case 'highpass'
        [b, a] = cheby1(order, ripple, wn, 'high');
    case 'bandpass'
        [b, a] = cheby1(order, ripple, wn, 'bandpass');
    case 'bandstop'
        [b, a] = cheby1(order, ripple, wn, 'stop');
end

end


function [b, a] = design_chebyshev2(filter_type, order, wn, stopband_atten)
% Design Chebyshev Type II filter

switch filter_type
    case 'lowpass'
        [b, a] = cheby2(order, stopband_atten, wn, 'low');
    case 'highpass'
        [b, a] = cheby2(order, stopband_atten, wn, 'high');
    case 'bandpass'
        [b, a] = cheby2(order, stopband_atten, wn, 'bandpass');
    case 'bandstop'
        [b, a] = cheby2(order, stopband_atten, wn, 'stop');
end

end


function [b, a] = design_elliptic(filter_type, order, wn, ripple, stopband_atten)
% Design Elliptic filter

switch filter_type
    case 'lowpass'
        [b, a] = ellip(order, ripple, stopband_atten, wn, 'low');
    case 'highpass'
        [b, a] = ellip(order, ripple, stopband_atten, wn, 'high');
    case 'bandpass'
        [b, a] = ellip(order, ripple, stopband_atten, wn, 'bandpass');
    case 'bandstop'
        [b, a] = ellip(order, ripple, stopband_atten, wn, 'stop');
end

end


function [b, a] = design_fir(filter_type, order, wn)
% Design FIR filter using windowing method

% Use Hamming window
window = hamming(order + 1);

switch filter_type
    case 'lowpass'
        b = fir1(order, wn, 'low', window);
    case 'highpass'
        b = fir1(order, wn, 'high', window);
    case 'bandpass'
        b = fir1(order, wn, 'bandpass', window);
    case 'bandstop'
        b = fir1(order, wn, 'stop', window);
end

a = 1; % FIR filter has denominator = 1

end


function plot_filter_response(b, a, fs, filter_type, method)
% Plot filter frequency response

figure('Name', 'Filter Frequency Response');

% Magnitude response
subplot(2, 1, 1);
[h, f] = freqz(b, a, 1024, fs);
plot(f, 20*log10(abs(h)), 'LineWidth', 1.5);
grid on;
xlabel('Frequency (Hz)');
ylabel('Magnitude (dB)');
title(sprintf('%s %s Filter - Magnitude Response', upper(method), filter_type));
ylim([-100 5]);

% Phase response
subplot(2, 1, 2);
plot(f, unwrap(angle(h))*180/pi, 'LineWidth', 1.5);
grid on;
xlabel('Frequency (Hz)');
ylabel('Phase (degrees)');
title('Phase Response');

end
