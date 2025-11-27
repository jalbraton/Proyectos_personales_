/**
 * ========================================
 * B.JAVA - Binary Operations & Bit Manipulation
 * ========================================
 * 
 * Enunciado: Clase de utilidades para operaciones binarias,
 * manipulación de bits y conversiones numéricas.
 * Proporciona herramientas para análisis de datos a nivel de bits.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class B {
	
	public String toBinary(int number) {
		if (number == 0) return "0";
		
		StringBuilder binary = new StringBuilder();
		int n = Math.abs(number);
		
		while (n > 0) {
			binary.insert(0, n % 2);
			n /= 2;
		}
		
		return (number < 0 ? "-" : "") + binary.toString();
	}
	
	public int fromBinary(String binary) {
		if (binary == null || binary.isEmpty()) return 0;
		
		boolean negative = binary.startsWith("-");
		String digits = negative ? binary.substring(1) : binary;
		
		int result = 0;
		for (int i = 0; i < digits.length(); i++) {
			if (digits.charAt(i) == '1') {
				result += Math.pow(2, digits.length() - 1 - i);
			}
		}
		
		return negative ? -result : result;
	}
	
	public int countSetBits(int number) {
		int count = 0;
		while (number > 0) {
			count += number & 1;
			number >>= 1;
		}
		return count;
	}
	
	public boolean isPowerOfTwo(int number) {
		return number > 0 && (number & (number - 1)) == 0;
	}
	
	public int setBit(int number, int position) {
		return number | (1 << position);
	}
	
	public int clearBit(int number, int position) {
		return number & ~(1 << position);
	}
	
	public int toggleBit(int number, int position) {
		return number ^ (1 << position);
	}
	
	public boolean checkBit(int number, int position) {
		return (number & (1 << position)) != 0;
	}
	
	public int swapBits(int number, int pos1, int pos2) {
		boolean bit1 = checkBit(number, pos1);
		boolean bit2 = checkBit(number, pos2);
		
		if (bit1 != bit2) {
			number = toggleBit(number, pos1);
			number = toggleBit(number, pos2);
		}
		
		return number;
	}
	
	public String toHexadecimal(int number) {
		if (number == 0) return "0";
		
		char[] hexChars = "0123456789ABCDEF".toCharArray();
		StringBuilder hex = new StringBuilder();
		int n = Math.abs(number);
		
		while (n > 0) {
			hex.insert(0, hexChars[n % 16]);
			n /= 16;
		}
		
		return (number < 0 ? "-" : "") + hex.toString();
	}
	
	public void printBitRepresentation(int number) {
		System.out.println("\n=== Bit Representation of " + number + " ===");
		System.out.println("Decimal:      " + number);
		System.out.println("Binary:       " + toBinary(number));
		System.out.println("Hexadecimal:  " + toHexadecimal(number));
		System.out.println("Set bits:     " + countSetBits(Math.abs(number)));
		System.out.println("Power of 2:   " + isPowerOfTwo(number));
	}
	
	public static void main(String[] args) {
		B bitUtils = new B();
		
		System.out.println("╔════════════════════════════════════════════════╗");
		System.out.println("║     BINARY OPERATIONS & BIT MANIPULATION       ║");
		System.out.println("╚════════════════════════════════════════════════╝");
		
		// Test conversions
		int[] testNumbers = {42, 255, 1024, 7, 63};
		
		for (int num : testNumbers) {
			bitUtils.printBitRepresentation(num);
		}
		
		System.out.println("\n=== BIT OPERATIONS DEMO ===");
		int value = 42;
		System.out.println("Original value: " + value + " = " + bitUtils.toBinary(value));
		
		int setBit = bitUtils.setBit(value, 0);
		System.out.println("Set bit 0: " + setBit + " = " + bitUtils.toBinary(setBit));
		
		int clearBit = bitUtils.clearBit(value, 1);
		System.out.println("Clear bit 1: " + clearBit + " = " + bitUtils.toBinary(clearBit));
		
		int toggleBit = bitUtils.toggleBit(value, 2);
		System.out.println("Toggle bit 2: " + toggleBit + " = " + bitUtils.toBinary(toggleBit));
		
		System.out.println("\n=== POWERS OF 2 CHECK ===");
		for (int i = 0; i <= 10; i++) {
			int pow = (int) Math.pow(2, i);
			System.out.println(pow + " is power of 2: " + bitUtils.isPowerOfTwo(pow));
		}
		
		System.out.println("\n65 is power of 2: " + bitUtils.isPowerOfTwo(65));
	}
}
