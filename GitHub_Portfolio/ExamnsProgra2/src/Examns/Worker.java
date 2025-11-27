/**
 * ========================================
 * WORKER.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class Worker extends Person {

	private static int salary = 2000;
	private static String position;

	private final static String WHAREHOUSE = "stockman";
	private final static String SELLER = "sales";

	public Worker(int id, String firstName, String lastName, String email, int salary, String position) {
		super(id, firstName, lastName, email);
		this.salary = salary;
		if ((position.equals("stockman")) || (position.equals("sales"))) {
			this.position = position;
		} else {
			this.position = this.WHAREHOUSE;
		}
	}

	public String toString() {
		String str = super.toString() + " salary: " + this.salary + " psotion: " + this.position;
		return str;
	}

}

