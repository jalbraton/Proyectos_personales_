/**
 * ========================================
 * CANDLECOMPARATOR.JAVA - Comparison Tool
 * ========================================
 * 
 * Enunciado: Compara diferentes implementaciones de contadores
 * de velas (recursivo vs iterativo vs optimizado) midiendo
 * precisión, performance y uso de memoria.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class CandleComparator {
	
	private String comparisonName;
	
	public CandleComparator(String comparisonName) {
		this.comparisonName = comparisonName;
	}
	
	public void compareImplementations(long years) {
		System.out.println("========== CANDLE COUNTER COMPARISON ==========");
		System.out.println("Comparison: " + comparisonName);
		System.out.println("Input: " + years + " years");
		System.out.println();
		
		CandleCounterRecursive recursive = new CandleCounterRecursive();
		CandleCounterIterative iterative = new CandleCounterIterative();
		
		// Test recursive
		long startRecursive = System.nanoTime();
		long resultRecursive = recursive.count(years);
		long endRecursive = System.nanoTime();
		double timeRecursive = (endRecursive - startRecursive) / 1_000_000.0;
		
		// Test iterative
		long startIterative = System.nanoTime();
		long resultIterative = iterative.count(years);
		long endIterative = System.nanoTime();
		double timeIterative = (endIterative - startIterative) / 1_000_000.0;
		
		// Test optimized
		long startOptimized = System.nanoTime();
		long resultOptimized = recursive.countOptimized(years);
		long endOptimized = System.nanoTime();
		double timeOptimized = (endOptimized - startOptimized) / 1_000_000.0;
		
		// Print results
		System.out.println("RESULTS:");
		System.out.printf("  Recursive:  %,d candles | %.6f ms\n", resultRecursive, timeRecursive);
		System.out.printf("  Iterative:  %,d candles | %.6f ms\n", resultIterative, timeIterative);
		System.out.printf("  Optimized:  %,d candles | %.6f ms\n", resultOptimized, timeOptimized);
		System.out.println();
		
		// Verify correctness
		boolean allMatch = (resultRecursive == resultIterative) && 
		                   (resultIterative == resultOptimized);
		System.out.println("CORRECTNESS: " + (allMatch ? "✓ All implementations match" : "✗ Results differ"));
		System.out.println();
		
		// Performance analysis
		System.out.println("PERFORMANCE ANALYSIS:");
		double fastestTime = Math.min(timeRecursive, Math.min(timeIterative, timeOptimized));
		System.out.printf("  Recursive speedup: %.2fx\n", timeRecursive / fastestTime);
		System.out.printf("  Iterative speedup: %.2fx\n", timeIterative / fastestTime);
		System.out.printf("  Optimized speedup: %.2fx\n", timeOptimized / fastestTime);
		
		String fastest = timeOptimized < timeIterative ? "Optimized" : "Iterative";
		fastest = timeRecursive < Math.min(timeOptimized, timeIterative) ? "Recursive" : fastest;
		System.out.println("  Fastest: " + fastest);
		System.out.println("===============================================");
	}
	
	public void runBenchmarkSuite() {
		System.out.println("╔════════════════════════════════════════════╗");
		System.out.println("║   CANDLE COUNTER BENCHMARK SUITE          ║");
		System.out.println("╚════════════════════════════════════════════╝\n");
		
		long[] testCases = {5, 10, 50, 100, 500};
		
		for (long testCase : testCases) {
			compareImplementations(testCase);
			System.out.println();
		}
	}
	
	public boolean verifyCorrectnessRange(int maxYears) {
		CandleCounterRecursive recursive = new CandleCounterRecursive();
		CandleCounterIterative iterative = new CandleCounterIterative();
		
		System.out.println("Verifying correctness for years 1-" + maxYears + "...");
		
		for (int i = 1; i <= maxYears; i++) {
			long r = recursive.count(i);
			long it = iterative.count(i);
			long opt = recursive.countOptimized(i);
			
			if (r != it || it != opt) {
				System.out.println("✗ Mismatch at year " + i);
				System.out.println("  Recursive: " + r);
				System.out.println("  Iterative: " + it);
				System.out.println("  Optimized: " + opt);
				return false;
			}
		}
		
		System.out.println("✓ All implementations correct for years 1-" + maxYears);
		return true;
	}
	
	public static void main(String[] args) {
		CandleComparator comparator = new CandleComparator("Performance Analysis");
		
		// Single comparison
		comparator.compareImplementations(20);
		
		System.out.println("\n\n");
		
		// Full benchmark suite
		comparator.runBenchmarkSuite();
		
		System.out.println("\n");
		
		// Correctness verification
		comparator.verifyCorrectnessRange(100);
	}
}
