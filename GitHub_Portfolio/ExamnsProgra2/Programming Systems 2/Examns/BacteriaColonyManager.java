/**
 * ========================================
 * BACTERIACOLONYMANAGER.JAVA - Colony Management
 * ========================================
 * 
 * Enunciado: Gestor de múltiples colonias de bacterias.
 * Administra poblaciones independientes, recursos compartidos
 * y competencia entre colonias.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class BacteriaColonyManager {
	
	private ArrayList<Colony> colonies;
	private String managerName;
	private long totalResourcePool;
	
	private class Colony {
		String name;
		long population;
		double growthRate;
		int foundedDay;
		
		Colony(String name, long initialPopulation, double growthRate, int foundedDay) {
			this.name = name;
			this.population = initialPopulation;
			this.growthRate = growthRate;
			this.foundedDay = foundedDay;
		}
		
		void grow() {
			population = (long)(population * growthRate);
		}
		
		void consumeResources(long amount) {
			// Resource consumption affects growth
			if (amount > totalResourcePool / 10) {
				growthRate *= 0.9; // Reduce growth if over-consuming
			}
		}
	}
	
	public BacteriaColonyManager(String managerName, long totalResourcePool) {
		this.managerName = managerName;
		this.totalResourcePool = totalResourcePool;
		this.colonies = new ArrayList<Colony>();
	}
	
	public void addColony(String name, long initialPopulation, double growthRate) {
		Colony colony = new Colony(name, initialPopulation, growthRate, colonies.size());
		colonies.add(colony);
		System.out.println("Colony '" + name + "' added with " + initialPopulation + 
		                   " bacteria (growth rate: " + growthRate + "x)");
	}
	
	public void simulateDays(int days) {
		System.out.println("========== COLONY SIMULATION ==========");
		System.out.println("Manager: " + managerName);
		System.out.println("Resource Pool: " + String.format("%,d", totalResourcePool));
		System.out.println("Colonies: " + colonies.size());
		System.out.println("Duration: " + days + " days");
		System.out.println();
		
		for (int day = 1; day <= days; day++) {
			System.out.println("=== DAY " + day + " ===");
			
			long totalPopulation = 0;
			
			// Each colony grows
			for (Colony colony : colonies) {
				long previousPopulation = colony.population;
				colony.grow();
				
				// Consume resources based on population
				long resourcesNeeded = colony.population / 100;
				colony.consumeResources(resourcesNeeded);
				totalResourcePool -= resourcesNeeded;
				
				totalPopulation += colony.population;
				
				System.out.printf("  %s: %,d → %,d bacteria (%.1f%% growth)\n", 
				                  colony.name, previousPopulation, colony.population,
				                  ((double)(colony.population - previousPopulation) / 
				                   previousPopulation * 100));
			}
			
			System.out.printf("Total population: %,d bacteria\n", totalPopulation);
			System.out.printf("Remaining resources: %,d\n", totalResourcePool);
			System.out.println();
			
			// Check resource depletion
			if (totalResourcePool <= 0) {
				System.out.println("!!! RESOURCES DEPLETED - Simulation ended !!!");
				break;
			}
		}
		
		System.out.println("========================================");
	}
	
	public void printColonySummary() {
		System.out.println("===== COLONY SUMMARY =====");
		System.out.println("Manager: " + managerName);
		System.out.println();
		
		long totalPopulation = 0;
		Colony largestColony = colonies.get(0);
		Colony smallestColony = colonies.get(0);
		
		for (Colony colony : colonies) {
			totalPopulation += colony.population;
			
			if (colony.population > largestColony.population) {
				largestColony = colony;
			}
			if (colony.population < smallestColony.population) {
				smallestColony = colony;
			}
		}
		
		System.out.println("STATISTICS:");
		System.out.printf("  Total colonies: %d\n", colonies.size());
		System.out.printf("  Total population: %,d bacteria\n", totalPopulation);
		System.out.printf("  Average per colony: %,d bacteria\n", 
		                  totalPopulation / colonies.size());
		System.out.println();
		
		System.out.println("EXTREMES:");
		System.out.printf("  Largest: %s (%,d bacteria)\n", 
		                  largestColony.name, largestColony.population);
		System.out.printf("  Smallest: %s (%,d bacteria)\n", 
		                  smallestColony.name, smallestColony.population);
		System.out.println();
		
		System.out.println("DETAILED LIST:");
		for (int i = 0; i < colonies.size(); i++) {
			Colony c = colonies.get(i);
			double percentage = (double)c.population / totalPopulation * 100;
			
			System.out.printf("  %d. %-15s | %,12d bacteria | %.2f%% | Rate: %.2fx\n", 
			                  i + 1, c.name, c.population, percentage, c.growthRate);
		}
		
		System.out.printf("\nResources remaining: %,d\n", totalResourcePool);
		System.out.println("==========================");
	}
	
	public void rankColonies() {
		// Sort colonies by population (simple bubble sort)
		for (int i = 0; i < colonies.size() - 1; i++) {
			for (int j = 0; j < colonies.size() - i - 1; j++) {
				if (colonies.get(j).population < colonies.get(j + 1).population) {
					Colony temp = colonies.get(j);
					colonies.set(j, colonies.get(j + 1));
					colonies.set(j + 1, temp);
				}
			}
		}
		
		System.out.println("===== COLONY RANKING =====");
		for (int i = 0; i < colonies.size(); i++) {
			Colony c = colonies.get(i);
			String medal = i == 0 ? "🥇" : (i == 1 ? "🥈" : (i == 2 ? "🥉" : "  "));
			
			System.out.printf("%s #%d: %-15s | %,d bacteria\n", 
			                  medal, i + 1, c.name, c.population);
		}
		System.out.println("==========================");
	}
	
	public static void main(String[] args) {
		BacteriaColonyManager manager = new BacteriaColonyManager(
			"Multi-Colony Lab", 1_000_000
		);
		
		// Add colonies with different growth rates
		manager.addColony("Alpha", 100, 2.0);
		manager.addColony("Beta", 100, 2.0);
		manager.addColony("Gamma", 100, 2.0);
		manager.addColony("Delta", 50, 2.5);
		manager.addColony("Epsilon", 200, 1.8);
		
		System.out.println("\n");
		
		// Simulate 10 days
		manager.simulateDays(10);
		
		System.out.println("\n");
		
		// Print summary
		manager.printColonySummary();
		
		System.out.println("\n");
		
		// Rank colonies
		manager.rankColonies();
	}
}
