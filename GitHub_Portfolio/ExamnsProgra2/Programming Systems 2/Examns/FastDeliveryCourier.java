/**
 * ========================================
 * FASTDELIVERYCOURIER.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class FastDeliveryCourier extends Courier {
	
	protected double cost; 
	
	public FastDeliveryCourier(String name) {
		super(name);
	}
	
	public FastDeliveryCourier() {
		super("");
	} 
	
	
	public double calculateComisiion(double cost) throws LowCostException{
		if (cost<20) {
			
			throw new LowCostException("Minimum cost is not reached");
			
		}else {
			cost = cost*0.2;
			return cost;
		}
		
	}
	
	
	
	
	

}

