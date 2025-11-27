/**
 * ========================================
 * COURIERMANAGER.JAVA - Courier Management System
 * ========================================
 * 
 * Enunciado: Sistema de gestión de mensajeros y envíos.
 * Calcula comisiones, gestiona múltiples couriers y
 * lleva registro de envíos procesados.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class CourierManager {
	
	private ArrayList<Courier> couriers;
	private ArrayList<Shipment> shipments;
	
	public CourierManager() {
		this.couriers = new ArrayList<Courier>();
		this.shipments = new ArrayList<Shipment>();
	}
	
	public void addCourier(Courier courier) {
		couriers.add(courier);
		System.out.println("Courier added: " + courier.name);
	}
	
	public void addShipment(Shipment shipment) {
		shipments.add(shipment);
		System.out.println("Shipment added. Total shipments: " + shipments.size());
	}
	
	public double calculateTotalCommissions(double baseCost) throws LowCostException {
		double total = 0.0;
		for (Courier courier : couriers) {
			try {
				total += courier.calculateComisiion(baseCost);
			} catch (LowCostException e) {
				System.out.println("Warning: " + courier.name + " - " + e.getMessage());
				throw e;
			}
		}
		return total;
	}
	
	public ArrayList<Shipment> getShipmentsByCourier(Courier targetCourier) {
		ArrayList<Shipment> result = new ArrayList<Shipment>();
		for (Shipment shipment : shipments) {
			if (shipment.getCourier().equals(targetCourier)) {
				result.add(shipment);
			}
		}
		return result;
	}
	
	public ArrayList<Shipment> getShipmentsByStatus(int status) {
		ArrayList<Shipment> result = new ArrayList<Shipment>();
		for (Shipment shipment : shipments) {
			if (shipment.getStatus() == status) {
				result.add(shipment);
			}
		}
		return result;
	}
	
	public int getTotalCouriers() {
		return couriers.size();
	}
	
	public int getTotalShipments() {
		return shipments.size();
	}
	
	public void printStatistics() {
		System.out.println("===== Courier Manager Statistics =====");
		System.out.println("Total couriers: " + couriers.size());
		System.out.println("Total shipments: " + shipments.size());
		System.out.println("Sent shipments: " + getShipmentsByStatus(Shipment.SENT).size());
		System.out.println("Delivered shipments: " + getShipmentsByStatus(Shipment.DELIVERED).size());
		System.out.println("======================================");
	}
	
	public static void main(String[] args) {
		CourierManager manager = new CourierManager();
		
		// Create couriers
		FastDeliveryCourier courier1 = new FastDeliveryCourier("Express Delivery");
		FastDeliveryCourier courier2 = new FastDeliveryCourier("Quick Service");
		
		manager.addCourier(courier1);
		manager.addCourier(courier2);
		
		// Create orders and shipments
		try {
			ArrayList<Product> products = new ArrayList<Product>();
			products.add(new Dairy("Milk", 2.5f));
			products.add(new Fresh("Tomatoes", 1.5f, 2.0f));
			
			Order order1 = new Order(products);
			Shipment shipment1 = new Shipment(order1, 25.0, courier1);
			shipment1.setStatus(Shipment.SENT);
			shipment1.addMessage("Package picked up");
			
			manager.addShipment(shipment1);
			
			// Calculate commissions
			double commission = courier1.calculateComisiion(25.0);
			System.out.println("Commission for $25 order: $" + commission);
			
			// Print statistics
			manager.printStatistics();
			
		} catch (EmptyOrderException | LowCostException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
}
