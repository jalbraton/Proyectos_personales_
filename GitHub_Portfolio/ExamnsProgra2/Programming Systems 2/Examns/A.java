/**
 * ========================================
 * A.JAVA - Recursive Algorithm Analyzer
 * ========================================
 * 
 * Enunciado: Sistema de análisis de algoritmos recursivos.
 * Implementa funciones matemáticas recursivas y realiza
 * análisis de complejidad, traza de ejecución y optimización.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class A {
	
	private static int recursionDepth = 0;
	private static int maxDepth = 0;
	private static int callCount = 0;
	
	public static void main(String[] args) {
		System.out.println("╔════════════════════════════════════════════════╗");
		System.out.println("║     RECURSIVE ALGORITHM ANALYZER              ║");
		System.out.println("╚════════════════════════════════════════════════╝\n");
		
		// Test basic recursive method
		B b = new B();
		int n = 5;
		int m = 2;
		
		System.out.println("=== BASIC RECURSIVE METHOD ===");
		System.out.println("Input: n=" + n + ", m=" + m);
		resetCounters();
		int result = method(n, m);
		System.out.println("Result: " + result);
		printStatistics();
		
		// Advanced recursive tests
		System.out.println("\n=== FACTORIAL CALCULATION ===");
		resetCounters();
		int factResult = factorial(5);
		System.out.println("5! = " + factResult);
		printStatistics();
		
		System.out.println("\n=== FIBONACCI SEQUENCE ===");
		resetCounters();
		int fibResult = fibonacci(10);
		System.out.println("fib(10) = " + fibResult);
		printStatistics();
		
		System.out.println("\n=== OPTIMIZED FIBONACCI (Memoization) ===");
		resetCounters();
		int[] memo = new int[11];
		int fibOptResult = fibonacciMemo(10, memo);
		System.out.println("fib(10) optimized = " + fibOptResult);
		printStatistics();
		
		System.out.println("\n=== POWER CALCULATION ===");
		resetCounters();
		long powerResult = power(2, 10);
		System.out.println("2^10 = " + powerResult);
		printStatistics();
		
		System.out.println("\n=== SUM OF DIGITS ===");
		resetCounters();
		int sumResult = sumDigits(12345);
		System.out.println("Sum of digits in 12345 = " + sumResult);
		printStatistics();
		
		System.out.println("\n=== COMPARISON TABLE ===");
		compareAlgorithms();
	}
	
	public static int method(int n, int m) {
		trackRecursion();
		if (n < m) {
			returnFromRecursion();
			return 3;
		} else {
			int result = 3 * method(n - m, n + m);
			returnFromRecursion();
			return result;
		}
	}
	
	public static int factorial(int n) {
		trackRecursion();
		if (n <= 1) {
			returnFromRecursion();
			return 1;
		}
		int result = n * factorial(n - 1);
		returnFromRecursion();
		return result;
	}
	
	public static int fibonacci(int n) {
		trackRecursion();
		if (n <= 1) {
			returnFromRecursion();
			return n;
		}
		int result = fibonacci(n - 1) + fibonacci(n - 2);
		returnFromRecursion();
		return result;
	}
	
	public static int fibonacciMemo(int n, int[] memo) {
		trackRecursion();
		if (n <= 1) {
			returnFromRecursion();
			return n;
		}
		if (memo[n] != 0) {
			returnFromRecursion();
			return memo[n];
		}
		memo[n] = fibonacciMemo(n - 1, memo) + fibonacciMemo(n - 2, memo);
		returnFromRecursion();
		return memo[n];
	}
	
	public static long power(int base, int exp) {
		trackRecursion();
		if (exp == 0) {
			returnFromRecursion();
			return 1;
		}
		if (exp == 1) {
			returnFromRecursion();
			return base;
		}
		if (exp % 2 == 0) {
			long half = power(base, exp / 2);
			returnFromRecursion();
			return half * half;
		} else {
			long result = base * power(base, exp - 1);
			returnFromRecursion();
			return result;
		}
	}
	
	public static int sumDigits(int n) {
		trackRecursion();
		if (n == 0) {
			returnFromRecursion();
			return 0;
		}
		int result = (n % 10) + sumDigits(n / 10);
		returnFromRecursion();
		return result;
	}
	
	private static void trackRecursion() {
		callCount++;
		recursionDepth++;
		if (recursionDepth > maxDepth) {
			maxDepth = recursionDepth;
		}
	}
	
	private static void returnFromRecursion() {
		recursionDepth--;
	}
	
	private static void resetCounters() {
		recursionDepth = 0;
		maxDepth = 0;
		callCount = 0;
	}
	
	private static void printStatistics() {
		System.out.println("\n--- Execution Statistics ---");
		System.out.println("Total recursive calls: " + callCount);
		System.out.println("Maximum recursion depth: " + maxDepth);
		System.out.println("Average depth: " + (callCount > 0 ? callCount / (maxDepth > 0 ? maxDepth : 1) : 0));
	}
	
	private static void compareAlgorithms() {
		System.out.println("┌─────────────────┬────────────┬───────────┐");
		System.out.println("│   Algorithm     │   Calls    │ Max Depth │");
		System.out.println("├─────────────────┼────────────┼───────────┤");
		
		resetCounters();
		factorial(5);
		System.out.printf("│ Factorial(5)    │ %,9d  │   %,6d  │%n", callCount, maxDepth);
		
		resetCounters();
		fibonacci(10);
		System.out.printf("│ Fibonacci(10)   │ %,9d  │   %,6d  │%n", callCount, maxDepth);
		
		resetCounters();
		int[] memo = new int[11];
		fibonacciMemo(10, memo);
		System.out.printf("│ Fib-Memo(10)    │ %,9d  │   %,6d  │%n", callCount, maxDepth);
		
		resetCounters();
		power(2, 10);
		System.out.printf("│ Power(2,10)     │ %,9d  │   %,6d  │%n", callCount, maxDepth);
		
		System.out.println("└─────────────────┴────────────┴───────────┘");
	}
}

