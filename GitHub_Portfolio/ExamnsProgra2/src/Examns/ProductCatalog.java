/**
 * ========================================
 * PRODUCTCATALOG.JAVA - Product Management
 * ========================================
 * 
 * Enunciado: Catálogo de productos que gestiona inventario,
 * calcula precios totales y genera reportes de productos
 * frescos y lácteos.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class ProductCatalog {
	
	private ArrayList<Product> products;
	private String catalogName;
	
	public ProductCatalog(String catalogName) {
		this.catalogName = catalogName;
		this.products = new ArrayList<Product>();
	}
	
	public void addProduct(Product product) {
		products.add(product);
		System.out.println("Product added: " + product.getName());
	}
	
	public void removeProduct(String name) {
		for (int i = 0; i < products.size(); i++) {
			if (products.get(i).getName().equalsIgnoreCase(name)) {
				products.remove(i);
				System.out.println("Product removed: " + name);
				return;
			}
		}
		System.out.println("Product not found: " + name);
	}
	
	public Product findProduct(String name) {
		for (Product product : products) {
			if (product.getName().equalsIgnoreCase(name)) {
				return product;
			}
		}
		return null;
	}
	
	public float calculateTotalValue() {
		float total = 0.0f;
		for (Product product : products) {
			total += product.claculatePrice();
		}
		return total;
	}
	
	public ArrayList<Product> getFreshProducts() {
		ArrayList<Product> freshProducts = new ArrayList<Product>();
		for (Product product : products) {
			if (product instanceof Fresh) {
				freshProducts.add(product);
			}
		}
		return freshProducts;
	}
	
	public ArrayList<Product> getDairyProducts() {
		ArrayList<Product> dairyProducts = new ArrayList<Product>();
		for (Product product : products) {
			if (product instanceof Dairy) {
				dairyProducts.add(product);
			}
		}
		return dairyProducts;
	}
	
	public void printCatalog() {
		System.out.println("===== " + catalogName + " =====");
		System.out.println("Total products: " + products.size());
		System.out.println("\nProduct List:");
		for (int i = 0; i < products.size(); i++) {
			Product p = products.get(i);
			System.out.println((i + 1) + ". " + p.getName() + 
			                   " | Type: " + p.getClass().getSimpleName() +
			                   " | Base Price: $" + p.getPrice() +
			                   " | Total Price: $" + p.claculatePrice());
		}
		System.out.println("\nTotal Catalog Value: $" + calculateTotalValue());
		System.out.println("Fresh Products: " + getFreshProducts().size());
		System.out.println("Dairy Products: " + getDairyProducts().size());
		System.out.println("================================");
	}
	
	public int getProductCount() {
		return products.size();
	}
	
	public String getCatalogName() {
		return catalogName;
	}
	
	public static void main(String[] args) {
		ProductCatalog catalog = new ProductCatalog("Supermarket Inventory");
		
		// Add various products
		catalog.addProduct(new Dairy("Milk", 2.99f));
		catalog.addProduct(new Dairy("Cheese", 4.50f));
		catalog.addProduct(new Dairy("Yogurt", 1.99f));
		catalog.addProduct(new Fresh("Apples", 0.50f, 5.0f)); // $0.50/kg, 5kg
		catalog.addProduct(new Fresh("Bananas", 0.30f, 3.0f)); // $0.30/kg, 3kg
		catalog.addProduct(new Fresh("Tomatoes", 1.20f, 2.0f)); // $1.20/kg, 2kg
		
		// Print full catalog
		catalog.printCatalog();
		
		// Search for a product
		System.out.println("\nSearching for 'Milk':");
		Product milk = catalog.findProduct("Milk");
		if (milk != null) {
			System.out.println("Found: " + milk.getName() + " - $" + milk.getPrice());
		}
		
		// Remove a product
		System.out.println("\nRemoving 'Yogurt':");
		catalog.removeProduct("Yogurt");
		
		// Print updated stats
		System.out.println("\nUpdated catalog has " + catalog.getProductCount() + " products");
		System.out.println("Fresh products: " + catalog.getFreshProducts().size());
		System.out.println("Dairy products: " + catalog.getDairyProducts().size());
	}
}
