/**
 * ========================================
 * PROVIDER.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class Provider{
	
	private String vat;
	private String name;
	private String taxAddress;
	private Person contactPerson;

	
	public String toString() {
		return super.toString() + " vat: "  + this.vat + " name: " + this.name + " taxtAddres: " + this.taxAddress + " contactPerson" + this.contactPerson.toString();
	}
	
	public static void main(String[] args) {
		
	}

}

