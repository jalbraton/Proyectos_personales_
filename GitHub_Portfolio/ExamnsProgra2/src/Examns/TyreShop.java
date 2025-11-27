/**
 * ========================================
 * TYRESHOP.JAVA - Tyre Shop Management
 * ========================================
 * 
 * Enunciado: Sistema de gestión de tienda de neumáticos.
 * Permite añadir, buscar y listar diferentes tipos de neumáticos
 * (All Season, Summer, Winter).
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class TyreShop {
	
	private String name;
	private ArrayList<AdvanceTyre> inventory;
	
	public TyreShop(String name) {
		this.name = name;
		this.inventory = new ArrayList<AdvanceTyre>();
	}
	
	public void addTyre(AdvanceTyre tyre) {
		inventory.add(tyre);
		System.out.println("Tyre added successfully. Total tyres: " + inventory.size());
	}
	
	public void removeTyre(int index) {
		if (index >= 0 && index < inventory.size()) {
			inventory.remove(index);
			System.out.println("Tyre removed. Remaining tyres: " + inventory.size());
		} else {
			System.out.println("Invalid index");
		}
	}
	
	public ArrayList<AdvanceTyre> getAllTyres() {
		return new ArrayList<AdvanceTyre>(inventory);
	}
	
	public ArrayList<AdvanceTyre> getTyresByOptimalTemp(double minTemp, double maxTemp) {
		ArrayList<AdvanceTyre> result = new ArrayList<AdvanceTyre>();
		for (AdvanceTyre tyre : inventory) {
			double temp = tyre.calculateOptimunTemp();
			if (temp >= minTemp && temp <= maxTemp) {
				result.add(tyre);
			}
		}
		return result;
	}
	
	public int getInventorySize() {
		return inventory.size();
	}
	
	public String getName() {
		return name;
	}
	
	public void printInventory() {
		System.out.println("===== " + name + " Inventory =====");
		System.out.println("Total tyres: " + inventory.size());
		for (int i = 0; i < inventory.size(); i++) {
			AdvanceTyre tyre = inventory.get(i);
			System.out.println((i + 1) + ". Type: " + tyre.getClass().getSimpleName() + 
			                   " | Height: " + tyre.getHeight() + 
			                   " | Optimal Temp: " + tyre.calculateOptimunTemp() + "°C");
		}
		System.out.println("================================");
	}
	
	public static void main(String[] args) {
		TyreShop shop = new TyreShop("Premium Tyres");
		
		try {
			// Adding different types of tyres
			shop.addTyre(new AllSeasonTyre(55.0));
			shop.addTyre(new WinterTyre(1.2, 60.0));
			shop.addTyre(new SummerTyres(50.0, 1.5, false));
			shop.addTyre(new AllSeasonTyre(45.0));
			shop.addTyre(new SummerTyres(55.0, 1.3, true));
			
			// Print inventory
			shop.printInventory();
			
			// Find tyres by temperature range
			System.out.println("\nTyres with optimal temperature between 75-85°C:");
			ArrayList<AdvanceTyre> filtered = shop.getTyresByOptimalTemp(75.0, 85.0);
			System.out.println("Found " + filtered.size() + " tyres");
			
		} catch (NegativeNumberException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
