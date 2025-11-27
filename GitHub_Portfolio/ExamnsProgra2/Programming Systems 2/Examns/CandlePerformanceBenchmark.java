/**
 * ========================================
 * CANDLEPERFORMANCEBENCHMARK.JAVA - Benchmarking
 * ========================================
 * 
 * Enunciado: Herramienta de benchmarking para medir y comparar
 * el rendimiento de diferentes algoritmos de conteo de velas.
 * Genera métricas detalladas de tiempo, memoria y escalabilidad.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class CandlePerformanceBenchmark {
	
	private String benchmarkName;
	private CandleCounterIterative counter;
	
	public CandlePerformanceBenchmark(String benchmarkName) {
		this.benchmarkName = benchmarkName;
		this.counter = new CandleCounterIterative();
	}
	
	private String repeatString(String str, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public double benchmarkSingleExecution(long years) {
		long startTime = System.nanoTime();
		counter.count(years);
		long endTime = System.nanoTime();
		
		return (endTime - startTime) / 1_000_000.0; // Convert to milliseconds
	}
	
	public double benchmarkAverageTime(long years, int iterations) {
		double totalTime = 0.0;
		
		for (int i = 0; i < iterations; i++) {
			totalTime += benchmarkSingleExecution(years);
		}
		
		return totalTime / iterations;
	}
	
	public void runScalabilityTest() {
		System.out.println("========== SCALABILITY TEST ==========");
		System.out.println("Benchmark: " + benchmarkName);
		System.out.println();
		
		long[] testSizes = {10, 50, 100, 500, 1000, 5000, 10000, 50000, 100000};
		
		System.out.println("┌────────────┬──────────────┬────────────┐");
		System.out.println("│    Size    │     Time     │  Per Item  │");
		System.out.println("├────────────┼──────────────┼────────────┤");
		
		for (long size : testSizes) {
			double time = benchmarkSingleExecution(size);
			double perItem = time / size * 1000; // microseconds per item
			
			System.out.printf("│ %,9d  │ %8.3f ms  │ %7.3f µs │\n", 
			                  size, time, perItem);
		}
		
		System.out.println("└────────────┴──────────────┴────────────┘");
		System.out.println("======================================");
	}
	
	public void runConsistencyTest(long years, int iterations) {
		System.out.println("========== CONSISTENCY TEST ==========");
		System.out.println("Testing: " + years + " years");
		System.out.println("Iterations: " + iterations);
		System.out.println();
		
		double[] times = new double[iterations];
		
		for (int i = 0; i < iterations; i++) {
			times[i] = benchmarkSingleExecution(years);
		}
		
		// Calculate statistics
		double sum = 0.0;
		double min = times[0];
		double max = times[0];
		
		for (double time : times) {
			sum += time;
			if (time < min) min = time;
			if (time > max) max = time;
		}
		
		double average = sum / iterations;
		
		// Calculate standard deviation
		double sumSquaredDiff = 0.0;
		for (double time : times) {
			double diff = time - average;
			sumSquaredDiff += diff * diff;
		}
		double stdDev = Math.sqrt(sumSquaredDiff / iterations);
		
		System.out.println("RESULTS:");
		System.out.printf("  Average:   %.6f ms\n", average);
		System.out.printf("  Minimum:   %.6f ms\n", min);
		System.out.printf("  Maximum:   %.6f ms\n", max);
		System.out.printf("  Std Dev:   %.6f ms\n", stdDev);
		System.out.printf("  Range:     %.6f ms\n", max - min);
		System.out.printf("  Variance:  %.2f%%\n", (stdDev / average * 100));
		System.out.println("======================================");
	}
	
	public void runThroughputTest(int duration) {
		System.out.println("========== THROUGHPUT TEST ==========");
		System.out.println("Duration: " + duration + " seconds");
		System.out.println();
		
		long startTime = System.currentTimeMillis();
		long endTime = startTime + (duration * 1000);
		long operations = 0;
		
		System.out.println("Running...");
		
		while (System.currentTimeMillis() < endTime) {
			counter.count(100); // Standard size operation
			operations++;
			
			// Print progress every second
			long elapsed = System.currentTimeMillis() - startTime;
			if (elapsed % 1000 == 0 && operations % 100 == 0) {
				System.out.printf("  %d seconds: %,d operations\n", 
				                  elapsed / 1000, operations);
			}
		}
		
		double actualDuration = (System.currentTimeMillis() - startTime) / 1000.0;
		double throughput = operations / actualDuration;
		
		System.out.println();
		System.out.println("RESULTS:");
		System.out.printf("  Total operations: %,d\n", operations);
		System.out.printf("  Actual duration:  %.2f seconds\n", actualDuration);
		System.out.printf("  Throughput:       %,.2f ops/second\n", throughput);
		System.out.printf("  Avg time/op:      %.6f ms\n", 1000.0 / throughput);
		System.out.println("=====================================");
	}
	
	public void runMemoryEstimation(long years) {
		System.out.println("========== MEMORY ESTIMATION ==========");
		System.out.println("Input: " + years + " years");
		System.out.println();
		
		// Estimate memory usage
		Runtime runtime = Runtime.getRuntime();
		
		// Force garbage collection
		System.gc();
		
		long memBefore = runtime.totalMemory() - runtime.freeMemory();
		
		// Execute operation
		counter.count(years);
		
		long memAfter = runtime.totalMemory() - runtime.freeMemory();
		long memUsed = memAfter - memBefore;
		
		System.out.println("MEMORY ANALYSIS:");
		System.out.printf("  Before:    %,d bytes\n", memBefore);
		System.out.printf("  After:     %,d bytes\n", memAfter);
		System.out.printf("  Used:      %,d bytes\n", memUsed);
		System.out.printf("  Used (KB): %,.2f KB\n", memUsed / 1024.0);
		System.out.printf("  Used (MB): %,.2f MB\n", memUsed / (1024.0 * 1024.0));
		System.out.println();
		
		System.out.println("HEAP STATUS:");
		System.out.printf("  Total:     %,d bytes\n", runtime.totalMemory());
		System.out.printf("  Free:      %,d bytes\n", runtime.freeMemory());
		System.out.printf("  Max:       %,d bytes\n", runtime.maxMemory());
		System.out.println("=======================================");
	}
	
	public void runComprehensiveBenchmark() {
		System.out.println("╔════════════════════════════════════════════════╗");
		System.out.println("║    COMPREHENSIVE PERFORMANCE BENCHMARK         ║");
		System.out.println("╚════════════════════════════════════════════════╝\n");
		
		System.out.println("Benchmark Suite: " + benchmarkName);
		System.out.println();
		
		// Scalability test
		runScalabilityTest();
		System.out.println("\n");
		
		// Consistency test
		runConsistencyTest(1000, 100);
		System.out.println("\n");
		
		// Memory estimation
		runMemoryEstimation(10000);
		System.out.println("\n");
		
		System.out.println(repeatString("═", 50));
		System.out.println("BENCHMARK COMPLETED");
		System.out.println(repeatString("═", 50));
	}
	
	public static void main(String[] args) {
		CandlePerformanceBenchmark benchmark = 
			new CandlePerformanceBenchmark("Iterative Counter Benchmark v1.0");
		
		// Run comprehensive benchmark
		benchmark.runComprehensiveBenchmark();
		
		System.out.println("\n\n");
		
		// Additional throughput test
		System.out.println("ADDITIONAL TESTS:");
		benchmark.runThroughputTest(5);
	}
}
