/**
 * ========================================
 * CANDLESTATISTICS.JAVA - Candle Statistics Manager
 * ========================================
 * 
 * Enunciado: Gestiona estadísticas avanzadas sobre el uso de velas
 * a lo largo de múltiples años. Calcula promedios, desviaciones y
 * proyecciones futuras basadas en datos históricos.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class CandleStatistics {
	
	private ArrayList<Long> yearlyConsumption;
	private String statisticsName;
	
	public CandleStatistics(String statisticsName) {
		this.statisticsName = statisticsName;
		this.yearlyConsumption = new ArrayList<Long>();
	}
	
	public void addYear(long candles) {
		yearlyConsumption.add(candles);
	}
	
	public void generateDataFromRecursive(int years) {
		CandleCounterRecursive counter = new CandleCounterRecursive();
		yearlyConsumption.clear();
		for (int i = 1; i <= years; i++) {
			yearlyConsumption.add(counter.count(i));
		}
	}
	
	public double calculateAverage() {
		if (yearlyConsumption.isEmpty()) return 0.0;
		
		long sum = 0;
		for (Long candles : yearlyConsumption) {
			sum += candles;
		}
		return (double) sum / yearlyConsumption.size();
	}
	
	public long calculateTotal() {
		long sum = 0;
		for (Long candles : yearlyConsumption) {
			sum += candles;
		}
		return sum;
	}
	
	public long getMaximum() {
		if (yearlyConsumption.isEmpty()) return 0;
		
		long max = yearlyConsumption.get(0);
		for (Long candles : yearlyConsumption) {
			if (candles > max) {
				max = candles;
			}
		}
		return max;
	}
	
	public long getMinimum() {
		if (yearlyConsumption.isEmpty()) return 0;
		
		long min = yearlyConsumption.get(0);
		for (Long candles : yearlyConsumption) {
			if (candles < min) {
				min = candles;
			}
		}
		return min;
	}
	
	public double calculateStandardDeviation() {
		if (yearlyConsumption.isEmpty()) return 0.0;
		
		double avg = calculateAverage();
		double sumSquaredDiff = 0.0;
		
		for (Long candles : yearlyConsumption) {
			double diff = candles - avg;
			sumSquaredDiff += diff * diff;
		}
		
		return Math.sqrt(sumSquaredDiff / yearlyConsumption.size());
	}
	
	public long predictNextYear() {
		if (yearlyConsumption.isEmpty()) return 0;
		
		// Predicción simple: último valor + incremento promedio
		if (yearlyConsumption.size() == 1) {
			return yearlyConsumption.get(0) + 1;
		}
		
		long lastValue = yearlyConsumption.get(yearlyConsumption.size() - 1);
		long secondLast = yearlyConsumption.get(yearlyConsumption.size() - 2);
		long increment = lastValue - secondLast;
		
		return lastValue + increment;
	}
	
	public void printFullReport() {
		System.out.println("========== CANDLE STATISTICS REPORT ==========");
		System.out.println("Report: " + statisticsName);
		System.out.println("Years recorded: " + yearlyConsumption.size());
		System.out.println();
		
		System.out.println("CONSUMPTION DATA:");
		for (int i = 0; i < yearlyConsumption.size(); i++) {
			System.out.printf("  Year %2d: %,d candles\n", i + 1, yearlyConsumption.get(i));
		}
		System.out.println();
		
		System.out.println("STATISTICAL SUMMARY:");
		System.out.printf("  Total consumed: %,d candles\n", calculateTotal());
		System.out.printf("  Average per year: %.2f candles\n", calculateAverage());
		System.out.printf("  Maximum: %,d candles\n", getMaximum());
		System.out.printf("  Minimum: %,d candles\n", getMinimum());
		System.out.printf("  Standard deviation: %.2f\n", calculateStandardDeviation());
		System.out.printf("  Predicted next year: %,d candles\n", predictNextYear());
		System.out.println("==============================================");
	}
	
	public static void main(String[] args) {
		CandleStatistics stats = new CandleStatistics("10-Year Analysis");
		
		// Generate data from recursive counter
		stats.generateDataFromRecursive(10);
		
		// Print full report
		stats.printFullReport();
		
		// Manual data entry example
		System.out.println("\n\nMANUAL DATA ENTRY EXAMPLE:");
		CandleStatistics customStats = new CandleStatistics("Custom Data");
		customStats.addYear(5);
		customStats.addYear(12);
		customStats.addYear(23);
		customStats.addYear(40);
		customStats.addYear(65);
		
		customStats.printFullReport();
	}
}
