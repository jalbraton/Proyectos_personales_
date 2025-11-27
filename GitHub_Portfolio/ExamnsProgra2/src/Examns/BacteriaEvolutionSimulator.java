/**
 * ========================================
 * BACTERIAEVOLUTIONSIMULATOR.JAVA - Evolution
 * ========================================
 * 
 * Enunciado: Simulador de evolución bacteriana. Modela mutaciones,
 * selección natural, adaptación al entorno y desarrollo de
 * resistencias a lo largo de generaciones.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.Random;

public class BacteriaEvolutionSimulator {
	
	private String simulatorName;
	private BacteriaCounterIterative counter;
	private double mutationRate;
	private double survivalRate;
	private Random random;
	
	public BacteriaEvolutionSimulator(String simulatorName) {
		this.simulatorName = simulatorName;
		this.counter = new BacteriaCounterIterative();
		this.mutationRate = 0.01; // 1% mutation rate
		this.survivalRate = 0.95; // 95% survival rate
		this.random = new Random();
	}
	
	public void setMutationRate(double rate) {
		if (rate >= 0.0 && rate <= 1.0) {
			this.mutationRate = rate;
		}
	}
	
	public void setSurvivalRate(double rate) {
		if (rate >= 0.0 && rate <= 1.0) {
			this.survivalRate = rate;
		}
	}
	
	public long simulateEvolution(int generations) {
		long population = 1;
		int mutantCount = 0;
		
		for (int gen = 0; gen < generations; gen++) {
			// Growth phase
			population *= 2;
			
			// Mutation phase
			if (random.nextDouble() < mutationRate) {
				mutantCount++;
			}
			
			// Selection phase
			long survivors = (long)(population * survivalRate);
			population = survivors;
			
			if (population <= 0) {
				System.out.println("Population extinct at generation " + gen);
				return 0;
			}
		}
		
		return population;
	}
	
	public void runEvolutionarySimulation(int generations) {
		System.out.println("========== EVOLUTIONARY SIMULATION ==========");
		System.out.println("Simulator: " + simulatorName);
		System.out.println("Generations: " + generations);
		System.out.println("Mutation Rate: " + (mutationRate * 100) + "%");
		System.out.println("Survival Rate: " + (survivalRate * 100) + "%");
		System.out.println();
		
		System.out.println("GENERATION TRACKING:");
		System.out.println("┌────────┬─────────────────┬──────────┬──────────────┐");
		System.out.println("│  Gen   │   Population    │ Mutants  │  Adaptation  │");
		System.out.println("├────────┼─────────────────┼──────────┼──────────────┤");
		
		long population = 1;
		int totalMutants = 0;
		
		for (int gen = 0; gen <= generations; gen++) {
			if (gen > 0) {
				// Growth
				population *= 2;
				
				// Mutation
				if (random.nextDouble() < mutationRate) {
					totalMutants++;
				}
				
				// Selection
				population = (long)(population * survivalRate);
			}
			
			double adaptationLevel = survivalRate + (totalMutants * 0.01);
			adaptationLevel = Math.min(adaptationLevel, 1.0);
			
			System.out.printf("│  %4d  │ %,14d  │   %4d   │    %.2f%%    │\n", 
			                  gen, population, totalMutants, adaptationLevel * 100);
			
			if (population <= 0) {
				System.out.println("└────────┴─────────────────┴──────────┴──────────────┘");
				System.out.println("\n!!! POPULATION EXTINCT !!!");
				return;
			}
		}
		
		System.out.println("└────────┴─────────────────┴──────────┴──────────────┘");
		System.out.println();
		
		System.out.println("FINAL STATISTICS:");
		System.out.printf("  Final population: %,d\n", population);
		System.out.printf("  Total mutations: %d\n", totalMutants);
		System.out.printf("  Mutation frequency: %.2f%%\n", 
		                  (double)totalMutants / generations * 100);
		System.out.println("=============================================");
	}
	
	public void compareEvolutionaryStrategies(int generations) {
		System.out.println("===== EVOLUTIONARY STRATEGIES COMPARISON =====");
		System.out.println("Generations: " + generations);
		System.out.println();
		
		double[][] strategies = {
			{0.001, 0.99},  // Low mutation, high survival (conservative)
			{0.01, 0.95},   // Medium mutation, medium survival (balanced)
			{0.05, 0.90},   // High mutation, lower survival (aggressive)
			{0.10, 0.85},   // Very high mutation, low survival (risky)
		};
		
		String[] strategyNames = {"Conservative", "Balanced", "Aggressive", "Risky"};
		
		System.out.println("┌───────────────┬──────────┬──────────┬────────────────┐");
		System.out.println("│   Strategy    │ Mutation │ Survival │     Result     │");
		System.out.println("├───────────────┼──────────┼──────────┼────────────────┤");
		
		for (int i = 0; i < strategies.length; i++) {
			setMutationRate(strategies[i][0]);
			setSurvivalRate(strategies[i][1]);
			
			long population = simulateEvolution(generations);
			
			System.out.printf("│ %-13s │  %.1f%%   │  %.1f%%   │ %,13d  │\n", 
			                  strategyNames[i], 
			                  strategies[i][0] * 100, 
			                  strategies[i][1] * 100, 
			                  population);
		}
		
		System.out.println("└───────────────┴──────────┴──────────┴────────────────┘");
		System.out.println("==============================================");
	}
	
	public void simulateAntibioticResistance(int generations) {
		System.out.println("===== ANTIBIOTIC RESISTANCE SIMULATION =====");
		System.out.println("Simulating development of resistance over time");
		System.out.println();
		
		long population = 1000;
		int resistantBacteria = 0;
		double resistanceRate = 0.0;
		
		System.out.println("┌────────┬─────────────┬──────────────┬────────────┐");
		System.out.println("│  Gen   │  Total Pop  │  Resistant   │  % Resist  │");
		System.out.println("├────────┼─────────────┼──────────────┼────────────┤");
		
		for (int gen = 0; gen <= generations; gen++) {
			// Some bacteria develop resistance through mutation
			if (random.nextDouble() < mutationRate) {
				int newResistant = (int)(population * 0.1); // 10% become resistant
				resistantBacteria += newResistant;
			}
			
			// Apply antibiotic pressure (kills non-resistant)
			if (gen > 5) { // Antibiotic applied after generation 5
				long nonResistant = population - resistantBacteria;
				nonResistant = (long)(nonResistant * 0.3); // 70% of non-resistant die
				population = nonResistant + resistantBacteria;
			}
			
			resistanceRate = population > 0 ? 
			                 (double)resistantBacteria / population * 100 : 0;
			
			System.out.printf("│  %4d  │ %,10d  │  %,10d  │   %5.1f%%  │\n", 
			                  gen, population, resistantBacteria, resistanceRate);
			
			// Growth for next generation
			if (gen < generations) {
				population *= 2;
				resistantBacteria *= 2;
			}
		}
		
		System.out.println("└────────┴─────────────┴──────────────┴────────────┘");
		System.out.println();
		System.out.println("RESISTANCE ANALYSIS:");
		System.out.printf("  Final resistance rate: %.2f%%\n", resistanceRate);
		System.out.println("  Outcome: " + 
		                   (resistanceRate > 80 ? "HIGH RESISTANCE DEVELOPED" : 
		                    resistanceRate > 50 ? "MODERATE RESISTANCE" : 
		                    "LOW RESISTANCE"));
		System.out.println("============================================");
	}
	
	public static void main(String[] args) {
		BacteriaEvolutionSimulator simulator = 
			new BacteriaEvolutionSimulator("Evolution Lab v1.0");
		
		// Basic evolutionary simulation
		simulator.runEvolutionarySimulation(15);
		System.out.println("\n");
		
		// Compare strategies
		BacteriaEvolutionSimulator comparator = 
			new BacteriaEvolutionSimulator("Strategy Analyzer");
		comparator.compareEvolutionaryStrategies(20);
		System.out.println("\n");
		
		// Antibiotic resistance
		BacteriaEvolutionSimulator resistanceSimulator = 
			new BacteriaEvolutionSimulator("Resistance Tracker");
		resistanceSimulator.setMutationRate(0.05);
		resistanceSimulator.simulateAntibioticResistance(15);
	}
}
