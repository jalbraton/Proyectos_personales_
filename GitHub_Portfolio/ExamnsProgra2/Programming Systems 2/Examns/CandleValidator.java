/**
 * ========================================
 * CANDLEVALIDATOR.JAVA - Input Validation
 * ========================================
 * 
 * Enunciado: Validador de datos de entrada para el sistema de
 * conteo de velas. Verifica rangos válidos, formatos correctos
 * y previene errores de cálculo.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class CandleValidator {
	
	private long minYear;
	private long maxYear;
	private String validatorName;
	
	public CandleValidator(String validatorName) {
		this.validatorName = validatorName;
		this.minYear = 0;
		this.maxYear = 10000; // Reasonable upper limit
	}
	
	public CandleValidator(String validatorName, long minYear, long maxYear) {
		this.validatorName = validatorName;
		this.minYear = minYear;
		this.maxYear = maxYear;
	}
	
	public boolean validateYear(long year) {
		return year >= minYear && year <= maxYear;
	}
	
	public boolean validateYearRange(long startYear, long endYear) {
		return validateYear(startYear) && 
		       validateYear(endYear) && 
		       startYear <= endYear;
	}
	
	public String getValidationMessage(long year) {
		if (year < minYear) {
			return "Error: Year " + year + " is below minimum allowed (" + minYear + ")";
		}
		if (year > maxYear) {
			return "Error: Year " + year + " exceeds maximum allowed (" + maxYear + ")";
		}
		return "✓ Year " + year + " is valid";
	}
	
	public long sanitizeYear(long year) {
		if (year < minYear) {
			System.out.println("Warning: Year " + year + " adjusted to minimum " + minYear);
			return minYear;
		}
		if (year > maxYear) {
			System.out.println("Warning: Year " + year + " adjusted to maximum " + maxYear);
			return maxYear;
		}
		return year;
	}
	
	public boolean validateAndCount(long year) {
		if (!validateYear(year)) {
			System.out.println(getValidationMessage(year));
			return false;
		}
		
		CandleCounterIterative counter = new CandleCounterIterative();
		long result = counter.count(year);
		
		System.out.println("✓ Validated and counted: " + result + " candles for year " + year);
		return true;
	}
	
	public void validateBatch(long[] years) {
		System.out.println("===== BATCH VALIDATION: " + validatorName + " =====");
		System.out.println("Validating " + years.length + " entries...");
		System.out.println();
		
		int validCount = 0;
		int invalidCount = 0;
		
		for (int i = 0; i < years.length; i++) {
			System.out.printf("[%3d/%3d] Year %d: ", i + 1, years.length, years[i]);
			
			if (validateYear(years[i])) {
				System.out.println("✓ VALID");
				validCount++;
			} else {
				System.out.println("✗ INVALID - " + getValidationMessage(years[i]));
				invalidCount++;
			}
		}
		
		System.out.println();
		System.out.println("VALIDATION SUMMARY:");
		System.out.println("  Valid entries:   " + validCount);
		System.out.println("  Invalid entries: " + invalidCount);
		System.out.println("  Success rate:    " + 
		                   String.format("%.2f%%", (double)validCount / years.length * 100));
		System.out.println("==============================================");
	}
	
	public long[] filterValidYears(long[] years) {
		// Count valid years
		int validCount = 0;
		for (long year : years) {
			if (validateYear(year)) {
				validCount++;
			}
		}
		
		// Create filtered array
		long[] validYears = new long[validCount];
		int index = 0;
		
		for (long year : years) {
			if (validateYear(year)) {
				validYears[index++] = year;
			}
		}
		
		return validYears;
	}
	
	public void printValidationRules() {
		System.out.println("===== VALIDATION RULES: " + validatorName + " =====");
		System.out.println("Minimum Year: " + minYear);
		System.out.println("Maximum Year: " + maxYear);
		System.out.println("Allowed Range: [" + minYear + ", " + maxYear + "]");
		System.out.println();
		System.out.println("RULES:");
		System.out.println("  1. Year must be >= " + minYear);
		System.out.println("  2. Year must be <= " + maxYear);
		System.out.println("  3. Year ranges must have start <= end");
		System.out.println("  4. Year must be a non-negative integer");
		System.out.println("================================================");
	}
	
	public boolean testValidator() {
		System.out.println("===== VALIDATOR SELF-TEST =====");
		System.out.println("Testing validator: " + validatorName);
		System.out.println();
		
		boolean allPassed = true;
		
		// Test 1: Valid year
		System.out.print("Test 1: Valid year (50)... ");
		if (validateYear(50)) {
			System.out.println("✓ PASSED");
		} else {
			System.out.println("✗ FAILED");
			allPassed = false;
		}
		
		// Test 2: Below minimum
		System.out.print("Test 2: Below minimum (-1)... ");
		if (!validateYear(-1)) {
			System.out.println("✓ PASSED");
		} else {
			System.out.println("✗ FAILED");
			allPassed = false;
		}
		
		// Test 3: Above maximum
		System.out.print("Test 3: Above maximum (" + (maxYear + 1) + ")... ");
		if (!validateYear(maxYear + 1)) {
			System.out.println("✓ PASSED");
		} else {
			System.out.println("✗ FAILED");
			allPassed = false;
		}
		
		// Test 4: Edge case minimum
		System.out.print("Test 4: Edge minimum (" + minYear + ")... ");
		if (validateYear(minYear)) {
			System.out.println("✓ PASSED");
		} else {
			System.out.println("✗ FAILED");
			allPassed = false;
		}
		
		// Test 5: Edge case maximum
		System.out.print("Test 5: Edge maximum (" + maxYear + ")... ");
		if (validateYear(maxYear)) {
			System.out.println("✓ PASSED");
		} else {
			System.out.println("✗ FAILED");
			allPassed = false;
		}
		
		// Test 6: Valid range
		System.out.print("Test 6: Valid range (1, 100)... ");
		if (validateYearRange(1, 100)) {
			System.out.println("✓ PASSED");
		} else {
			System.out.println("✗ FAILED");
			allPassed = false;
		}
		
		// Test 7: Invalid range (reversed)
		System.out.print("Test 7: Invalid range (100, 1)... ");
		if (!validateYearRange(100, 1)) {
			System.out.println("✓ PASSED");
		} else {
			System.out.println("✗ FAILED");
			allPassed = false;
		}
		
		System.out.println();
		System.out.println("RESULT: " + (allPassed ? "✓ ALL TESTS PASSED" : "✗ SOME TESTS FAILED"));
		System.out.println("===============================");
		
		return allPassed;
	}
	
	public static void main(String[] args) {
		CandleValidator validator = new CandleValidator("Standard Validator", 0, 1000);
		
		// Print validation rules
		validator.printValidationRules();
		System.out.println("\n");
		
		// Run self-test
		validator.testValidator();
		System.out.println("\n");
		
		// Validate individual years
		System.out.println("INDIVIDUAL VALIDATIONS:");
		validator.validateAndCount(50);
		validator.validateAndCount(-5);
		validator.validateAndCount(2000);
		System.out.println("\n");
		
		// Batch validation
		long[] testYears = {1, 5, 10, 50, 100, 500, 1000, 2000, -10};
		validator.validateBatch(testYears);
		System.out.println("\n");
		
		// Filter valid years
		System.out.println("FILTERING EXAMPLE:");
		long[] filtered = validator.filterValidYears(testYears);
		System.out.println("Original: " + testYears.length + " years");
		System.out.println("Filtered: " + filtered.length + " valid years");
		System.out.print("Valid years: ");
		for (long year : filtered) {
			System.out.print(year + " ");
		}
		System.out.println();
	}
}
