/**
 * ========================================
 * PRODUCT.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public abstract class Product {
	private String name;
	private float price;
	
	public Product() {
		
	}
	
	public Product(String name, float a) {
		this.name = name;
		this.price = a;
	}
	
	public float getPrice() {
		return this.price;
	}
	
	public String getName() {
		return this.name;
	}
	
	public abstract float claculatePrice();

}

