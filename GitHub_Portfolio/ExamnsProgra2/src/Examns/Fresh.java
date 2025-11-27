/**
 * ========================================
 * FRESH.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class Fresh extends Product {

	private float weight;
	
	public Fresh(String name, float price, float weight) {
		super(name, price);
		this.weight = weight;
	}
	
	@Override
	public float claculatePrice() {
		return getPrice() * weight;
	}
}

