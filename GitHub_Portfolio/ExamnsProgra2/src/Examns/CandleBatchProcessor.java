/**
 * ========================================
 * CANDLEBATCHPROCESSOR.JAVA - Batch Processing
 * ========================================
 * 
 * Enunciado: Procesador por lotes para calcular múltiples
 * valores de velas simultáneamente. Optimizado para procesar
 * grandes volúmenes de datos de forma eficiente.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class CandleBatchProcessor {
	
	private String processorName;
	private CandleCounterIterative counter;
	private ArrayList<Long> batchResults;
	
	public CandleBatchProcessor(String processorName) {
		this.processorName = processorName;
		this.counter = new CandleCounterIterative();
		this.batchResults = new ArrayList<Long>();
	}
	
	public long[] processRange(long startYear, long endYear) {
		int size = (int)(endYear - startYear + 1);
		long[] results = new long[size];
		
		for (int i = 0; i < size; i++) {
			results[i] = counter.count(startYear + i);
		}
		
		return results;
	}
	
	public long[] processMultipleValues(long[] years) {
		long[] results = new long[years.length];
		
		for (int i = 0; i < years.length; i++) {
			results[i] = counter.count(years[i]);
		}
		
		return results;
	}
	
	public void processBatch(long[] years) {
		batchResults.clear();
		
		System.out.println("===== BATCH PROCESSING: " + processorName + " =====");
		System.out.println("Processing " + years.length + " values...");
		System.out.println();
		
		long startTime = System.nanoTime();
		
		for (int i = 0; i < years.length; i++) {
			long result = counter.count(years[i]);
			batchResults.add(result);
			
			System.out.printf("  [%3d/%3d] Year %4d → %,10d candles\n", 
			                  i + 1, years.length, years[i], result);
		}
		
		long endTime = System.nanoTime();
		double processingTime = (endTime - startTime) / 1_000_000.0;
		
		System.out.println();
		System.out.printf("✓ Batch completed in %.3f ms\n", processingTime);
		System.out.printf("  Average time per item: %.6f ms\n", 
		                  processingTime / years.length);
		System.out.println("==============================================");
	}
	
	public long calculateBatchSum() {
		long sum = 0;
		for (Long result : batchResults) {
			sum += result;
		}
		return sum;
	}
	
	public double calculateBatchAverage() {
		if (batchResults.isEmpty()) return 0.0;
		return (double) calculateBatchSum() / batchResults.size();
	}
	
	public long findBatchMaximum() {
		if (batchResults.isEmpty()) return 0;
		
		long max = batchResults.get(0);
		for (Long result : batchResults) {
			if (result > max) {
				max = result;
			}
		}
		return max;
	}
	
	public long findBatchMinimum() {
		if (batchResults.isEmpty()) return 0;
		
		long min = batchResults.get(0);
		for (Long result : batchResults) {
			if (result < min) {
				min = result;
			}
		}
		return min;
	}
	
	public void printBatchStatistics() {
		System.out.println("===== BATCH STATISTICS =====");
		System.out.println("Items processed: " + batchResults.size());
		System.out.printf("Total sum: %,d\n", calculateBatchSum());
		System.out.printf("Average: %.2f\n", calculateBatchAverage());
		System.out.printf("Maximum: %,d\n", findBatchMaximum());
		System.out.printf("Minimum: %,d\n", findBatchMinimum());
		System.out.println("===========================");
	}
	
	public void parallelProcessingSimulation(long[] years) {
		System.out.println("===== PARALLEL PROCESSING SIMULATION =====");
		System.out.println("Simulating multi-threaded batch processing...");
		System.out.println("Batch size: " + years.length);
		System.out.println();
		
		int numThreads = 4;
		int itemsPerThread = years.length / numThreads;
		
		long totalTime = 0;
		
		for (int t = 0; t < numThreads; t++) {
			int start = t * itemsPerThread;
			int end = (t == numThreads - 1) ? years.length : (t + 1) * itemsPerThread;
			int threadItems = end - start;
			
			System.out.printf("Thread %d: Processing items %d-%d (%d items)\n", 
			                  t + 1, start, end - 1, threadItems);
			
			long threadStart = System.nanoTime();
			for (int i = start; i < end; i++) {
				counter.count(years[i]);
			}
			long threadEnd = System.nanoTime();
			double threadTime = (threadEnd - threadStart) / 1_000_000.0;
			
			System.out.printf("  Thread %d completed in %.3f ms\n", t + 1, threadTime);
			
			if (threadTime > totalTime) {
				totalTime = (long) threadTime;
			}
		}
		
		System.out.println();
		System.out.printf("✓ All threads completed\n");
		System.out.printf("  Longest thread time: %.3f ms\n", (double)totalTime);
		System.out.println("==========================================");
	}
	
	public static void main(String[] args) {
		CandleBatchProcessor processor = new CandleBatchProcessor("Production Processor");
		
		// Process a range
		System.out.println("RANGE PROCESSING:");
		long[] range = processor.processRange(1, 20);
		System.out.println("Processed range 1-20: " + range.length + " values");
		System.out.println();
		
		// Process specific values
		long[] specificYears = {5, 10, 15, 20, 25, 50, 100, 500, 1000};
		processor.processBatch(specificYears);
		System.out.println();
		
		processor.printBatchStatistics();
		System.out.println("\n");
		
		// Parallel processing simulation
		long[] largeSet = new long[100];
		for (int i = 0; i < largeSet.length; i++) {
			largeSet[i] = i + 1;
		}
		processor.parallelProcessingSimulation(largeSet);
	}
}
