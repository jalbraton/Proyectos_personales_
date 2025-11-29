"""
Test script to validate signal processing package
"""

import sys
import traceback

def test_imports():
    """Test all imports"""
    print("Testing imports...")
    try:
        import numpy as np
        print("  ✓ numpy")
    except ImportError as e:
        print(f"  ✗ numpy: {e}")
        return False
    
    try:
        from scipy import signal as sp_signal
        print("  ✓ scipy")
    except ImportError as e:
        print(f"  ✗ scipy: {e}")
        return False
    
    try:
        import matplotlib.pyplot as plt
        print("  ✓ matplotlib")
    except ImportError as e:
        print(f"  ✗ matplotlib: {e}")
        return False
    
    return True


def test_signal_generator():
    """Test signal generator module"""
    print("\nTesting signal_generator...")
    try:
        from signal_processing.signal_generator import generate_signal
        
        # Test sine
        signal, t = generate_signal('sine', 1000, 0.1, frequency=50)
        assert len(signal) > 0, "Sine signal empty"
        assert len(signal) == len(t), "Signal and time vector length mismatch"
        print("  ✓ sine generation")
        
        # Test chirp
        signal, t = generate_signal('chirp', 1000, 0.1, f0=0, f1=500)
        assert len(signal) > 0, "Chirp signal empty"
        print("  ✓ chirp generation")
        
        # Test composite
        signal, t = generate_signal('composite', 1000, 0.1, composite_freqs=[50, 120])
        assert len(signal) > 0, "Composite signal empty"
        print("  ✓ composite generation")
        
        # Test noise
        signal, t = generate_signal('white_noise', 1000, 0.1, amplitude=0.5)
        assert len(signal) > 0, "Noise signal empty"
        print("  ✓ noise generation")
        
        return True
    except Exception as e:
        print(f"  ✗ Error: {e}")
        traceback.print_exc()
        return False


def test_spectrum():
    """Test spectrum analysis"""
    print("\nTesting spectrum analysis...")
    try:
        from signal_processing.spectrum import analyze_spectrum
        from signal_processing.signal_generator import generate_signal
        import numpy as np
        
        signal, t = generate_signal('sine', 1000, 1, frequency=50)
        fft_result, freq, mag_db = analyze_spectrum(signal, 1000, window='hann')
        
        assert len(fft_result) > 0, "FFT result empty"
        assert len(freq) == len(mag_db), "Frequency and magnitude length mismatch"
        print("  ✓ FFT analysis")
        
        return True
    except Exception as e:
        print(f"  ✗ Error: {e}")
        traceback.print_exc()
        return False


def test_filters():
    """Test filter design"""
    print("\nTesting filter design...")
    try:
        from signal_processing.filters import design_filter, apply_filter
        from signal_processing.signal_generator import generate_signal
        
        # Test Butterworth lowpass
        b, a = design_filter('lowpass', 4, 100, 1000, method='butterworth')
        assert len(b) > 0 and len(a) > 0, "Filter coefficients empty"
        print("  ✓ Butterworth filter design")
        
        # Test filter application
        signal, t = generate_signal('sine', 1000, 0.1, frequency=50)
        filtered = apply_filter(signal, b, a)
        assert len(filtered) == len(signal), "Filtered signal length mismatch"
        print("  ✓ Filter application")
        
        # Test bandpass
        b, a = design_filter('bandpass', 4, (80, 200), 1000, method='chebyshev1')
        assert len(b) > 0, "Bandpass filter empty"
        print("  ✓ Bandpass filter design")
        
        return True
    except Exception as e:
        print(f"  ✗ Error: {e}")
        traceback.print_exc()
        return False


def test_modulation():
    """Test modulation schemes"""
    print("\nTesting modulation...")
    try:
        from signal_processing.modulation import modulate_signal
        from signal_processing.signal_generator import generate_signal
        import numpy as np
        
        fs = 10000
        
        # Test AM
        message, t = generate_signal('sine', fs, 0.1, frequency=10)
        mod_sig, t_out, carrier = modulate_signal(message, fs, 1000, 'AM', mod_index=0.8)
        assert len(mod_sig) > 0, "AM modulation failed"
        print("  ✓ AM modulation")
        
        # Test FM
        mod_sig, t_out, carrier = modulate_signal(message, fs, 1000, 'FM', freq_deviation=100)
        assert len(mod_sig) > 0, "FM modulation failed"
        print("  ✓ FM modulation")
        
        # Test BPSK
        bits = np.random.randint(0, 2, 50)
        mod_sig, t_out, carrier = modulate_signal(bits, fs, 1000, 'BPSK')
        assert len(mod_sig) > 0, "BPSK modulation failed"
        print("  ✓ BPSK modulation")
        
        # Test QPSK
        mod_sig, t_out, carrier = modulate_signal(bits, fs, 1000, 'QPSK')
        assert len(mod_sig) > 0, "QPSK modulation failed"
        print("  ✓ QPSK modulation")
        
        # Test 16-QAM
        mod_sig, t_out, carrier = modulate_signal(bits, fs, 1000, '16QAM')
        assert len(mod_sig) > 0, "16-QAM modulation failed"
        print("  ✓ 16-QAM modulation")
        
        return True
    except Exception as e:
        print(f"  ✗ Error: {e}")
        traceback.print_exc()
        return False


def test_demodulation():
    """Test demodulation schemes"""
    print("\nTesting demodulation...")
    try:
        from signal_processing.modulation import modulate_signal
        from signal_processing.demodulation import demodulate_signal
        import numpy as np
        
        fs = 10000
        fc = 1000
        
        # Generate bits
        bits = np.random.randint(0, 2, 50)
        
        # Test BPSK demodulation
        mod_sig, _, _ = modulate_signal(bits, fs, fc, 'BPSK')
        demod_bits, ber = demodulate_signal(mod_sig, fs, fc, 'BPSK', bits)
        assert len(demod_bits) > 0, "BPSK demodulation failed"
        assert ber >= 0, "Invalid BER"
        print(f"  ✓ BPSK demodulation (BER: {ber:.4f})")
        
        # Test QPSK demodulation
        mod_sig, _, _ = modulate_signal(bits, fs, fc, 'QPSK')
        demod_bits, ber = demodulate_signal(mod_sig, fs, fc, 'QPSK', bits)
        assert len(demod_bits) > 0, "QPSK demodulation failed"
        print(f"  ✓ QPSK demodulation (BER: {ber:.4f})")
        
        return True
    except Exception as e:
        print(f"  ✗ Error: {e}")
        traceback.print_exc()
        return False


def main():
    """Run all tests"""
    print("=" * 60)
    print("Signal Processing Package - Validation Tests")
    print("=" * 60)
    
    results = []
    
    # Run tests
    results.append(("Imports", test_imports()))
    results.append(("Signal Generator", test_signal_generator()))
    results.append(("Spectrum Analysis", test_spectrum()))
    results.append(("Filter Design", test_filters()))
    results.append(("Modulation", test_modulation()))
    results.append(("Demodulation", test_demodulation()))
    
    # Summary
    print("\n" + "=" * 60)
    print("Test Summary")
    print("=" * 60)
    
    passed = sum(1 for _, result in results if result)
    total = len(results)
    
    for name, result in results:
        status = "✓ PASS" if result else "✗ FAIL"
        print(f"{name:25s} {status}")
    
    print(f"\nTotal: {passed}/{total} tests passed")
    
    if passed == total:
        print("\n✓ All tests passed! Package is working correctly.")
        return 0
    else:
        print("\n✗ Some tests failed. Please review the errors above.")
        return 1


if __name__ == "__main__":
    sys.exit(main())
