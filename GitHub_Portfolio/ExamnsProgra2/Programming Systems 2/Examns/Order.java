/**
 * ========================================
 * ORDER.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class Order {

	private static int id;
	private ArrayList<Product> list;

	public Order() {

	}

	public Order(ArrayList<Product> lis) throws EmptyOrderException {
		if (lis.size() == 0) {
			throw new EmptyOrderException("There is nothing in the order list");
		} else {
			this.list = lis;
			id++;
		}
	}

	public ArrayList<Product> addProduct(Product p) {

		this.list.add(p);
		id++;

		return this.list;
	}

	public String toString() {
		String str = "";
		str = "Invoce number: " + id ;
		String str2 = "";
		for (int i = 0; i<this.list.size();i++) {
		 str2 = list.get(i).getName() + ": "  + list.get(i).getPrice() + " euros. \n";
		}
		float a = 0.0f;
		for (int j = 0; j<this.list.size(); j++) {
		a  = a + list.get(j).getPrice();	
		}
		String str3 = str + "\n" +str2+"\n"+"Total price: " + a;
		
		return str3;
	}

}

