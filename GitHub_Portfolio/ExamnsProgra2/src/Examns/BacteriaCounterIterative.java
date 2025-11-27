/**
 * ========================================
 * BACTERIACOUNTERITERATIVE.JAVA
 * ========================================
 * 
 * Enunciado: Contador iterativo de bacterias. Simula el
 * crecimiento exponencial de bacterias que se duplican
 * cada día: count(n) = 2^n
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class BacteriaCounterIterative {

	public String getImplementationDescription() {
		return "Iterative implementation of bacteria counter - calculates exponential growth 2^n";
	}

	public long count(int days) {
		if (days == 0) {
			return 1; // Día 0: 1 bacteria inicial
		}
		long bacteria = 1;
		for (int i = 0; i < days; i++) {
			bacteria *= 2;
		}
		return bacteria;
	}
	
	public long countWithInitialPopulation(int days, long initialPopulation) {
		long bacteria = initialPopulation;
		for (int i = 0; i < days; i++) {
			bacteria *= 2;
		}
		return bacteria;
	}
	
	public double countWithGrowthRate(int days, double growthRate) {
		// Crecimiento con tasa personalizada (e.g., 1.5 = 150%)
		double bacteria = 1.0;
		for (int i = 0; i < days; i++) {
			bacteria *= growthRate;
		}
		return bacteria;
	}
	
	public void printGrowthSequence(int days) {
		System.out.println("Bacteria growth sequence:");
		long bacteria = 1;
		System.out.println("Day 0: " + bacteria + " bacteria");
		for (int i = 1; i <= days; i++) {
			bacteria *= 2;
			System.out.printf("Day %2d: %,d bacteria (2^%d)\n", i, bacteria, i);
		}
	}
	
	public long[] getGrowthArray(int days) {
		long[] growth = new long[days + 1];
		growth[0] = 1;
		for (int i = 1; i <= days; i++) {
			growth[i] = growth[i - 1] * 2;
		}
		return growth;
	}

	public static void main(String args[]) {
		BacteriaCounterIterative counter = new BacteriaCounterIterative();
		
		System.out.println("===== BACTERIA COUNTER ITERATIVE =====");
		System.out.println(counter.getImplementationDescription());
		System.out.println();
		
		int days = 6;
		System.out.println("Bacteria after " + days + " days: " + counter.count(days));
		System.out.println("Expected: " + (long)Math.pow(2, days));
		System.out.println();
		
		counter.printGrowthSequence(10);
		
		System.out.println("\nCustom initial population:");
		System.out.println("100 bacteria after 5 days: " + 
		                   counter.countWithInitialPopulation(5, 100));
		
		System.out.println("\nCustom growth rate (1.5x per day):");
		System.out.printf("After 10 days: %.2f bacteria\n", 
		                  counter.countWithGrowthRate(10, 1.5));
		
		System.out.println("\nLarge day calculations:");
		System.out.println("Day 20: " + counter.count(20));
		System.out.println("Day 30: " + counter.count(30));
	}
	
}
