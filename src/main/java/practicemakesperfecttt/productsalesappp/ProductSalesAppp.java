/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package practicemakesperfecttt.productsalesappp;

/**
 *
 * @author manga
 */
public class ProductSalesAppp {

   

    // Helper to print a divider
    private static void printLine() {
        System.out.println("--------------------------------------------------");
    }

    public static void main(String[] args) {
        // ===== Example data =====
        // Row 0 = Year 1 (older year), Row 1 = Year 2 (more recent year)
        // Change these values to match your exam table.
        int[][] sales = {
            // Year 1: Q1, Q2, Q3, Q4  (if your exam only has 3 quarters, use 3 values here)
            { 1200, 1500, 1100, 1800 },
            // Year 2: Q1, Q2, Q3, Q4
            { 1300, 1400, 1700, 1600 }
        };

        // If your exam really has 3 quarter columns, example:
        // int[][] sales = { {1200,1500,1100}, {1300,1400,1700} };

        int years = sales.length;        // expected 2
        int quarters = sales[0].length;  // number of quarter columns (flexible)

        // Validate rectangular shape
        for (int r = 0; r < years; r++) {
            if (sales[r].length != quarters) {
                System.err.println("All rows must have the same number of quarters.");
                return;
            }
        }

        // Compute per-quarter totals (sum across years)
        int[] quarterTotals = new int[quarters];
        for (int q = 0; q < quarters; q++) {
            int sum = 0;
            for (int y = 0; y < years; y++) sum += sales[y][q];
            quarterTotals[q] = sum;
        }

        // Compute per-year totals and overall stats
        int[] yearTotals = new int[years];
        int overallTotal = 0;
        int count = years * quarters;
        int overallMax = Integer.MIN_VALUE;
        int maxYear = -1, maxQuarter = -1;
        int overallMin = Integer.MAX_VALUE;
        int minYear = -1, minQuarter = -1;

        for (int y = 0; y < years; y++) {
            int ysum = 0;
            for (int q = 0; q < quarters; q++) {
                int val = sales[y][q];
                ysum += val;
                overallTotal += val;

                if (val > overallMax) {
                    overallMax = val;
                    maxYear = y;
                    maxQuarter = q;
                }
                if (val < overallMin) {
                    overallMin = val;
                    minYear = y;
                    minQuarter = q;
                }
            }
            yearTotals[y] = ysum;
        }

        double overallAvg = (double) overallTotal / count;

        // ===== Print report =====
        System.out.println("PRODUCT SALES REPORT (2-year period)");
        printLine();

        // Header
        System.out.printf("%-8s", "Year");
        for (int q = 0; q < quarters; q++) System.out.printf("%8s", "Q" + (q+1));
        System.out.printf("%12s%n", "YearTotal");
        printLine();

        // Rows: each year
        for (int y = 0; y < years; y++) {
            System.out.printf("Year %d  ", y+1);
            for (int q = 0; q < quarters; q++) System.out.printf("%8d", sales[y][q]);
            System.out.printf("%12d%n", yearTotals[y]);
        }

        printLine();
        // Quarter totals row
        System.out.printf("%-8s", "QuarterTotal");
        for (int q = 0; q < quarters; q++) System.out.printf("%8d", quarterTotals[q]);
        System.out.printf("%12d%n", overallTotal);
        printLine();

        // Overall stats
        System.out.printf("Overall total (2 years) : %d%n", overallTotal);
        System.out.printf("Overall average (per quarter entry) : %.2f%n", overallAvg);
        System.out.printf("Maximum single value : %d (Year %d, Q%d)%n", overallMax, maxYear+1, maxQuarter+1);
        System.out.printf("Minimum single value : %d (Year %d, Q%d)%n", overallMin, minYear+1, minQuarter+1);
        printLine();

        // Extra: per-quarter average across years (useful)
        System.out.println("Average per quarter across the 2 years:");
        for (int q = 0; q < quarters; q++) {
            double avgQ = quarterTotals[q] / (double) years;
            System.out.printf("  Q%d average : %.2f%n", q+1, avgQ);
        }

        // End
        printLine();
        System.out.println("End of report.");
    }

}