/**
 * ========================================
 * SERIES.JAVA
 * ========================================
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;
import java.util.ArrayList;

public class Series extends Media {
	private int numberOfEpisodes;
	private ArrayList<Episode> list;
	
	public Series(String name, String category, int number, ArrayList<Episode> a) throws FullSeriesException{
		super(name, category);
		this.numberOfEpisodes = number;
		if(a.size()>10) {
			throw new FullSeriesException("More than 10 episodes");
		}else {
			this.list = a;
		}
		
	}
	
	protected int computeTotalDuration() {
		int a = 0;
		for(int i = 0; i<this.list.size(); i++) {
			a = list.get(i).computeTotalDuration() + a;
		}
		return a;
	}
	public void addNewEpisode(Episode j) throws FullSeriesException {
		if (list.size()==10) {
			throw new FullSeriesException("No puedes");
		}
		this.list.add(j);
		this.numberOfEpisodes++;
	}
	
	

	public static void main(String[] args) {
	
	}

}

