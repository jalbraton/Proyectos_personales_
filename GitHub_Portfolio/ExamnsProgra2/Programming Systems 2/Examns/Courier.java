/**
 * ========================================
 * COURIER.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public abstract class Courier implements CouirerFunctions {

	 protected String name;
	
	 public Courier(String name) {
		 this.name = name;
	 }
	 
	 public abstract double calculateComisiion (double cost) throws LowCostException;
	 
}

