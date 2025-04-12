import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

public class PDFViewer extends JFrame {
    private JLabel pdfLabel;
    private JScrollPane scrollPane;
    private PDDocument document;
    private PDFRenderer pdfRenderer;
    private int currentPage = 0;
    private int totalPages;
    private String studentName;
    private String bookIsbn;

    public PDFViewer(String filePath, String studentName, String bookIsbn) throws Exception {
        this.studentName = studentName;
        this.bookIsbn = bookIsbn;

        setTitle("PDF Viewer");
        setSize(600, 800);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load PDF
        document = PDDocument.load(new File(filePath));
        pdfRenderer = new PDFRenderer(document);
        totalPages = document.getNumberOfPages();

        // Tampilan PDF
        pdfLabel = new JLabel();
        scrollPane = new JScrollPane(pdfLabel);
        add(scrollPane, BorderLayout.CENTER);

        // Tombol navigasi
        JButton prevButton = new JButton("Previous");
        prevButton.setBackground(new Color(66, 165, 245));
        prevButton.setForeground(Color.WHITE);
        JButton nextButton = new JButton("Next");
        nextButton.setBackground(new Color(66, 165, 245));
        nextButton.setForeground(Color.WHITE);
        JLabel pageLabel = new JLabel("Page: 1 / " + totalPages);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.add(prevButton);
        buttonPanel.add(pageLabel);
        buttonPanel.add(nextButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listener untuk tombol navigasi
        prevButton.addActionListener(e -> {
            if (currentPage > 0) {
                currentPage--;
                displayPage();
                pageLabel.setText("Page: " + (currentPage + 1) + " / " + totalPages);
            }
        });

        nextButton.addActionListener(e -> {
            if (currentPage < totalPages - 1) {
                currentPage++;
                displayPage();
                pageLabel.setText("Page: " + (currentPage + 1) + " / " + totalPages);
            }
        });

        // Tampilkan halaman pertama
        displayPage();

        // Rekam riwayat baca saat PDF dibuka
        LibraryManager manager = new LibraryManager();
        manager.recordReading(studentName, bookIsbn);
    }

    private void displayPage() {
        try {
            BufferedImage image = pdfRenderer.renderImageWithDPI(currentPage, 100);
            pdfLabel.setIcon(new ImageIcon(image));
            scrollPane.revalidate();
            scrollPane.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menampilkan halaman: " + e.getMessage());
        }
    }

    @Override
    public void dispose() {
        try {
            if (document != null) {
                document.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.dispose();
    }
}