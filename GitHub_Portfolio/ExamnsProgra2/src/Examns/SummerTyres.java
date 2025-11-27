/**
 * ========================================
 * SUMMERTYRES.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class SummerTyres extends AdvanceTyre {
	private double temp;
	private double wear;
	private boolean runFlat;

	public SummerTyres(double height, double wear, boolean runFlat) throws NegativeNumberException {
		super(height);
		this.wear = wear;
		this.runFlat = runFlat;
	}

	@Override
	public double calculateOptimunTemp() {
		if (runFlat) {
			this.temp = AdvanceTyre.TEMP * 2.5;
		} else {
			this.temp = AdvanceTyre.TEMP * wear;
		}
		return this.temp;
	}
}

