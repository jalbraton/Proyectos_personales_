"""
Signal Processing Python Demo Package
======================================

Professional signal processing library implementing FFT analysis, digital filters,
and communication system modulation/demodulation schemes.

Modules:
    spectrum: FFT-based spectrum analysis with windowing
    filters: IIR/FIR filter design (Butterworth, Chebyshev, Elliptic)
    modulation: AM, FM, PSK, QAM modulators
    demodulation: Coherent demodulators with BER analysis
    signal_generator: Test signal generation utilities

Author: Signal Processing Package
Date: 2025
Python Version: 3.8+

Dependencies:
    numpy>=1.20.0
    scipy>=1.7.0
    matplotlib>=3.4.0

References:
    Oppenheim, A. V., & Schafer, R. W. (2010). Discrete-Time Signal Processing. Pearson.
    Proakis, J. G., & Salehi, M. (2008). Digital Communications (5th ed.). McGraw-Hill.
"""

__version__ = "1.0.0"
__author__ = "Signal Processing Package"

from .spectrum import analyze_spectrum
from .filters import design_filter, apply_filter
from .modulation import modulate_signal
from .demodulation import demodulate_signal
from .signal_generator import generate_signal

__all__ = [
    'analyze_spectrum',
    'design_filter',
    'apply_filter',
    'modulate_signal',
    'demodulate_signal',
    'generate_signal',
]
