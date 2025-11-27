/**
 * ========================================
 * MEDIA.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public abstract class Media {
	
	protected String name;
	protected String category;
	
	public Media(String name, String category) {
		this.name = name;
		this.category = category;
	}
	
	protected abstract int computeTotalDuration();

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

