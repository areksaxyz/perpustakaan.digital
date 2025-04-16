import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class StatisticsPanel extends JPanel {
    private LibraryManager libraryManager;
    private DefaultTableModel popularBooksModel;
    private DefaultTableModel activeBorrowersModel;
    private DefaultTableModel studentHistoryModel;
    private String currentUser = "Muhammad Arga Reksapati";
    private JLabel popularBooksEmptyLabel;
    private JLabel activeBorrowersEmptyLabel;
    private JLabel studentHistoryEmptyLabel;

    public StatisticsPanel(LibraryManager manager) {
        this.libraryManager = manager;
        setLayout(new BorderLayout());
        setBackground(new Color(240, 242, 245));
        initializeComponents();
        updateStatistics();
    }

    private void initializeComponents() {
        JLabel headerLabel = new JLabel("Riwayat Baca dan Statistik");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(new Color(25, 118, 210));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(new Color(240, 242, 245));

        JPanel popularBooksPanel = createPopularBooksPanel();
        tabbedPane.addTab("Buku Populer", popularBooksPanel);

        JPanel activeBorrowersPanel = createActiveBorrowersPanel();
        tabbedPane.addTab("Peminjam Aktif", activeBorrowersPanel);

        JPanel studentHistoryPanel = createStudentHistoryPanel();
        tabbedPane.addTab("Rekam Jejak Siswa", studentHistoryPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    private JPanel createPopularBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));

        String[] columns = {"Judul Buku", "Kategori", "Jumlah Peminjaman"};
        popularBooksModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable popularBooksTable = new JTable(popularBooksModel);
        styleTable(popularBooksTable);

        JScrollPane scrollPane = new JScrollPane(popularBooksTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        popularBooksEmptyLabel = new JLabel("Belum ada buku yang dipinjam.", SwingConstants.CENTER);
        popularBooksEmptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        popularBooksEmptyLabel.setForeground(Color.GRAY);
        panel.add(popularBooksEmptyLabel, BorderLayout.SOUTH);
        popularBooksEmptyLabel.setVisible(popularBooksModel.getRowCount() == 0);

        return panel;
    }

    private JPanel createActiveBorrowersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));

        String[] columns = {"Nama Peminjam", "Jumlah Peminjaman"};
        activeBorrowersModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable activeBorrowersTable = new JTable(activeBorrowersModel);
        styleTable(activeBorrowersTable);

        JScrollPane scrollPane = new JScrollPane(activeBorrowersTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        activeBorrowersEmptyLabel = new JLabel("Belum ada peminjam aktif.", SwingConstants.CENTER);
        activeBorrowersEmptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        activeBorrowersEmptyLabel.setForeground(Color.GRAY);
        panel.add(activeBorrowersEmptyLabel, BorderLayout.SOUTH);
        activeBorrowersEmptyLabel.setVisible(activeBorrowersModel.getRowCount() == 0);

        return panel;
    }

    private JPanel createStudentHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(240, 242, 245));

        String[] columns = {"ID Peminjaman", "Judul Buku", "NIM", "Kelas", "Tanggal Pinjam", "Tanggal Kembali", "Status", "Denda (Rp)"};
        studentHistoryModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable studentHistoryTable = new JTable(studentHistoryModel);
        styleTable(studentHistoryTable);

        JScrollPane scrollPane = new JScrollPane(studentHistoryTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        studentHistoryEmptyLabel = new JLabel("Belum ada riwayat peminjaman untuk " + currentUser + ".", SwingConstants.CENTER);
        studentHistoryEmptyLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        studentHistoryEmptyLabel.setForeground(Color.GRAY);
        panel.add(studentHistoryEmptyLabel, BorderLayout.SOUTH);
        studentHistoryEmptyLabel.setVisible(studentHistoryModel.getRowCount() == 0);

        return panel;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(35);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setBackground(Color.WHITE);
        table.setForeground(new Color(33, 33, 33));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(25, 118, 210));
        table.getTableHeader().setForeground(Color.WHITE);
        table.setGridColor(new Color(200, 200, 200));
        table.setSelectionBackground(new Color(25, 118, 210));
        table.setSelectionForeground(Color.WHITE);

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setPreferredWidth(150);
        }
    }

    public void updateStatistics() {
        updatePopularBooks();
        updateActiveBorrowers();
        updateStudentHistory();
    }

    private void updatePopularBooks() {
        popularBooksModel.setRowCount(0);

        Map<String, Long> bookLoanCounts = libraryManager.getAllLoans().stream()
                .collect(Collectors.groupingBy(Loan::getBookIsbn, Collectors.counting()));

        List<Map.Entry<String, Long>> sortedBooks = bookLoanCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : sortedBooks) {
            String isbn = entry.getKey();
            long loanCount = entry.getValue();
            Book book = libraryManager.getBookByIsbn(isbn);
            if (book != null) {
                popularBooksModel.addRow(new Object[]{
                        book.getTitle(),
                        book.getSubject(),
                        loanCount
                });
            }
        }

        popularBooksEmptyLabel.setVisible(popularBooksModel.getRowCount() == 0);
    }

    private void updateActiveBorrowers() {
        activeBorrowersModel.setRowCount(0);

        Map<String, Long> borrowerLoanCounts = libraryManager.getAllLoans().stream()
                .collect(Collectors.groupingBy(Loan::getBorrowerName, Collectors.counting()));

        List<Map.Entry<String, Long>> sortedBorrowers = borrowerLoanCounts.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : sortedBorrowers) {
            activeBorrowersModel.addRow(new Object[]{
                    entry.getKey(),
                    entry.getValue()
            });
        }

        activeBorrowersEmptyLabel.setVisible(activeBorrowersModel.getRowCount() == 0);
    }

    private void updateStudentHistory() {
        studentHistoryModel.setRowCount(0);

        List<Loan> userLoans = libraryManager.getAllLoans().stream()
                .filter(loan -> loan.getBorrowerName().equals(currentUser))
                .collect(Collectors.toList());

        for (Loan loan : userLoans) {
            String bookTitle = libraryManager.getBookTitleByIsbn(loan.getBookIsbn());
            String returnDate = loan.isReturned() ? (loan.getReturnDate() != null ? loan.getReturnDate().toString() : "Tidak Dicatat") : "Belum Dikembalikan";
            String status = loan.isReturned() ? "Dikembalikan" : "Dipinjam";
            String fine = loan.isFinePaid() || loan.getFine() == 0 ? "Lunas" : formatRupiah(loan.getFine());
            studentHistoryModel.addRow(new Object[]{
                    loan.getLoanId(),
                    bookTitle,
                    loan.getNim(),
                    loan.getBorrowerClass(),
                    loan.getBorrowDate().toString(),
                    returnDate,
                    status,
                    fine
            });
        }

        studentHistoryEmptyLabel.setVisible(studentHistoryModel.getRowCount() == 0);
    }

    private String formatRupiah(double amount) {
        java.text.NumberFormat formatter = java.text.NumberFormat.getCurrencyInstance(new java.util.Locale("id", "ID"));
        String formatted = formatter.format(amount);
        return formatted.replace("Rp", "Rp ").trim();
    }
}
