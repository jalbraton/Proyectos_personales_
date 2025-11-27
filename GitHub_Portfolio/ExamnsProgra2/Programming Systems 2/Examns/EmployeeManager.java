/**
 * ========================================
 * EMPLOYEEMANAGER.JAVA - Employee Management
 * ========================================
 * 
 * Enunciado: Sistema de gestión de empleados con
 * cálculo de nóminas, bonos y reportes de personal.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class EmployeeManager {
	
	private ArrayList<Worker> employees;
	private String companyName;
	
	public EmployeeManager(String companyName) {
		this.companyName = companyName;
		this.employees = new ArrayList<Worker>();
	}
	
	public void hireEmployee(Worker worker) {
		employees.add(worker);
		System.out.println("Hired: " + worker.getFirstName() + " " + worker.getLastName());
	}
	
	public void fireEmployee(int id) {
		for (int i = 0; i < employees.size(); i++) {
			if (employees.get(i).getId() == id) {
				Worker fired = employees.remove(i);
				System.out.println("Fired: " + fired.getFirstName() + " " + fired.getLastName());
				return;
			}
		}
		System.out.println("Employee with ID " + id + " not found");
	}
	
	public Worker findEmployee(int id) {
		for (Worker worker : employees) {
			if (worker.getId() == id) {
				return worker;
			}
		}
		return null;
	}
	
	public ArrayList<Worker> getEmployeesByPosition(String position) {
		ArrayList<Worker> result = new ArrayList<Worker>();
		for (Worker worker : employees) {
			if (worker.toString().contains(position)) {
				result.add(worker);
			}
		}
		return result;
	}
	
	public int getTotalEmployees() {
		return employees.size();
	}
	
	public void printEmployeeList() {
		System.out.println("\n╔════════════════════════════════════════════╗");
		System.out.println("║         " + companyName + " - EMPLOYEES          ║");
		System.out.println("╚════════════════════════════════════════════╝");
		System.out.println("Total Employees: " + employees.size());
		System.out.println("--------------------------------------------");
		
		for (int i = 0; i < employees.size(); i++) {
			Worker w = employees.get(i);
			System.out.println((i+1) + ". ID: " + w.getId() + " | " + 
			                   w.getFirstName() + " " + w.getLastName());
		}
		System.out.println("============================================\n");
	}
	
	public static void main(String[] args) {
		EmployeeManager manager = new EmployeeManager("Tech Corp");
		
		// Hire employees
		manager.hireEmployee(new Worker(1, "Alice", "Johnson", "alice@techcorp.com", 3000, "sales"));
		manager.hireEmployee(new Worker(2, "Bob", "Williams", "bob@techcorp.com", 2800, "stockman"));
		manager.hireEmployee(new Worker(3, "Carol", "Davis", "carol@techcorp.com", 3200, "sales"));
		manager.hireEmployee(new Worker(4, "David", "Miller", "david@techcorp.com", 2600, "stockman"));
		manager.hireEmployee(new Worker(5, "Emma", "Wilson", "emma@techcorp.com", 3500, "sales"));
		
		// Print list
		manager.printEmployeeList();
		
		// Find employee
		Worker found = manager.findEmployee(3);
		if (found != null) {
			System.out.println("Found employee: " + found.getFirstName() + " " + found.getLastName());
		}
		
		// Get by position
		ArrayList<Worker> sales = manager.getEmployeesByPosition("sales");
		System.out.println("\nSales employees: " + sales.size());
		
		ArrayList<Worker> warehouse = manager.getEmployeesByPosition("stockman");
		System.out.println("Warehouse employees: " + warehouse.size());
		
		// Fire employee
		manager.fireEmployee(2);
		manager.printEmployeeList();
	}
}
