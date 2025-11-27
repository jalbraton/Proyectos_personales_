/**
 * ========================================
 * BACTERIAGROWTHSIMULATOR.JAVA - Growth Simulation
 * ========================================
 * 
 * Enunciado: Simulador avanzado de crecimiento bacteriano.
 * Modela multiplicación exponencial con factores ambientales,
 * recursos limitados y condiciones variables.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class BacteriaGrowthSimulator {
	
	private String simulatorName;
	private BacteriaCounterIterative counter;
	private double environmentalFactor;
	private long resourceLimit;
	
	public BacteriaGrowthSimulator(String simulatorName) {
		this.simulatorName = simulatorName;
		this.counter = new BacteriaCounterIterative();
		this.environmentalFactor = 1.0; // Neutral conditions
		this.resourceLimit = Long.MAX_VALUE; // Unlimited resources
	}
	
	public void setEnvironmentalFactor(double factor) {
		if (factor >= 0.0 && factor <= 2.0) {
			this.environmentalFactor = factor;
		} else {
			System.out.println("Warning: Environmental factor must be between 0.0 and 2.0");
		}
	}
	
	public void setResourceLimit(long limit) {
		this.resourceLimit = limit;
	}
	
	public long simulateGrowth(int days) {
		long bacteria = counter.count(days);
		
		// Apply environmental factor
		bacteria = (long)(bacteria * environmentalFactor);
		
		// Apply resource limit
		if (bacteria > resourceLimit) {
			bacteria = resourceLimit;
		}
		
		return bacteria;
	}
	
	public long simulateWithMortality(int days, double mortalityRate) {
		// Mortality rate: percentage of bacteria that die each day
		if (mortalityRate < 0.0 || mortalityRate > 1.0) {
			System.out.println("Error: Mortality rate must be between 0.0 and 1.0");
			return 0;
		}
		
		long bacteria = 1;
		
		for (int i = 0; i < days; i++) {
			// Growth phase: bacteria double
			bacteria *= 2;
			
			// Death phase: apply mortality rate
			long deaths = (long)(bacteria * mortalityRate);
			bacteria -= deaths;
			
			// Check extinction
			if (bacteria <= 0) {
				System.out.println("Population extinct at day " + (i + 1));
				return 0;
			}
		}
		
		return bacteria;
	}
	
	public void runFullSimulation(int days) {
		System.out.println("========== BACTERIA GROWTH SIMULATION ==========");
		System.out.println("Simulator: " + simulatorName);
		System.out.println("Days: " + days);
		System.out.println("Environmental Factor: " + environmentalFactor);
		System.out.println("Resource Limit: " + 
		                   (resourceLimit == Long.MAX_VALUE ? "Unlimited" : 
		                    String.format("%,d", resourceLimit)));
		System.out.println();
		
		System.out.println("DAY-BY-DAY SIMULATION:");
		System.out.println("┌──────┬────────────────┬────────────────┬──────────┐");
		System.out.println("│ Day  │   Theoretical  │     Actual     │  Status  │");
		System.out.println("├──────┼────────────────┼────────────────┼──────────┤");
		
		long previousActual = 1;
		
		for (int i = 0; i <= days; i++) {
			long theoretical = counter.count(i);
			long actual = simulateGrowth(i);
			
			String status;
			if (actual >= resourceLimit) {
				status = "LIMITED";
			} else if (actual < theoretical) {
				status = "REDUCED";
			} else if (actual > theoretical) {
				status = "BOOSTED";
			} else {
				status = "NORMAL";
			}
			
			System.out.printf("│ %4d │ %,13d  │ %,13d  │ %-8s │\n", 
			                  i, theoretical, actual, status);
			
			previousActual = actual;
		}
		
		System.out.println("└──────┴────────────────┴────────────────┴──────────┘");
		System.out.println("================================================");
	}
	
	public void compareEnvironmentalConditions(int days) {
		System.out.println("===== ENVIRONMENTAL CONDITIONS COMPARISON =====");
		System.out.println("Days: " + days);
		System.out.println();
		
		double[] factors = {0.5, 0.75, 1.0, 1.25, 1.5};
		String[] conditions = {"Harsh", "Poor", "Neutral", "Favorable", "Optimal"};
		
		System.out.println("┌─────────────┬────────────┬─────────────────┐");
		System.out.println("│ Condition   │   Factor   │    Population   │");
		System.out.println("├─────────────┼────────────┼─────────────────┤");
		
		for (int i = 0; i < factors.length; i++) {
			setEnvironmentalFactor(factors[i]);
			long population = simulateGrowth(days);
			
			System.out.printf("│ %-11s │    %.2f    │ %,14d  │\n", 
			                  conditions[i], factors[i], population);
		}
		
		System.out.println("└─────────────┴────────────┴─────────────────┘");
		System.out.println("===============================================");
	}
	
	public void testMortalityImpact(int days) {
		System.out.println("===== MORTALITY IMPACT ANALYSIS =====");
		System.out.println("Days: " + days);
		System.out.println();
		
		double[] mortalityRates = {0.0, 0.1, 0.25, 0.4, 0.5, 0.6, 0.75};
		
		System.out.println("┌───────────────┬─────────────────┬────────────┐");
		System.out.println("│ Mortality (%) │    Population   │  vs Normal │");
		System.out.println("├───────────────┼─────────────────┼────────────┤");
		
		long normalPopulation = counter.count(days);
		
		for (double rate : mortalityRates) {
			long population = simulateWithMortality(days, rate);
			double percentageVsNormal = (double)population / normalPopulation * 100;
			
			System.out.printf("│     %5.1f%%     │ %,14d  │   %6.2f%% │\n", 
			                  rate * 100, population, percentageVsNormal);
		}
		
		System.out.println("└───────────────┴─────────────────┴────────────┘");
		System.out.println("=====================================");
	}
	
	public static void main(String[] args) {
		BacteriaGrowthSimulator simulator = new BacteriaGrowthSimulator("Lab Simulator v2.0");
		
		// Basic simulation
		simulator.runFullSimulation(10);
		System.out.println("\n");
		
		// Test with resource limit
		simulator.setResourceLimit(1000);
		simulator.runFullSimulation(15);
		System.out.println("\n");
		
		// Compare environmental conditions
		BacteriaGrowthSimulator envSimulator = new BacteriaGrowthSimulator("Environment Test");
		envSimulator.compareEnvironmentalConditions(10);
		System.out.println("\n");
		
		// Test mortality impact
		BacteriaGrowthSimulator mortalitySimulator = new BacteriaGrowthSimulator("Mortality Test");
		mortalitySimulator.testMortalityImpact(10);
	}
}
