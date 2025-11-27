/**
 * ========================================
 * CANDLECOUNTERITERATIVE.JAVA
 * ========================================
 * 
 * Enunciado: Contador iterativo de velas. Implementación
 * iterativa que suma todas las velas de 0 a n años.
 * Más eficiente que la versión recursiva para valores grandes.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class CandleCounterIterative {

	public String getImplementationDescription() {
		return "Iterative implementation of candle counter - uses loop to sum all years";
	}

	public long count(long years) {
		long sum = 0;
		for (long i = 0; i <= years; i++) {
			sum += i;
		}
		return sum;
	}
	
	public long countReverse(long years) {
		// Cuenta desde years hacia 0
		long sum = 0;
		for (long i = years; i >= 0; i--) {
			sum += i;
		}
		return sum;
	}
	
	public long[] getCandlesPerYear(long years) {
		long[] candles = new long[(int)years + 1];
		long sum = 0;
		for (int i = 0; i <= years; i++) {
			sum += i;
			candles[i] = sum;
		}
		return candles;
	}
	
	public void printYearlyBreakdown(long years) {
		System.out.println("Yearly candle breakdown:");
		long cumulative = 0;
		for (long i = 1; i <= years; i++) {
			cumulative += i;
			System.out.printf("Year %2d: +%2d candles | Total: %d\n", i, i, cumulative);
		}
	}

	public static void main(String[] args) {
		CandleCounterIterative counter = new CandleCounterIterative();
		
		System.out.println("===== CANDLE COUNTER ITERATIVE =====");
		System.out.println(counter.getImplementationDescription());
		System.out.println();
		
		long years = 6;
		System.out.println("Candles for " + years + " years (iterative): " + counter.count(years));
		System.out.println("Candles for " + years + " years (reverse): " + counter.countReverse(years));
		System.out.println();
		
		counter.printYearlyBreakdown(10);
		
		// Test performance with large values
		System.out.println("\nLarge value tests:");
		long start = System.nanoTime();
		long result = counter.count(1000000);
		long end = System.nanoTime();
		System.out.println("Count(1,000,000): " + result);
		System.out.println("Time: " + (end - start) / 1_000_000.0 + " ms");
	}

}
