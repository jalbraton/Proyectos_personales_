/**
 * ========================================
 * H.JAVA - Hierarchical Instance Tracker
 * ========================================
 * 
 * Enunciado: Subclase de P que extiende el tracking de instancias.
 * Demuestra herencia, polimorfismo y manejo avanzado de
 * contadores estáticos en jerarquías de clases.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class H extends P {
	protected static int h = 0;
	protected static int hTotalCreated = 0;
	protected static int hTotalDestroyed = 0;
	private String hierarchyLevel;
	
	public H() {
		super();
		h++;
		hTotalCreated++;
		this.hierarchyLevel = "Level-1";
		setInstanceName("H-Instance-" + hTotalCreated);
	}
	
	public H(String name) {
		super(name);
		h++;
		hTotalCreated++;
		this.hierarchyLevel = "Level-1";
	}
	
	public H(String name, String level) {
		this(name);
		this.hierarchyLevel = level;
	}
	
	public static int getActiveHInstances() {
		return h;
	}
	
	public static int getTotalHCreated() {
		return hTotalCreated;
	}
	
	public static int getTotalHDestroyed() {
		return hTotalDestroyed;
	}
	
	public String getHierarchyLevel() {
		return hierarchyLevel;
	}
	
	public void setHierarchyLevel(String level) {
		this.hierarchyLevel = level;
	}
	
	protected void finalize() throws Throwable {
		try {
			h--;
			hTotalDestroyed++;
		} finally {
			super.finalize();
		}
	}
	
	public void displayInfo() {
		System.out.println("[" + getClass().getSimpleName() + "] " + 
		                   "ID: " + getInstanceId() + ", Name: " + getInstanceName() + 
		                   ", Level: " + hierarchyLevel);
	}
	
	public static void displayStatistics() {
		System.out.println("\n=== H Instance Statistics ===");
		System.out.println("Active H instances: " + h);
		System.out.println("Total H created: " + hTotalCreated);
		System.out.println("Total H destroyed: " + hTotalDestroyed);
		System.out.println("\n=== Parent P Statistics ===");
		System.out.println("Active P instances (including H): " + p);
		System.out.println("Total P+H created: " + getTotalCreated());
	}
	
	public int getCombinedCount() {
		return p + h;
	}
	
	public static int getTotalCombinedCount() {
		return P.p + H.h;
	}
	
	public String toString() {
		return "H[id=" + getInstanceId() + ", name=" + getInstanceName() + 
		       ", level=" + hierarchyLevel + "]";
	}
	
	public static void main(String[] args) {
		System.out.println("╔════════════════════════════════════════════════╗");
		System.out.println("║     HIERARCHICAL TRACKING DEMONSTRATION        ║");
		System.out.println("╚════════════════════════════════════════════════╝\n");
		
		System.out.println("=== Test 1: Basic Creation ===");
		P a = new P();
		H b = new H();
		System.out.println("Combined count (p + h): " + (p + h));
		a.displayInfo();
		b.displayInfo();
		
		System.out.println("\n=== Test 2: Multiple H Instances ===");
		H h1 = new H("Alpha");
		H h2 = new H("Beta", "Level-2");
		H h3 = new H("Gamma", "Level-3");
		
		h1.displayInfo();
		h2.displayInfo();
		h3.displayInfo();
		
		H.displayStatistics();
		
		System.out.println("\n=== Test 3: Mixed Creation ===");
		P p1 = new P("ParentA");
		H h4 = new H("ChildA");
		P p2 = new P("ParentB");
		H h5 = new H("ChildB", "Level-1");
		
		System.out.println("\nAll instances:");
		System.out.println("a: " + a);
		System.out.println("b: " + b);
		System.out.println("h1: " + h1);
		System.out.println("h2: " + h2);
		System.out.println("h3: " + h3);
		System.out.println("p1: " + p1);
		System.out.println("h4: " + h4);
		System.out.println("p2: " + p2);
		System.out.println("h5: " + h5);
		
		System.out.println("\n=== Final Statistics ===");
		System.out.println("Total active P instances: " + P.getActiveInstances());
		System.out.println("Total active H instances: " + H.getActiveHInstances());
		System.out.println("Combined total: " + H.getTotalCombinedCount());
		
		P.displayStatistics();
		H.displayStatistics();
	}
}

