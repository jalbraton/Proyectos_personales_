/**
 * ========================================
 * CANDLEOPTIMIZER.JAVA - Optimization Engine
 * ========================================
 * 
 * Enunciado: Motor de optimización para el consumo de velas.
 * Busca la forma más eficiente de calcular valores, minimizar
 * recursos y maximizar rendimiento.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.HashMap;

public class CandleOptimizer {
	
	private String optimizerName;
	private HashMap<Long, Long> memoizationCache;
	private long cacheHits;
	private long cacheMisses;
	
	public CandleOptimizer(String optimizerName) {
		this.optimizerName = optimizerName;
		this.memoizationCache = new HashMap<Long, Long>();
		this.cacheHits = 0;
		this.cacheMisses = 0;
	}
	
	public long countWithMemoization(long years) {
		// Check cache first
		if (memoizationCache.containsKey(years)) {
			cacheHits++;
			return memoizationCache.get(years);
		}
		
		// Calculate and cache
		cacheMisses++;
		long result;
		if (years == 0) {
			result = 0;
		} else {
			result = years + countWithMemoization(years - 1);
		}
		
		memoizationCache.put(years, result);
		return result;
	}
	
	public long countOptimizedFormula(long years) {
		// Mathematical formula: sum = n * (n + 1) / 2
		// O(1) complexity, most efficient
		return (years * (years + 1)) / 2;
	}
	
	public long countWithIterativeDynamicProgramming(long years) {
		// Dynamic programming approach - builds from bottom up
		if (years == 0) return 0;
		
		long[] dp = new long[(int)years + 1];
		dp[0] = 0;
		
		for (int i = 1; i <= years; i++) {
			dp[i] = dp[i - 1] + i;
		}
		
		return dp[(int)years];
	}
	
	public void clearCache() {
		memoizationCache.clear();
		cacheHits = 0;
		cacheMisses = 0;
	}
	
	public void printCacheStatistics() {
		long totalAccesses = cacheHits + cacheMisses;
		double hitRate = totalAccesses > 0 ? (double) cacheHits / totalAccesses * 100 : 0;
		
		System.out.println("===== CACHE STATISTICS =====");
		System.out.println("Cache hits: " + cacheHits);
		System.out.println("Cache misses: " + cacheMisses);
		System.out.println("Total accesses: " + totalAccesses);
		System.out.printf("Hit rate: %.2f%%\n", hitRate);
		System.out.println("Cache size: " + memoizationCache.size() + " entries");
		System.out.println("===========================");
	}
	
	public void runOptimizationComparison(long years) {
		System.out.println("========== OPTIMIZATION COMPARISON ==========");
		System.out.println("Optimizer: " + optimizerName);
		System.out.println("Input: " + years + " years");
		System.out.println();
		
		// Clear cache for fair comparison
		clearCache();
		
		// Test memoization
		long startMemo = System.nanoTime();
		long resultMemo = countWithMemoization(years);
		long endMemo = System.nanoTime();
		double timeMemo = (endMemo - startMemo) / 1_000_000.0;
		
		// Test formula
		long startFormula = System.nanoTime();
		long resultFormula = countOptimizedFormula(years);
		long endFormula = System.nanoTime();
		double timeFormula = (endFormula - startFormula) / 1_000_000.0;
		
		// Test dynamic programming
		long startDP = System.nanoTime();
		long resultDP = countWithIterativeDynamicProgramming(years);
		long endDP = System.nanoTime();
		double timeDP = (endDP - startDP) / 1_000_000.0;
		
		// Print results
		System.out.println("RESULTS:");
		System.out.printf("  Memoization: %,d candles | %.6f ms\n", resultMemo, timeMemo);
		System.out.printf("  Formula:     %,d candles | %.6f ms\n", resultFormula, timeFormula);
		System.out.printf("  Dynamic Prog:%,d candles | %.6f ms\n", resultDP, timeDP);
		System.out.println();
		
		// Verify correctness
		boolean allMatch = (resultMemo == resultFormula) && (resultFormula == resultDP);
		System.out.println("CORRECTNESS: " + (allMatch ? "✓ All methods match" : "✗ Results differ"));
		System.out.println();
		
		// Print cache stats
		printCacheStatistics();
		
		// Speed comparison
		double fastestTime = Math.min(timeMemo, Math.min(timeFormula, timeDP));
		System.out.println("\nSPEED COMPARISON:");
		System.out.printf("  Memoization: %.2fx speed\n", timeMemo / fastestTime);
		System.out.printf("  Formula: %.2fx speed\n", timeFormula / fastestTime);
		System.out.printf("  Dynamic Prog: %.2fx speed\n", timeDP / fastestTime);
		System.out.println("============================================");
	}
	
	public void demonstrateMemoizationBenefit() {
		System.out.println("===== MEMOIZATION BENEFIT DEMONSTRATION =====");
		clearCache();
		
		System.out.println("First call - building cache:");
		long start1 = System.nanoTime();
		countWithMemoization(100);
		long end1 = System.nanoTime();
		System.out.printf("  Time: %.6f ms\n", (end1 - start1) / 1_000_000.0);
		printCacheStatistics();
		
		System.out.println("\nSecond call - using cache:");
		long start2 = System.nanoTime();
		countWithMemoization(100);
		long end2 = System.nanoTime();
		System.out.printf("  Time: %.6f ms\n", (end2 - start2) / 1_000_000.0);
		printCacheStatistics();
		
		double speedup = (double)(end1 - start1) / (end2 - start2);
		System.out.printf("\nSpeedup: %.2fx faster with cache!\n", speedup);
		System.out.println("=============================================");
	}
	
	public static void main(String[] args) {
		CandleOptimizer optimizer = new CandleOptimizer("High-Performance Engine");
		
		// Run optimization comparison
		optimizer.runOptimizationComparison(50);
		
		System.out.println("\n\n");
		
		// Demonstrate memoization benefit
		optimizer.demonstrateMemoizationBenefit();
		
		System.out.println("\n\n");
		
		// Large-scale test
		System.out.println("===== LARGE-SCALE PERFORMANCE TEST =====");
		optimizer.clearCache();
		
		long[] testSizes = {100, 500, 1000, 5000, 10000};
		for (long size : testSizes) {
			long start = System.nanoTime();
			long result = optimizer.countOptimizedFormula(size);
			long end = System.nanoTime();
			
			System.out.printf("Count(%,d): %,d candles | %.6f ms\n", 
			                  size, result, (end - start) / 1_000_000.0);
		}
		System.out.println("========================================");
	}
}
