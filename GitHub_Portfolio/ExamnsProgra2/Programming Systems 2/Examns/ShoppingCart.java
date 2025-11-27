/**
 * ========================================
 * SHOPPINGCART.JAVA - Shopping Cart System
 * ========================================
 * 
 * Enunciado: Carrito de compras con gestión de productos,
 * cálculo de totales, descuentos y generación de facturas.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class ShoppingCart {
	
	private ArrayList<Product> items;
	private String customerName;
	private double discountPercentage;
	
	public ShoppingCart(String customerName) {
		this.customerName = customerName;
		this.items = new ArrayList<Product>();
		this.discountPercentage = 0.0;
	}
	
	public void addItem(Product product) {
		items.add(product);
		System.out.println("Added: " + product.getName() + " ($" + product.getPrice() + ")");
	}
	
	public void removeItem(String productName) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getName().equalsIgnoreCase(productName)) {
				Product removed = items.remove(i);
				System.out.println("Removed: " + removed.getName());
				return;
			}
		}
		System.out.println("Product not found: " + productName);
	}
	
	public void clearCart() {
		items.clear();
		System.out.println("Cart cleared");
	}
	
	public float getSubtotal() {
		float subtotal = 0.0f;
		for (Product product : items) {
			subtotal += product.claculatePrice();
		}
		return subtotal;
	}
	
	public float getDiscount() {
		return (float)(getSubtotal() * discountPercentage / 100);
	}
	
	public float getTotal() {
		return getSubtotal() - getDiscount();
	}
	
	public void applyDiscount(double percentage) {
		if (percentage >= 0 && percentage <= 100) {
			this.discountPercentage = percentage;
			System.out.println("Applied " + percentage + "% discount");
		} else {
			System.out.println("Invalid discount percentage");
		}
	}
	
	public int getItemCount() {
		return items.size();
	}
	
	public String getCustomerName() {
		return customerName;
	}
	
	public void printReceipt() {
		System.out.println("\n╔════════════════════════════════════════════╗");
		System.out.println("║              PURCHASE RECEIPT              ║");
		System.out.println("╚════════════════════════════════════════════╝");
		System.out.println("Customer: " + customerName);
		System.out.println("Items: " + items.size());
		System.out.println("--------------------------------------------");
		
		for (int i = 0; i < items.size(); i++) {
			Product p = items.get(i);
			System.out.printf("%d. %-20s $%.2f%n", (i+1), p.getName(), p.claculatePrice());
		}
		
		System.out.println("--------------------------------------------");
		System.out.printf("Subtotal:                         $%.2f%n", getSubtotal());
		if (discountPercentage > 0) {
			System.out.printf("Discount (%.1f%%):                  -$%.2f%n", 
			                  discountPercentage, getDiscount());
		}
		System.out.printf("TOTAL:                            $%.2f%n", getTotal());
		System.out.println("============================================\n");
	}
	
	public static void main(String[] args) {
		ShoppingCart cart = new ShoppingCart("John Smith");
		
		// Adding products
		cart.addItem(new Dairy("Milk", 3.99f));
		cart.addItem(new Dairy("Butter", 4.50f));
		cart.addItem(new Fresh("Strawberries", 2.50f, 1.5f));
		cart.addItem(new Fresh("Chicken", 5.99f, 2.0f));
		cart.addItem(new Dairy("Yogurt", 2.99f));
		
		// Print receipt
		cart.printReceipt();
		
		// Apply discount
		cart.applyDiscount(10.0);
		cart.printReceipt();
		
		// Remove item
		cart.removeItem("Butter");
		cart.printReceipt();
		
		System.out.println("Final cart has " + cart.getItemCount() + " items");
	}
}
