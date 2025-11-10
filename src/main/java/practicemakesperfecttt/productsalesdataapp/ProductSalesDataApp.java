/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package practicemakesperfecttt.productsalesdataapp;

/**
 *
 * @author mangangi PR
 * ST10456588
 * 
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ProductSalesDataApp
 *
 * - Two buttons: Load Product Data, Save Product Data
 * - Read-only text area to display the product sales data
 * - Read-only label to show number of years processed
 *
 * CSV format expected (optional header):
 * Microphone,Speakers,Mixing Desk
 * 300,150,700
 * 250,200,600
 */
public class ProductSalesDataApp extends JFrame implements ActionListener {

    private final JButton btnLoad = new JButton("Load Product Data");
    private final JButton btnSave = new JButton("Save Product Data");
    private final JTextArea txtDisplay = new JTextArea(10, 40);
    private final JLabel lblYearsProcessed = new JLabel("Years processed: 0");

    // File used to persist sample/load/save
    private final Path dataFile = Paths.get("product_sales.csv");

    // In-memory data representation (rows x columns). Initialized with sample.
    private int[][] data = {
            {300, 150, 700},
            {250, 200, 600}
    };
    private final String[] headers = {"Microphone", "Speakers", "Mixing Desk"};

    public ProductSalesDataApp() {
        super("Product Sales Data App");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 420);
        setLocationRelativeTo(null);

        initUi();
        // Display the initial (sample) data
        showDataInTextArea();
    }

    private void initUi() {
        // Top: two buttons
        JPanel pnlButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnLoad.addActionListener(this);
        btnSave.addActionListener(this);
        pnlButtons.add(btnLoad);
        pnlButtons.add(btnSave);

        // Text area - read only
        txtDisplay.setEditable(false);
        txtDisplay.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));
        txtDisplay.setLineWrap(true);
        txtDisplay.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(txtDisplay,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        // Bottom: years processed label
        JPanel pnlBottom = new JPanel(new BorderLayout());
        pnlBottom.setBorder(BorderFactory.createEmptyBorder(8, 12, 12, 12));
        pnlBottom.add(lblYearsProcessed, BorderLayout.WEST);

        // Layout main frame
        Container cp = getContentPane();
        cp.setLayout(new BorderLayout(8, 8));
        cp.add(pnlButtons, BorderLayout.NORTH);
        cp.add(scroll, BorderLayout.CENTER);
        cp.add(pnlBottom, BorderLayout.SOUTH);
    }

    // ActionListener: handle button clicks
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnLoad) {
            loadDataFromFile();
        } else if (e.getSource() == btnSave) {
            saveDataToFile();
        }
    }

    // Display the current 'data' array in the text area (pretty printed)
    private void showDataInTextArea() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s%n%n", "Product Sales Data"));

        // Show year-by-year blocks
        for (int r = 0; r < data.length; r++) {
            sb.append("Year ").append(r + 1).append(":\n");
            for (int c = 0; c < headers.length; c++) {
                sb.append(String.format("%-12s : %d%n", headers[c], data[r][c]));
            }
            int total = 0;
            for (int v : data[r]) total += v;
            sb.append("Total units (that year): ").append(total).append("\n\n");
        }

        // Show a compact table below
        sb.append("Compact table (rows = years):\n");
        // header
        sb.append(String.format("%-8s", "Year"));
        for (String h : headers) sb.append(String.format("%12s", h));
        sb.append("\n");

        for (int r = 0; r < data.length; r++) {
            sb.append(String.format("%-8s", "Year " + (r+1)));
            for (int c = 0; c < headers.length; c++) {
                sb.append(String.format("%12d", data[r][c]));
            }
            sb.append("\n");
        }

        txtDisplay.setText(sb.toString());
        lblYearsProcessed.setText("Years processed: " + data.length);
    }

    // Load data from CSV file. If missing, offer to create a sample file.
    private void loadDataFromFile() {
        if (!Files.exists(dataFile)) {
            int choice = JOptionPane.showConfirmDialog(this,
                    "Data file not found: " + dataFile.toAbsolutePath() + "\nCreate sample file with example data?",
                    "File not found", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                createSampleFile();
            } else {
                // just show current (in-memory) data
                showDataInTextArea();
                return;
            }
        }

        try {
            List<String> lines = Files.readAllLines(dataFile);
            List<int[]> rows = new ArrayList<>();
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                // skip header if it contains non-digit letters
                if (line.toLowerCase().startsWith("microphone") || line.matches(".[a-zA-Z].") && line.contains(",")) {
                    continue;
                }
                String[] parts = line.split(",");
                // allow lines with exactly 3 numeric entries
                if (parts.length >= headers.length) {
                    int[] row = new int[headers.length];
                    boolean ok = true;
                    for (int i = 0; i < headers.length; i++) {
                        try {
                            row[i] = Integer.parseInt(parts[i].trim());
                        } catch (NumberFormatException ex) {
                            ok = false;
                            break;
                        }
                    }
                    if (ok) rows.add(row);
                }
            }

            if (!rows.isEmpty()) {
                data = new int[rows.size()][headers.length];
                for (int r = 0; r < rows.size(); r++) data[r] = rows.get(r);
                JOptionPane.showMessageDialog(this, "Loaded " + data.length + " year(s) from file.");
            } else {
                JOptionPane.showMessageDialog(this, "No valid data rows found in file. Using sample data.");
                // keep default data
            }

            showDataInTextArea();
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error reading file: " + ex.getMessage(), "Read error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Save the current in-memory 'data' array to CSV (with header)
    private void saveDataToFile() {
        List<String> out = new ArrayList<>();
        // header
        out.add(String.join(",", headers));
        for (int r = 0; r < data.length; r++) {
            StringBuilder line = new StringBuilder();
            for (int c = 0; c < headers.length; c++) {
                if (c > 0) line.append(",");
                line.append(data[r][c]);
            }
            out.add(line.toString());
        }

        try {
            Files.write(dataFile, out);
            JOptionPane.showMessageDialog(this, "Saved " + data.length + " year(s) to " + dataFile.toAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + ex.getMessage(), "Save error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Create a sample CSV file (same as default data) then load it
    private void createSampleFile() {
        List<String> sample = new ArrayList<>();
        sample.add(String.join(",", headers));
        for (int r = 0; r < data.length; r++) {
            sample.add(data[r][0] + "," + data[r][1] + "," + data[r][2]);
        }
        try {
            Files.write(dataFile, sample);
            JOptionPane.showMessageDialog(this, "Sample file created at: " + dataFile.toAbsolutePath());
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Could not create sample file: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); }
            catch (Exception ignored) {}
            ProductSalesDataApp app = new ProductSalesDataApp();
            app.setVisible(true);
        });
    }
}