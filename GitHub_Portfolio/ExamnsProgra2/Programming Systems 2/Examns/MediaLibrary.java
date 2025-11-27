/**
 * ========================================
 * MEDIALIBRARY.JAVA - Media Collection Manager
 * ========================================
 * 
 * Enunciado: Sistema de gestión de biblioteca multimedia.
 * Organiza series y episodios, calcula duraciones totales
 * y genera estadísticas de contenido.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

import java.util.ArrayList;

public class MediaLibrary {
	
	private ArrayList<Series> seriesCollection;
	private String libraryName;
	
	public MediaLibrary(String libraryName) {
		this.libraryName = libraryName;
		this.seriesCollection = new ArrayList<Series>();
	}
	
	public void addSeries(Series series) {
		seriesCollection.add(series);
		System.out.println("Series added: " + series.name);
	}
	
	public void removeSeries(String seriesName) {
		for (int i = 0; i < seriesCollection.size(); i++) {
			if (seriesCollection.get(i).name.equalsIgnoreCase(seriesName)) {
				seriesCollection.remove(i);
				System.out.println("Series removed: " + seriesName);
				return;
			}
		}
		System.out.println("Series not found: " + seriesName);
	}
	
	public Series findSeries(String seriesName) {
		for (Series series : seriesCollection) {
			if (series.name.equalsIgnoreCase(seriesName)) {
				return series;
			}
		}
		return null;
	}
	
	public int getTotalDuration() {
		int total = 0;
		for (Series series : seriesCollection) {
			total += series.computeTotalDuration();
		}
		return total;
	}
	
	public ArrayList<Series> getSeriesByCategory(String category) {
		ArrayList<Series> result = new ArrayList<Series>();
		for (Series series : seriesCollection) {
			if (series.category.equalsIgnoreCase(category)) {
				result.add(series);
			}
		}
		return result;
	}
	
	public void printLibraryStats() {
		System.out.println("===== " + libraryName + " Statistics =====");
		System.out.println("Total series: " + seriesCollection.size());
		System.out.println("Total duration: " + getTotalDuration() + " minutes");
		System.out.println("Average duration per series: " + 
		                   (seriesCollection.size() > 0 ? getTotalDuration() / seriesCollection.size() : 0) + 
		                   " minutes");
		System.out.println("======================================");
	}
	
	public void printFullLibrary() {
		System.out.println("===== " + libraryName + " - Full Catalog =====");
		for (int i = 0; i < seriesCollection.size(); i++) {
			Series s = seriesCollection.get(i);
			System.out.println((i + 1) + ". " + s.name + 
			                   " | Category: " + s.category +
			                   " | Total Duration: " + s.computeTotalDuration() + " min");
		}
		System.out.println("==========================================");
	}
	
	public int getSeriesCount() {
		return seriesCollection.size();
	}
	
	public static void main(String[] args) {
		MediaLibrary library = new MediaLibrary("Netflix Clone");
		
		try {
			// Create episodes for series 1
			ArrayList<Episode> episodes1 = new ArrayList<Episode>();
			episodes1.add(new Episode("Pilot", "Drama", 45, "S01E01"));
			episodes1.add(new Episode("The Beginning", "Drama", 42, "S01E02"));
			episodes1.add(new Episode("Revelations", "Drama", 44, "S01E03"));
			
			Series series1 = new Series("Breaking Bad", "Drama", 3, episodes1);
			library.addSeries(series1);
			
			// Create episodes for series 2
			ArrayList<Episode> episodes2 = new ArrayList<Episode>();
			episodes2.add(new Episode("Winter is Coming", "Fantasy", 60, "S01E01"));
			episodes2.add(new Episode("The King's Road", "Fantasy", 56, "S01E02"));
			episodes2.add(new Episode("Lord Snow", "Fantasy", 58, "S01E03"));
			episodes2.add(new Episode("Cripples and Bastards", "Fantasy", 56, "S01E04"));
			
			Series series2 = new Series("Game of Thrones", "Fantasy", 4, episodes2);
			library.addSeries(series2);
			
			// Create episodes for series 3
			ArrayList<Episode> episodes3 = new ArrayList<Episode>();
			episodes3.add(new Episode("The One Where It All Began", "Comedy", 22, "S01E01"));
			episodes3.add(new Episode("The One with the Sonogram", "Comedy", 22, "S01E02"));
			
			Series series3 = new Series("Friends", "Comedy", 2, episodes3);
			library.addSeries(series3);
			
			// Print library
			library.printFullLibrary();
			library.printLibraryStats();
			
			// Search by category
			System.out.println("\nDrama series:");
			ArrayList<Series> dramas = library.getSeriesByCategory("Drama");
			System.out.println("Found " + dramas.size() + " drama series");
			
			// Try to add episode (will fail if series is full)
			try {
				Episode newEp = new Episode("New Episode", "Drama", 43, "S01E04");
				series1.addNewEpisode(newEp);
				System.out.println("Episode added successfully");
			} catch (FullSeriesException e) {
				System.out.println("Cannot add episode: " + e.getMessage());
			}
			
		} catch (FullSeriesException e) {
			System.out.println("Error creating series: " + e.getMessage());
		}
	}
}
