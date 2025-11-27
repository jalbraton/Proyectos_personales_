/**
 * ========================================
 * EPISODE.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class Episode extends Media{
	
	private int duration;
	private String Id;
	
	public Episode(String name, String category, int duration, String id) {
		super(name, category);
		setDuration(duration); 
		this.Id = id;
	}
	
	public Episode(String name, String category, int duration) {
		super(name, category);
		setDuration(duration); 
		this.Id = null;
	}
	
	public void setDuration(int duration){
		 this.duration = duration;
	}
	
	protected int computeTotalDuration() {
		return this.duration;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}

