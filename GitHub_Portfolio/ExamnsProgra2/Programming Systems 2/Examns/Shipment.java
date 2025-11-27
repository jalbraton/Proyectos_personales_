/**
 * ========================================
 * SHIPMENT.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;
import java.util.ArrayList;

public class Shipment {
	
	private Order order;
	private double price;
	private Courier courier;
	
	private int status;
	public final static int SENT = 1;
	public final static int DELIVERED = 2;

	private ArrayList<String> messages;

	public Shipment(Order order, double price, Courier courier) {
		this.order = order;
		this.price = price;
		this.courier = courier;
		this.status = 0;
		this.messages = new ArrayList<String>();
	}
	
	public Order getOrder() {
		return order;
	}
	
	public double getPrice() {
		return price;
	}
	
	public Courier getCourier() {
		return courier;
	}
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int status) {
		this.status = status;
	}
	
	public void addMessage(String message) {
		messages.add(message);
	}
	
	public ArrayList<String> getMessages() {
		return messages;
	}
}

