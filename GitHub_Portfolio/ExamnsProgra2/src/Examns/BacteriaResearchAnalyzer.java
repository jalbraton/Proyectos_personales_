/**
 * ========================================
 * BACTERIARESEARCHANALYZER.JAVA - Research Analysis
 * ========================================
 * 
 * Enunciado: Analizador de datos de investigación bacteriana.
 * Procesa resultados experimentales, calcula estadísticas de
 * laboratorio y genera reportes científicos.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class BacteriaResearchAnalyzer {
	
	private String analyzerName;
	private ArrayList<ExperimentalData> experiments;
	
	private class ExperimentalData {
		String experimentName;
		int days;
		long observedPopulation;
		long theoreticalPopulation;
		double accuracy;
		
		ExperimentalData(String name, int days, long observed, long theoretical) {
			this.experimentName = name;
			this.days = days;
			this.observedPopulation = observed;
			this.theoreticalPopulation = theoretical;
			this.accuracy = theoretical > 0 ? 
			                (double)observed / theoretical * 100 : 0;
		}
	}
	
	public BacteriaResearchAnalyzer(String analyzerName) {
		this.analyzerName = analyzerName;
		this.experiments = new ArrayList<ExperimentalData>();
	}
	
	private String repeatString(String str, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public void addExperiment(String name, int days, long observedPopulation) {
		BacteriaCounterIterative counter = new BacteriaCounterIterative();
		long theoretical = counter.count(days);
		
		ExperimentalData data = new ExperimentalData(
			name, days, observedPopulation, theoretical
		);
		
		experiments.add(data);
		System.out.println("Experiment '" + name + "' added: " + 
		                   observedPopulation + " bacteria observed vs " + 
		                   theoretical + " theoretical");
	}
	
	public void generateResearchReport() {
		System.out.println("╔════════════════════════════════════════════════╗");
		System.out.println("║       BACTERIAL RESEARCH ANALYSIS REPORT       ║");
		System.out.println("╚════════════════════════════════════════════════╝");
		System.out.println();
		System.out.println("Analyzer: " + analyzerName);
		System.out.println("Total Experiments: " + experiments.size());
		System.out.println();
		
		System.out.println("EXPERIMENTAL DATA:");
		System.out.println("┌─────┬───────────────────┬──────┬─────────────┬─────────────┬──────────┐");
		System.out.println("│  #  │   Experiment      │ Days │  Observed   │ Theoretical │ Accuracy │");
		System.out.println("├─────┼───────────────────┼──────┼─────────────┼─────────────┼──────────┤");
		
		for (int i = 0; i < experiments.size(); i++) {
			ExperimentalData exp = experiments.get(i);
			
			System.out.printf("│ %3d │ %-17s │  %2d  │ %,10d  │ %,10d  │  %5.1f%%  │\n", 
			                  i + 1, 
			                  exp.experimentName.length() > 17 ? 
			                      exp.experimentName.substring(0, 17) : exp.experimentName,
			                  exp.days,
			                  exp.observedPopulation,
			                  exp.theoreticalPopulation,
			                  exp.accuracy);
		}
		
		System.out.println("└─────┴───────────────────┴──────┴─────────────┴─────────────┴──────────┘");
		System.out.println();
		
		printStatisticalAnalysis();
		printRecommendations();
		
		System.out.println(repeatString("═", 50));
		System.out.println("End of Research Report");
		System.out.println(repeatString("═", 50));
	}
	
	private void printStatisticalAnalysis() {
		if (experiments.isEmpty()) {
			System.out.println("No experiments to analyze.");
			return;
		}
		
		System.out.println("STATISTICAL ANALYSIS:");
		System.out.println(repeatString("─", 50));
		
		// Calculate statistics
		double sumAccuracy = 0.0;
		double minAccuracy = experiments.get(0).accuracy;
		double maxAccuracy = experiments.get(0).accuracy;
		long totalObserved = 0;
		long totalTheoretical = 0;
		
		for (ExperimentalData exp : experiments) {
			sumAccuracy += exp.accuracy;
			totalObserved += exp.observedPopulation;
			totalTheoretical += exp.theoreticalPopulation;
			
			if (exp.accuracy < minAccuracy) minAccuracy = exp.accuracy;
			if (exp.accuracy > maxAccuracy) maxAccuracy = exp.accuracy;
		}
		
		double avgAccuracy = sumAccuracy / experiments.size();
		
		// Standard deviation
		double sumSquaredDiff = 0.0;
		for (ExperimentalData exp : experiments) {
			double diff = exp.accuracy - avgAccuracy;
			sumSquaredDiff += diff * diff;
		}
		double stdDev = Math.sqrt(sumSquaredDiff / experiments.size());
		
		System.out.printf("  Average Accuracy:       %.2f%%\n", avgAccuracy);
		System.out.printf("  Standard Deviation:     %.2f%%\n", stdDev);
		System.out.printf("  Minimum Accuracy:       %.2f%%\n", minAccuracy);
		System.out.printf("  Maximum Accuracy:       %.2f%%\n", maxAccuracy);
		System.out.printf("  Total Observed:         %,d bacteria\n", totalObserved);
		System.out.printf("  Total Theoretical:      %,d bacteria\n", totalTheoretical);
		System.out.printf("  Overall Match:          %.2f%%\n", 
		                  (double)totalObserved / totalTheoretical * 100);
		System.out.println();
	}
	
	private void printRecommendations() {
		if (experiments.isEmpty()) return;
		
		double sumAccuracy = 0.0;
		for (ExperimentalData exp : experiments) {
			sumAccuracy += exp.accuracy;
		}
		double avgAccuracy = sumAccuracy / experiments.size();
		
		System.out.println("RECOMMENDATIONS:");
		System.out.println(repeatString("─", 50));
		
		if (avgAccuracy > 95) {
			System.out.println("  ✓ Excellent experimental accuracy");
			System.out.println("  ✓ Methodology appears robust");
			System.out.println("  → Continue current protocols");
		} else if (avgAccuracy > 85) {
			System.out.println("  ✓ Good experimental accuracy");
			System.out.println("  → Minor refinements recommended");
			System.out.println("  → Review outlier experiments");
		} else if (avgAccuracy > 70) {
			System.out.println("  ⚠ Moderate accuracy - improvement needed");
			System.out.println("  → Review experimental procedures");
			System.out.println("  → Check environmental controls");
			System.out.println("  → Verify counting methodology");
		} else {
			System.out.println("  ✗ Low accuracy - significant issues detected");
			System.out.println("  → Complete protocol review required");
			System.out.println("  → Verify equipment calibration");
			System.out.println("  → Consider alternative methods");
		}
		System.out.println();
	}
	
	public void identifyOutliers() {
		if (experiments.size() < 3) {
			System.out.println("Insufficient data for outlier analysis (need ≥3 experiments)");
			return;
		}
		
		System.out.println("===== OUTLIER ANALYSIS =====");
		
		// Calculate mean and std dev
		double sumAccuracy = 0.0;
		for (ExperimentalData exp : experiments) {
			sumAccuracy += exp.accuracy;
		}
		double mean = sumAccuracy / experiments.size();
		
		double sumSquaredDiff = 0.0;
		for (ExperimentalData exp : experiments) {
			double diff = exp.accuracy - mean;
			sumSquaredDiff += diff * diff;
		}
		double stdDev = Math.sqrt(sumSquaredDiff / experiments.size());
		
		System.out.printf("Mean accuracy: %.2f%% ± %.2f%%\n", mean, stdDev);
		System.out.println();
		
		// Identify outliers (>2 standard deviations from mean)
		ArrayList<ExperimentalData> outliers = new ArrayList<ExperimentalData>();
		
		for (ExperimentalData exp : experiments) {
			double zScore = Math.abs(exp.accuracy - mean) / stdDev;
			if (zScore > 2.0) {
				outliers.add(exp);
			}
		}
		
		if (outliers.isEmpty()) {
			System.out.println("✓ No significant outliers detected");
		} else {
			System.out.println("⚠ Outliers detected:");
			for (ExperimentalData exp : outliers) {
				double deviation = exp.accuracy - mean;
				System.out.printf("  • %s: %.2f%% (%.2f%% from mean)\n", 
				                  exp.experimentName, exp.accuracy, deviation);
			}
		}
		
		System.out.println("============================");
	}
	
	public void exportToCSV() {
		System.out.println("===== CSV EXPORT =====");
		System.out.println("Experiment,Days,Observed,Theoretical,Accuracy");
		
		for (ExperimentalData exp : experiments) {
			System.out.printf("%s,%d,%d,%d,%.2f%%\n", 
			                  exp.experimentName,
			                  exp.days,
			                  exp.observedPopulation,
			                  exp.theoreticalPopulation,
			                  exp.accuracy);
		}
		
		System.out.println("======================");
	}
	
	public static void main(String[] args) {
		BacteriaResearchAnalyzer analyzer = new BacteriaResearchAnalyzer(
			"Microbiology Lab Analysis System"
		);
		
		// Add experimental data
		analyzer.addExperiment("Control-A", 5, 31);
		analyzer.addExperiment("Control-B", 5, 33);
		analyzer.addExperiment("Treatment-1", 6, 60);
		analyzer.addExperiment("Treatment-2", 6, 65);
		analyzer.addExperiment("Extended-1", 10, 1020);
		analyzer.addExperiment("Extended-2", 10, 1050);
		analyzer.addExperiment("Stress-Test", 8, 240);
		
		System.out.println("\n");
		
		// Generate full report
		analyzer.generateResearchReport();
		
		System.out.println("\n");
		
		// Outlier analysis
		analyzer.identifyOutliers();
		
		System.out.println("\n");
		
		// Export to CSV
		analyzer.exportToCSV();
	}
}
