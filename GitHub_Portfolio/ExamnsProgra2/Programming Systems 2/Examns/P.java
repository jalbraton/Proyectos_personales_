/**
 * ========================================
 * P.JAVA - Parent Class with Instance Tracking
 * ========================================
 * 
 * Enunciado: Clase base que implementa seguimiento de instancias.
 * Demuestra conceptos de herencia, variables estáticas y
 * patrones de diseño para control de objetos.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class P {

	protected static int p = 0;
	protected static int totalCreated = 0;
	protected static int totalDestroyed = 0;
	protected int instanceId;
	protected String instanceName;
	
	public P() {
		p++;
		totalCreated++;
		this.instanceId = totalCreated;
		this.instanceName = "Instance-" + instanceId;
	}
	
	public P(String name) {
		this();
		this.instanceName = name;
	}
	
	public static int getActiveInstances() {
		return p;
	}
	
	public static int getTotalCreated() {
		return totalCreated;
	}
	
	public static int getTotalDestroyed() {
		return totalDestroyed;
	}
	
	public int getInstanceId() {
		return instanceId;
	}
	
	public String getInstanceName() {
		return instanceName;
	}
	
	public void setInstanceName(String name) {
		this.instanceName = name;
	}
	
	protected void finalize() throws Throwable {
		try {
			p--;
			totalDestroyed++;
		} finally {
			super.finalize();
		}
	}
	
	public void displayInfo() {
		System.out.println("[" + getClass().getSimpleName() + "] " + 
		                   "ID: " + instanceId + ", Name: " + instanceName);
	}
	
	public static void displayStatistics() {
		System.out.println("\n=== Instance Statistics ===");
		System.out.println("Active instances: " + p);
		System.out.println("Total created: " + totalCreated);
		System.out.println("Total destroyed: " + totalDestroyed);
	}
	
	public static void resetCounters() {
		p = 0;
		totalCreated = 0;
		totalDestroyed = 0;
	}
	
	public String toString() {
		return getClass().getSimpleName() + "[id=" + instanceId + 
		       ", name=" + instanceName + "]";
	}
	
	public static void main(String[] args) {
		System.out.println("╔════════════════════════════════════════════════╗");
		System.out.println("║     INSTANCE TRACKING DEMONSTRATION           ║");
		System.out.println("╚════════════════════════════════════════════════╝\n");
		
		System.out.println("Creating P instances...");
		P p1 = new P();
		P p2 = new P("CustomP");
		P p3 = new P("SpecialP");
		
		p1.displayInfo();
		p2.displayInfo();
		p3.displayInfo();
		
		P.displayStatistics();
		
		System.out.println("\nCreating H instances (subclass)...");
		H h1 = new H();
		H h2 = new H("CustomH");
		
		h1.displayInfo();
		h2.displayInfo();
		
		P.displayStatistics();
		H.displayStatistics();
		
		System.out.println("\n=== Object Details ===");
		System.out.println("p1: " + p1);
		System.out.println("p2: " + p2);
		System.out.println("p3: " + p3);
		System.out.println("h1: " + h1);
		System.out.println("h2: " + h2);
		
		System.out.println("\nTotal P+H instances: " + P.getActiveInstances());
		System.out.println("Combined counter (p+h): " + (P.p + H.h));
	}
}

