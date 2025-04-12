import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

public class StatisticsPanel extends JPanel {
    private JTextField studentField;
    private JButton searchButton;
    private JTextArea historyArea;
    private JLabel popularBooksLabel;
    private JLabel activeBorrowersLabel;

    public StatisticsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 255, 255));

        // Panel untuk riwayat siswa
        JPanel studentPanel = new JPanel(new BorderLayout(10, 10));
        studentPanel.setBorder(BorderFactory.createTitledBorder("Riwayat Siswa"));
        studentPanel.setBackground(new Color(255, 255, 255));

        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        inputPanel.setBackground(new Color(255, 255, 255));
        studentField = new JTextField(20);
        searchButton = new JButton("Cari Riwayat");
        searchButton.setBackground(new Color(66, 165, 245));
        searchButton.setForeground(Color.WHITE);
        inputPanel.add(new JLabel("Nama Siswa:"));
        inputPanel.add(studentField);
        inputPanel.add(searchButton);

        historyArea = new JTextArea(10, 50);
        historyArea.setEditable(false);
        historyArea.setFont(new Font("Roboto", Font.PLAIN, 14));
        JScrollPane historyScroll = new JScrollPane(historyArea);

        studentPanel.add(inputPanel, BorderLayout.NORTH);
        studentPanel.add(historyScroll, BorderLayout.CENTER);

        // Panel untuk statistik
        JPanel statsPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Statistik"));
        statsPanel.setBackground(new Color(255, 255, 255));

        popularBooksLabel = new JLabel("<html><b>Buku Populer:</b><br>Belum ada data</html>");
        popularBooksLabel.setFont(new Font("Roboto", Font.PLAIN, 14));
        activeBorrowersLabel = new JLabel("<html><b>Peminjam Aktif:</b><br>Belum ada data</html>");
        activeBorrowersLabel.setFont(new Font("Roboto", Font.PLAIN, 14));

        statsPanel.add(popularBooksLabel);
        statsPanel.add(activeBorrowersLabel);

        // Tambahkan ke panel utama
        add(studentPanel, BorderLayout.CENTER);
        add(statsPanel, BorderLayout.SOUTH);

        // Event listener untuk tombol Cari
        searchButton.addActionListener(e -> {
            String studentName = studentField.getText().trim();
            if (studentName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan nama siswa!");
                return;
            }

            LibraryManager manager = new LibraryManager();
            List<String> history = manager.getStudentHistory(studentName);
            if (history.isEmpty()) {
                historyArea.setText("Tidak ada riwayat untuk siswa: " + studentName);
            } else {
                StringBuilder sb = new StringBuilder();
                for (String entry : history) {
                    sb.append(entry).append("\n");
                }
                historyArea.setText(sb.toString());
            }
        });

        // Muat statistik awal
        refreshStatistics();
    }

    private void refreshStatistics() {
        LibraryManager manager = new LibraryManager();

        // Buku populer
        List<Map.Entry<String, Integer>> popularBooks = manager.getPopularBooks();
        StringBuilder booksText = new StringBuilder("<html><b>Buku Populer:</b><br>");
        if (popularBooks.isEmpty()) {
            booksText.append("Belum ada data");
        } else {
            for (Map.Entry<String, Integer> entry : popularBooks) {
                String title = manager.getBookTitleByIsbn(entry.getKey());
                booksText.append(title).append(": ").append(entry.getValue()).append(" kali<br>");
            }
        }
        booksText.append("</html>");
        popularBooksLabel.setText(booksText.toString());

        // Peminjam aktif
        List<Map.Entry<String, Integer>> activeBorrowers = manager.getActiveBorrowers();
        StringBuilder borrowersText = new StringBuilder("<html><b>Peminjam Aktif:</b><br>");
        if (activeBorrowers.isEmpty()) {
            borrowersText.append("Belum ada data");
        } else {
            for (Map.Entry<String, Integer> entry : activeBorrowers) {
                borrowersText.append(entry.getKey()).append(": ").append(entry.getValue()).append(" peminjaman<br>");
            }
        }
        borrowersText.append("</html>");
        activeBorrowersLabel.setText(borrowersText.toString());
    }
}