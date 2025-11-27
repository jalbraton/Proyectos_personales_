/**
 * ========================================
 * COMPREHENSIVETEST.JAVA - Complete System Test
 * ========================================
 * 
 * Enunciado: Pruebas comprehensivas del sistema completo.
 * Integra y prueba todas las clases principales: productos,
 * neumáticos, couriers, series y recursión.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class ComprehensiveTest {
	
	public static void testProductSystem() {
		System.out.println("\n========== TESTING PRODUCT SYSTEM ==========");
		try {
			// Test Product Catalog
			ProductCatalog catalog = new ProductCatalog("Test Store");
			catalog.addProduct(new Dairy("Milk", 2.50f));
			catalog.addProduct(new Fresh("Oranges", 1.20f, 3.0f));
			
			ArrayList<Product> products = new ArrayList<Product>();
			products.add(new Dairy("Cheese", 5.00f));
			products.add(new Fresh("Lettuce", 0.80f, 1.5f));
			
			Order order = new Order(products);
			System.out.println("✓ Order created successfully");
			
			// Test Workers and Providers
			Worker worker = new Worker(1, "John", "Doe", "john@example.com", 2500, "sales");
			System.out.println("✓ Worker created: " + worker.toString());
			
		} catch (EmptyOrderException e) {
			System.out.println("✗ Order test failed: " + e.getMessage());
		}
	}
	
	public static void testTyreSystem() {
		System.out.println("\n========== TESTING TYRE SYSTEM ==========");
		try {
			TyreShop shop = new TyreShop("Test Tyre Shop");
			
			AllSeasonTyre tyre1 = new AllSeasonTyre(55.0);
			WinterTyre tyre2 = new WinterTyre(1.3, 60.0);
			SummerTyres tyre3 = new SummerTyres(50.0, 1.5, false);
			
			shop.addTyre(tyre1);
			shop.addTyre(tyre2);
			shop.addTyre(tyre3);
			
			System.out.println("✓ All Season optimal temp: " + tyre1.calculateOptimunTemp() + "°C");
			System.out.println("✓ Winter optimal temp: " + tyre2.calculateOptimunTemp() + "°C");
			System.out.println("✓ Summer optimal temp: " + tyre3.calculateOptimunTemp() + "°C");
			
			ArrayList<AdvanceTyre> filtered = shop.getTyresByOptimalTemp(70.0, 100.0);
			System.out.println("✓ Found " + filtered.size() + " tyres in temperature range");
			
		} catch (NegativeNumberException e) {
			System.out.println("✗ Tyre test failed: " + e.getMessage());
		}
	}
	
	public static void testCourierSystem() {
		System.out.println("\n========== TESTING COURIER SYSTEM ==========");
		try {
			CourierManager manager = new CourierManager();
			FastDeliveryCourier courier = new FastDeliveryCourier("Express");
			manager.addCourier(courier);
			
			// Test valid cost
			double commission = courier.calculateComisiion(50.0);
			System.out.println("✓ Commission for $50: $" + commission);
			
			// Test low cost exception
			try {
				courier.calculateComisiion(10.0);
				System.out.println("✗ Should have thrown LowCostException");
			} catch (LowCostException e) {
				System.out.println("✓ LowCostException caught correctly: " + e.getMessage());
			}
			
		} catch (LowCostException e) {
			System.out.println("✗ Courier test failed: " + e.getMessage());
		}
	}
	
	public static void testMediaSystem() {
		System.out.println("\n========== TESTING MEDIA SYSTEM ==========");
		try {
			MediaLibrary library = new MediaLibrary("Test Library");
			
			ArrayList<Episode> episodes = new ArrayList<Episode>();
			for (int i = 1; i <= 5; i++) {
				episodes.add(new Episode("Episode " + i, "Action", 45, "S01E0" + i));
			}
			
			Series series = new Series("Test Series", "Action", 5, episodes);
			library.addSeries(series);
			
			int totalDuration = series.computeTotalDuration();
			System.out.println("✓ Series total duration: " + totalDuration + " minutes");
			System.out.println("✓ Expected: " + (45 * 5) + " minutes");
			
			// Test adding to full series
			ArrayList<Episode> maxEpisodes = new ArrayList<Episode>();
			for (int i = 1; i <= 10; i++) {
				maxEpisodes.add(new Episode("Ep" + i, "Drama", 30, "E" + i));
			}
			
			Series fullSeries = new Series("Full Series", "Drama", 10, maxEpisodes);
			try {
				fullSeries.addNewEpisode(new Episode("Extra", "Drama", 30, "E11"));
				System.out.println("✗ Should have thrown FullSeriesException");
			} catch (FullSeriesException e) {
				System.out.println("✓ FullSeriesException caught correctly");
			}
			
		} catch (FullSeriesException e) {
			System.out.println("✗ Media test failed: " + e.getMessage());
		}
	}
	
	public static void testRecursionSystem() {
		System.out.println("\n========== TESTING RECURSION SYSTEM ==========");
		
		// Test candle counters
		CandleCounterRecursive recursive = new CandleCounterRecursive();
		CandleCounterIterative iterative = new CandleCounterIterative();
		
		long result1 = recursive.count(6);
		long result2 = iterative.count(6);
		
		System.out.println("✓ Recursive count(6): " + result1);
		System.out.println("✓ Iterative count(6): " + result2);
		System.out.println("✓ Results match: " + (result1 == result2));
		
		// Test bacteria counter
		BacteriaCounterIterative bacteria = new BacteriaCounterIterative();
		long bacteriaResult = bacteria.count(6);
		System.out.println("✓ Bacteria count(6): " + bacteriaResult);
	}
	
	public static void testExceptionHandling() {
		System.out.println("\n========== TESTING EXCEPTION HANDLING ==========");
		
		// Test PersonException
		try {
			Person person = new Person(-1, "Test", "User", "test@test.com");
			System.out.println("✗ Should have thrown PersonException");
		} catch (Exception e) {
			System.out.println("✓ PersonException handled correctly");
		}
		
		// Test EmptyOrderException
		try {
			ArrayList<Product> emptyList = new ArrayList<Product>();
			Order emptyOrder = new Order(emptyList);
			System.out.println("✗ Should have thrown EmptyOrderException");
		} catch (EmptyOrderException e) {
			System.out.println("✓ EmptyOrderException caught: " + e.getMessage());
		}
		
		// Test NegativeNumberException
		try {
			AllSeasonTyre negativeTyre = new AllSeasonTyre(-10.0);
			System.out.println("✗ Should have thrown NegativeNumberException");
		} catch (NegativeNumberException e) {
			System.out.println("✓ NegativeNumberException handled correctly");
		}
	}
	
	public static void main(String[] args) {
		System.out.println("╔════════════════════════════════════════════╗");
		System.out.println("║   COMPREHENSIVE SYSTEM TEST SUITE         ║");
		System.out.println("╚════════════════════════════════════════════╝");
		
		testProductSystem();
		testTyreSystem();
		testCourierSystem();
		testMediaSystem();
		testRecursionSystem();
		testExceptionHandling();
		
		System.out.println("\n╔════════════════════════════════════════════╗");
		System.out.println("║   ALL TESTS COMPLETED                     ║");
		System.out.println("╚════════════════════════════════════════════╝");
	}
}
