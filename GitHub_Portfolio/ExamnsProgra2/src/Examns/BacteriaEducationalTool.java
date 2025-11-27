/**
 * ========================================
 * BACTERIAEDUCATIONALTOOL.JAVA - Educational Tool
 * ========================================
 * 
 * Enunciado: Herramienta educativa interactiva sobre bacterias.
 * Proporciona lecciones, ejercicios, quizzes y demostraciones
 * visuales para estudiantes de microbiología.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.Scanner;

public class BacteriaEducationalTool {
	
	private String toolName;
	private BacteriaCounterIterative counter;
	private int lessonsCompleted;
	private int quizScore;
	
	public BacteriaEducationalTool(String toolName) {
		this.toolName = toolName;
		this.counter = new BacteriaCounterIterative();
		this.lessonsCompleted = 0;
		this.quizScore = 0;
	}
	
	public void printWelcome() {
		System.out.println("╔════════════════════════════════════════════════╗");
		System.out.println("║     BACTERIA GROWTH EDUCATIONAL TOOL           ║");
		System.out.println("╚════════════════════════════════════════════════╝");
		System.out.println();
		System.out.println("Welcome to " + toolName + "!");
		System.out.println("Learn about exponential bacterial growth");
		System.out.println();
	}
	
	public void lesson1_Introduction() {
		System.out.println("========== LESSON 1: INTRODUCTION ==========");
		System.out.println();
		System.out.println("BINARY FISSION:");
		System.out.println("Bacteria reproduce through a process called binary fission.");
		System.out.println("One bacterium divides into two identical daughter cells.");
		System.out.println();
		System.out.println("EXPONENTIAL GROWTH:");
		System.out.println("Because each bacterium doubles, growth is exponential:");
		System.out.println("  Day 0: 1 bacterium");
		System.out.println("  Day 1: 2 bacteria (2^1)");
		System.out.println("  Day 2: 4 bacteria (2^2)");
		System.out.println("  Day 3: 8 bacteria (2^3)");
		System.out.println("  Day n: 2^n bacteria");
		System.out.println();
		System.out.println("KEY INSIGHT:");
		System.out.println("The population doubles with each generation!");
		System.out.println("This leads to VERY rapid growth.");
		System.out.println("============================================");
		System.out.println();
		
		lessonsCompleted++;
	}
	
	public void lesson2_MathematicalFormula() {
		System.out.println("========== LESSON 2: THE FORMULA ==========");
		System.out.println();
		System.out.println("EXPONENTIAL GROWTH FORMULA:");
		System.out.println("  N(t) = N₀ × 2^t");
		System.out.println();
		System.out.println("WHERE:");
		System.out.println("  N(t) = Population at time t");
		System.out.println("  N₀   = Initial population (usually 1)");
		System.out.println("  t    = Number of generations (days)");
		System.out.println("  2    = Growth factor (doubling)");
		System.out.println();
		System.out.println("EXAMPLES:");
		for (int i = 0; i <= 5; i++) {
			long bacteria = counter.count(i);
			System.out.printf("  N(%d) = 1 × 2^%d = %d bacteria\n", i, i, bacteria);
		}
		System.out.println();
		System.out.println("PRACTICE:");
		System.out.println("Try calculating: What is N(10)?");
		long answer = counter.count(10);
		System.out.println("Answer: " + answer + " bacteria");
		System.out.println("===========================================");
		System.out.println();
		
		lessonsCompleted++;
	}
	
	public void lesson3_RealWorldApplications() {
		System.out.println("========== LESSON 3: REAL-WORLD APPLICATIONS ==========");
		System.out.println();
		System.out.println("1. FOOD SAFETY:");
		System.out.println("   Understanding bacterial growth helps prevent food poisoning.");
		System.out.println("   Example: E. coli can double every 20 minutes at 37°C");
		System.out.println();
		
		System.out.println("2. MEDICAL MICROBIOLOGY:");
		System.out.println("   Predicting infection progression");
		System.out.println("   Determining antibiotic dosing schedules");
		System.out.println();
		
		System.out.println("3. BIOTECHNOLOGY:");
		System.out.println("   Industrial fermentation processes");
		System.out.println("   Production of insulin, vaccines, enzymes");
		System.out.println();
		
		System.out.println("4. ENVIRONMENTAL SCIENCE:");
		System.out.println("   Bioremediation (cleaning up pollution)");
		System.out.println("   Wastewater treatment");
		System.out.println();
		
		System.out.println("DEMONSTRATION:");
		System.out.println("Starting with 1 bacterium, after 20 generations:");
		long result = counter.count(20);
		System.out.printf("  Population = %,d bacteria\n", result);
		System.out.println("  That's over 1 MILLION bacteria!");
		System.out.println("========================================================");
		System.out.println();
		
		lessonsCompleted++;
	}
	
	public void interactiveDemo() {
		Scanner scanner = new Scanner(System.in);
		
		System.out.println("========== INTERACTIVE DEMO ==========");
		System.out.println("Calculate bacterial growth for any number of days!");
		System.out.println();
		
		boolean continueDemo = true;
		
		while (continueDemo) {
			System.out.print("Enter number of days (or 0 to exit): ");
			
			try {
				int days = scanner.nextInt();
				
				if (days == 0) {
					continueDemo = false;
					continue;
				}
				
				if (days < 0) {
					System.out.println("Please enter a positive number.");
					continue;
				}
				
				long bacteria = counter.count(days);
				
				System.out.println();
				System.out.println("RESULTS:");
				System.out.printf("  After %d days: %,d bacteria\n", days, bacteria);
				System.out.printf("  Growth factor: %dx\n", bacteria);
				System.out.printf("  Formula: 2^%d = %,d\n", days, bacteria);
				System.out.println();
				
				// Visual representation for small numbers
				if (days <= 10) {
					System.out.println("Visual:");
					for (int i = 0; i <= days; i++) {
						long pop = counter.count(i);
						System.out.printf("  Day %2d: ", i);
						
						// Print bacteria symbols (limit to avoid overflow)
						int symbolsToPrint = (int)Math.min(pop, 50);
						for (int j = 0; j < symbolsToPrint; j++) {
							System.out.print("●");
						}
						if (pop > 50) {
							System.out.print(" ... (" + pop + " total)");
						}
						System.out.println();
					}
					System.out.println();
				}
				
			} catch (Exception e) {
				System.out.println("Invalid input. Please enter a whole number.");
				scanner.nextLine(); // Clear buffer
			}
		}
		
		System.out.println("Demo completed!");
		System.out.println("======================================");
		System.out.println();
	}
	
	public void runQuiz() {
		Scanner scanner = new Scanner(System.in);
		quizScore = 0;
		
		System.out.println("========== KNOWLEDGE QUIZ ==========");
		System.out.println("Test your understanding of bacterial growth!");
		System.out.println();
		
		// Question 1
		System.out.println("Question 1: If you start with 1 bacterium, how many");
		System.out.println("            bacteria will you have after 5 days?");
		System.out.print("Your answer: ");
		try {
			long answer1 = scanner.nextLong();
			if (answer1 == counter.count(5)) {
				System.out.println("✓ Correct! The answer is " + counter.count(5));
				quizScore++;
			} else {
				System.out.println("✗ Incorrect. The correct answer is " + counter.count(5));
			}
		} catch (Exception e) {
			System.out.println("✗ Invalid input");
			scanner.nextLine();
		}
		System.out.println();
		
		// Question 2
		System.out.println("Question 2: What is the growth factor in bacterial");
		System.out.println("            reproduction (how many times does it multiply)?");
		System.out.print("Your answer: ");
		try {
			int answer2 = scanner.nextInt();
			if (answer2 == 2) {
				System.out.println("✓ Correct! Bacteria double (×2) each generation");
				quizScore++;
			} else {
				System.out.println("✗ Incorrect. Bacteria double, so the factor is 2");
			}
		} catch (Exception e) {
			System.out.println("✗ Invalid input");
			scanner.nextLine();
		}
		System.out.println();
		
		// Question 3
		System.out.println("Question 3: After 10 days, approximately how many");
		System.out.println("            bacteria are there? (nearest thousand)");
		System.out.println("            a) 100    b) 1,000    c) 10,000");
		System.out.print("Your answer (a/b/c): ");
		try {
			String answer3 = scanner.next().toLowerCase();
			if (answer3.equals("b")) {
				System.out.println("✓ Correct! 2^10 = 1,024 bacteria");
				quizScore++;
			} else {
				System.out.println("✗ Incorrect. 2^10 = 1,024 ≈ 1,000");
			}
		} catch (Exception e) {
			System.out.println("✗ Invalid input");
		}
		System.out.println();
		
		// Results
		System.out.println("QUIZ RESULTS:");
		System.out.printf("  Score: %d/3 (%.0f%%)\n", quizScore, (quizScore / 3.0 * 100));
		
		if (quizScore == 3) {
			System.out.println("  Grade: A - Excellent!");
		} else if (quizScore == 2) {
			System.out.println("  Grade: B - Good job!");
		} else if (quizScore == 1) {
			System.out.println("  Grade: C - Review the lessons");
		} else {
			System.out.println("  Grade: F - Please study more");
		}
		
		System.out.println("====================================");
		System.out.println();
	}
	
	public void printProgress() {
		System.out.println("===== YOUR PROGRESS =====");
		System.out.println("Lessons completed: " + lessonsCompleted);
		System.out.println("Quiz score: " + quizScore + "/3");
		System.out.println("=========================");
	}
	
	public static void main(String[] args) {
		BacteriaEducationalTool tool = new BacteriaEducationalTool(
			"Microbiology Education Platform"
		);
		
		tool.printWelcome();
		
		// Lessons
		tool.lesson1_Introduction();
		tool.lesson2_MathematicalFormula();
		tool.lesson3_RealWorldApplications();
		
		// Interactive demo
		tool.interactiveDemo();
		
		// Quiz
		tool.runQuiz();
		
		// Show progress
		tool.printProgress();
		
		System.out.println("\nThank you for learning with " + tool.toolName + "!");
	}
}
