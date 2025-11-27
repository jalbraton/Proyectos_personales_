/**
 * ========================================
 * CANDLECOUNTERRECURSIVE.JAVA
 * ========================================
 * 
 * Enunciado: Contador recursivo de velas. Calcula el número
 * total de velas utilizadas a lo largo de los años.
 * Formula: count(n) = n + count(n-1), donde count(0) = 0
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class CandleCounterRecursive {

	public String getImplementationDescription() {
		return "Recursive implementation of candle counter - uses recursive approach with base case 0";
	}

	public long count(long years) {
		if (years == 0) {
			return 0;
		} else {
			return years + count(years - 1);
		}
	}
	
	public long countOptimized(long years) {
		// Formula matemática: sum = n * (n + 1) / 2
		return (years * (years + 1)) / 2;
	}
	
	public void printSequence(long years) {
		System.out.println("Candle sequence for " + years + " years:");
		for (long i = 1; i <= years; i++) {
			System.out.println("Year " + i + ": " + count(i) + " candles");
		}
	}

	public static void main(String[] args) {
		CandleCounterRecursive counter = new CandleCounterRecursive();
		
		System.out.println("===== CANDLE COUNTER RECURSIVE =====");
		System.out.println(counter.getImplementationDescription());
		System.out.println();
		
		long years = 6;
		long result = counter.count(years);
		System.out.println("Candles for " + years + " years (recursive): " + result);
		System.out.println("Candles for " + years + " years (optimized): " + counter.countOptimized(years));
		System.out.println();
		
		counter.printSequence(10);
		
		// Test larger values
		System.out.println("\nLarge value tests:");
		System.out.println("Count(20): " + counter.count(20));
		System.out.println("Count(50): " + counter.countOptimized(50)); // Use optimized for large values
		System.out.println("Count(100): " + counter.countOptimized(100));
	}

}
