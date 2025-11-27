/**
 * ========================================
 * CANDLEVISUALIZATION.JAVA - Data Visualization
 * ========================================
 * 
 * Enunciado: Crea visualizaciones en consola del crecimiento
 * de velas a lo largo del tiempo. Genera gráficos de barras,
 * líneas y tablas formateadas.
 * 
 * @author jalbraton
 * @date 2023-2024
 * @github github.com/jalbatron
 * ========================================
 */

package Examns;

public class CandleVisualization {
	
	private String chartTitle;
	private CandleCounterRecursive counter;
	
	public CandleVisualization(String chartTitle) {
		this.chartTitle = chartTitle;
		this.counter = new CandleCounterRecursive();
	}
	
	private String repeatString(String str, int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append(str);
		}
		return sb.toString();
	}
	
	public void drawBarChart(int years) {
		System.out.println("========== BAR CHART: " + chartTitle + " ==========");
		System.out.println("Candle consumption over " + years + " years\n");
		
		// Find max value for scaling
		long maxValue = counter.count(years);
		int maxBarLength = 50;
		
		for (int i = 1; i <= years; i++) {
			long candles = counter.count(i);
			int barLength = (int) ((candles * maxBarLength) / maxValue);
			
			System.out.printf("Year %2d [%3d] ", i, candles);
			for (int j = 0; j < barLength; j++) {
				System.out.print("█");
			}
			System.out.println();
		}
		System.out.println(repeatString("=", 60));
	}
	
	public void drawLineChart(int years) {
		System.out.println("========== LINE CHART: " + chartTitle + " ==========");
		System.out.println();
		
		long maxValue = counter.count(years);
		int chartHeight = 15;
		long[][] grid = new long[chartHeight][years];
		
		// Populate grid
		for (int i = 0; i < years; i++) {
			long value = counter.count(i + 1);
			int height = (int) ((value * chartHeight) / maxValue);
			
			for (int j = 0; j < height; j++) {
				grid[chartHeight - 1 - j][i] = 1;
			}
		}
		
		// Draw grid
		System.out.printf("%4d |", maxValue);
		for (int i = 0; i < chartHeight; i++) {
			if (i > 0) {
				long yValue = maxValue - ((maxValue * i) / chartHeight);
				System.out.printf("%4d |", yValue);
			}
			for (int j = 0; j < years; j++) {
				System.out.print(grid[i][j] == 1 ? " ●" : "  ");
			}
			System.out.println();
		}
		
		System.out.print("     +");
		System.out.println(repeatString("-", years * 2));
		System.out.print("      ");
		for (int i = 1; i <= years; i++) {
			System.out.printf("%2d", i);
		}
		System.out.println();
		System.out.println(repeatString("=", 60));
	}
	
	public void drawTable(int years) {
		System.out.println("========== TABLE: " + chartTitle + " ==========");
		System.out.println();
		System.out.println("╔═══════╦═══════════╦═══════════╦══════════════╗");
		System.out.println("║ Year  ║  Candles  ║   Delta   ║  Cumulative  ║");
		System.out.println("╠═══════╬═══════════╬═══════════╬══════════════╣");
		
		long previousCandles = 0;
		long cumulative = 0;
		
		for (int i = 1; i <= years; i++) {
			long candles = counter.count(i);
			long delta = candles - previousCandles;
			cumulative += candles;
			
			System.out.printf("║  %2d   ║  %6d   ║  +%5d   ║  %9d   ║\n", 
			                  i, candles, delta, cumulative);
			
			previousCandles = candles;
		}
		
		System.out.println("╚═══════╩═══════════╩═══════════╩══════════════╝");
	}
	
	public void drawProgressBar(int currentYear, int totalYears) {
		long current = counter.count(currentYear);
		long total = counter.count(totalYears);
		
		double percentage = (double) current / total * 100;
		int filledLength = (int) (percentage / 2); // 50 chars max
		
		System.out.print("Progress [");
		for (int i = 0; i < 50; i++) {
			System.out.print(i < filledLength ? "█" : "░");
		}
		System.out.printf("] %.1f%% (%d/%d candles)\n", percentage, current, total);
	}
	
	public void drawAllCharts(int years) {
		System.out.println("\n╔═══════════════════════════════════════════════╗");
		System.out.println("║        CANDLE VISUALIZATION SUITE             ║");
		System.out.println("╚═══════════════════════════════════════════════╝\n");
		
		drawBarChart(years);
		System.out.println("\n");
		
		drawLineChart(years);
		System.out.println("\n");
		
		drawTable(years);
		System.out.println("\n");
		
		System.out.println("Progress indicators:");
		for (int i = 1; i <= years; i += Math.max(1, years / 5)) {
			drawProgressBar(i, years);
		}
	}
	
	public static void main(String[] args) {
		CandleVisualization viz = new CandleVisualization("Candle Growth Analysis");
		
		// Draw all visualizations for 10 years
		viz.drawAllCharts(10);
		
		// Individual charts
		System.out.println("\n\n=== CUSTOM VISUALIZATIONS ===\n");
		
		CandleVisualization shortTerm = new CandleVisualization("Short Term (5 years)");
		shortTerm.drawBarChart(5);
		
		System.out.println("\n");
		
		CandleVisualization longTerm = new CandleVisualization("Long Term (20 years)");
		longTerm.drawTable(20);
	}
}
