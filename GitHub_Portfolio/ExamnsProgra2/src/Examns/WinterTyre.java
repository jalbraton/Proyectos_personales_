/**
 * ========================================
 * WINTERTYRE.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class WinterTyre extends AdvanceTyre {
	
	private double snowGrip;
	
	public WinterTyre(double snowGrip, double height) throws NegativeNumberException {
		super(height);
		this.snowGrip = snowGrip;
	}
	
	@Override
	public double calculateOptimunTemp() {
		return AdvanceTyre.TEMP * snowGrip;
	}
}

