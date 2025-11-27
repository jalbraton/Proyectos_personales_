/**
 * ========================================
 * ADVANCETYRE.JAVA - Advanced Tyre Implementation
 * ========================================
 * 
 * Enunciado: Clase abstracta para neumáticos avanzados.
 * Implementa la interfaz AdvancedTyreInterface.
 * 
 * @author jalbraton
 * @github github.com/jalbraton
 * ========================================
 */

package Examns;

public abstract class AdvanceTyre implements AdvancedTyreInterface {

	protected static final double TEMP = 75.0;
	private double height;

	public AdvanceTyre(double height) throws NegativeNumberException {
		try {
			if (height < 0) {
				throw new NegativeNumberException("Nada");
			} else {
				this.height = height;
			}

		} catch (NegativeNumberException e) {
			Exception a = new Exception("Nada");

		}
	}

	public double getHeight() {
		return this.height;
	}

	public void setHeight(double height) {
		this.height = height;
	}


	public abstract double calculateOptimunTemp();

}
