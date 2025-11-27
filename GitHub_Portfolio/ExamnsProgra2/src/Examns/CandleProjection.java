/**
 * ========================================
 * CANDLEPROJECTION.JAVA - Future Projections
 * ========================================
 * 
 * Enunciado: Realiza proyecciones futuras del consumo de velas
 * basándose en datos históricos. Calcula tendencias, estima
 * necesidades futuras y optimiza pedidos.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class CandleProjection {
	
	private String projectionName;
	private CandleCounterRecursive counter;
	private ArrayList<Long> historicalData;
	
	public CandleProjection(String projectionName) {
		this.projectionName = projectionName;
		this.counter = new CandleCounterRecursive();
		this.historicalData = new ArrayList<Long>();
	}
	
	public void loadHistoricalData(int years) {
		historicalData.clear();
		for (int i = 1; i <= years; i++) {
			historicalData.add(counter.count(i));
		}
	}
	
	public long projectLinear(int futureYears) {
		if (historicalData.size() < 2) {
			return 0;
		}
		
		// Calculate average growth rate
		long totalGrowth = 0;
		for (int i = 1; i < historicalData.size(); i++) {
			totalGrowth += historicalData.get(i) - historicalData.get(i - 1);
		}
		
		long avgGrowth = totalGrowth / (historicalData.size() - 1);
		long lastValue = historicalData.get(historicalData.size() - 1);
		
		return lastValue + (avgGrowth * futureYears);
	}
	
	public long projectExponential(int futureYears) {
		if (historicalData.isEmpty()) {
			return 0;
		}
		
		// Use mathematical formula: n*(n+1)/2
		int lastYear = historicalData.size();
		int projectedYear = lastYear + futureYears;
		
		return counter.countOptimized(projectedYear);
	}
	
	public long[] projectRange(int startYear, int endYear) {
		int range = endYear - startYear + 1;
		long[] projections = new long[range];
		
		for (int i = 0; i < range; i++) {
			projections[i] = counter.count(startYear + i);
		}
		
		return projections;
	}
	
	public long calculateTotalNeed(int fromYear, int toYear) {
		long total = 0;
		for (int i = fromYear; i <= toYear; i++) {
			total += counter.count(i);
		}
		return total;
	}
	
	public void optimizeOrderSchedule(int years, int orderFrequency) {
		System.out.println("===== OPTIMIZED ORDER SCHEDULE =====");
		System.out.println("Planning for " + years + " years");
		System.out.println("Order frequency: every " + orderFrequency + " years");
		System.out.println();
		
		for (int i = orderFrequency; i <= years; i += orderFrequency) {
			long needForPeriod = calculateTotalNeed(i - orderFrequency + 1, i);
			System.out.printf("Order for years %d-%d: %,d candles\n", 
			                  i - orderFrequency + 1, i, needForPeriod);
		}
		
		// Handle remaining years
		if (years % orderFrequency != 0) {
			int lastOrderYear = (years / orderFrequency) * orderFrequency;
			long remainingNeed = calculateTotalNeed(lastOrderYear + 1, years);
			System.out.printf("Final order for years %d-%d: %,d candles\n", 
			                  lastOrderYear + 1, years, remainingNeed);
		}
		
		long totalNeed = calculateTotalNeed(1, years);
		System.out.printf("\nTotal candles needed: %,d\n", totalNeed);
		System.out.println("====================================");
	}
	
	public void printProjectionReport(int historicalYears, int futureYears) {
		loadHistoricalData(historicalYears);
		
		System.out.println("========== PROJECTION REPORT: " + projectionName + " ==========");
		System.out.println("Historical data: " + historicalYears + " years");
		System.out.println("Projection horizon: " + futureYears + " years");
		System.out.println();
		
		System.out.println("HISTORICAL CONSUMPTION:");
		for (int i = 0; i < historicalData.size(); i++) {
			System.out.printf("  Year %2d: %,d candles\n", i + 1, historicalData.get(i));
		}
		System.out.println();
		
		System.out.println("FUTURE PROJECTIONS:");
		long linearProj = projectLinear(futureYears);
		long exponentialProj = projectExponential(futureYears);
		
		System.out.printf("  Linear projection (+%d years): %,d candles\n", 
		                  futureYears, linearProj);
		System.out.printf("  Exponential projection (+%d years): %,d candles\n", 
		                  futureYears, exponentialProj);
		System.out.println();
		
		System.out.println("YEAR-BY-YEAR PROJECTIONS:");
		long[] range = projectRange(historicalYears + 1, historicalYears + futureYears);
		for (int i = 0; i < range.length; i++) {
			System.out.printf("  Year %2d: %,d candles\n", 
			                  historicalYears + i + 1, range[i]);
		}
		System.out.println();
		
		long totalFutureNeed = calculateTotalNeed(historicalYears + 1, 
		                                          historicalYears + futureYears);
		System.out.printf("Total need for next %d years: %,d candles\n", 
		                  futureYears, totalFutureNeed);
		System.out.println("========================================================");
	}
	
	public static void main(String[] args) {
		CandleProjection projection = new CandleProjection("5-Year Forecast");
		
		// Full projection report
		projection.printProjectionReport(10, 5);
		
		System.out.println("\n\n");
		
		// Optimize order schedule
		CandleProjection orderPlanner = new CandleProjection("Order Planning");
		orderPlanner.optimizeOrderSchedule(20, 3);
		
		System.out.println("\n");
		
		// Quick projection examples
		projection.loadHistoricalData(15);
		System.out.println("Quick projections:");
		System.out.println("Linear +5 years: " + projection.projectLinear(5));
		System.out.println("Exponential +5 years: " + projection.projectExponential(5));
		System.out.println("Total need years 16-20: " + projection.calculateTotalNeed(16, 20));
	}
}
