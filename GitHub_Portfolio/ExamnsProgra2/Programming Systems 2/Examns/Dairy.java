/**
 * ========================================
 * DAIRY.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class Dairy extends Product {
	
	public Dairy(String name, float price) {
		super(name, price);
	}
	
	@Override
	public float claculatePrice() {
		return super.getPrice();
	}
}

