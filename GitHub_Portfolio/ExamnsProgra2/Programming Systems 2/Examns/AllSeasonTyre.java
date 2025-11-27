/**
 * ========================================
 * ALLSEASONTYRE.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class AllSeasonTyre extends AdvanceTyre {

	public AllSeasonTyre(double height) throws NegativeNumberException {
		super(height);
	}

	private double temp; 
	
	@Override
	public double calculateOptimunTemp() {
		if (super.getHeight() > 50) {
			this.temp = 80.3;			
		} else {
			this.temp = 75.7;
		}
		return temp;
	}
}

