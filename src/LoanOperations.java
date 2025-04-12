import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

public class LoanOperations extends JPanel {
    private JTextField isbnField, borrowerField;
    private JButton borrowButton, extendButton, returnButton, refreshButton;
    private JTable loanTable;
    private DefaultTableModel tableModel;

    public LoanOperations() {
        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(255, 255, 255));

        // Form peminjaman
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Pinjam Buku"));
        formPanel.setBackground(new Color(255, 255, 255));

        isbnField = new JTextField();
        borrowerField = new JTextField();
        borrowButton = new JButton("Pinjam Buku");
        borrowButton.setBackground(new Color(66, 165, 245));
        borrowButton.setForeground(Color.WHITE);

        formPanel.add(new JLabel("ISBN Buku:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("Nama Siswa:"));
        formPanel.add(borrowerField);
        formPanel.add(new JLabel(""));
        formPanel.add(borrowButton);

        // Tabel untuk daftar peminjaman
        String[] columns = {"ID Pinjam", "ISBN", "Siswa", "Tgl Pinjam", "Jatuh Tempo", "Status", "Denda (Rp)", "Status Jatuh Tempo"};
        tableModel = new DefaultTableModel(columns, 0);
        loanTable = new JTable(tableModel);
        loanTable.setRowHeight(25);
        loanTable.getTableHeader().setBackground(new Color(66, 165, 245));
        loanTable.getTableHeader().setForeground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(loanTable);

        // Tombol aksi
        extendButton = new JButton("Perpanjang");
        extendButton.setBackground(new Color(255, 193, 7)); // Kuning
        extendButton.setForeground(Color.WHITE);
        returnButton = new JButton("Kembalikan");
        returnButton.setBackground(new Color(102, 187, 106)); // Hijau
        returnButton.setForeground(Color.WHITE);
        refreshButton = new JButton("Perbarui Daftar");
        refreshButton.setBackground(new Color(66, 165, 245));
        refreshButton.setForeground(Color.WHITE);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(255, 255, 255));
        buttonPanel.add(extendButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(refreshButton);

        // Tambahkan komponen ke panel
        add(formPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listener untuk tombol Pinjam
        borrowButton.addActionListener(e -> {
            try {
                String isbn = isbnField.getText();
                String borrower = borrowerField.getText();

                if (isbn.isEmpty() || borrower.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Harap isi semua kolom!");
                    return;
                }

                LibraryManager manager = new LibraryManager();
                List<Book> books = manager.getAllBooks();
                Book selectedBook = null;
                for (Book book : books) {
                    if (book.getIsbn().equals(isbn) && book.isAvailable()) {
                        selectedBook = book;
                        break;
                    }
                }

                if (selectedBook == null) {
                    JOptionPane.showMessageDialog(this, "Buku tidak ditemukan atau sudah dipinjam!");
                    return;
                }

                String loanId = UUID.randomUUID().toString();
                LocalDate borrowDate = LocalDate.now();
                LocalDate dueDate = borrowDate.plusDays(14);
                Loan loan = new Loan(loanId, isbn, borrower, borrowDate, dueDate);
                manager.addLoan(loan);

                JOptionPane.showMessageDialog(this, "Buku berhasil dipinjam!");
                clearForm();
                refreshTable();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Event listener untuk tombol Perpanjang
        extendButton.addActionListener(e -> {
            int selectedRow = loanTable.getSelectedRow();
            if (selectedRow >= 0) {
                String loanId = (String) tableModel.getValueAt(selectedRow, 0);
                LibraryManager manager = new LibraryManager();
                manager.extendLoan(loanId, 7);
                JOptionPane.showMessageDialog(this, "Peminjaman diperpanjang!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Pilih peminjaman terlebih dahulu!");
            }
        });

        // Event listener untuk tombol Kembalikan
        returnButton.addActionListener(e -> {
            int selectedRow = loanTable.getSelectedRow();
            if (selectedRow >= 0) {
                String loanId = (String) tableModel.getValueAt(selectedRow, 0);
                LibraryManager manager = new LibraryManager();
                manager.returnBook(loanId);
                JOptionPane.showMessageDialog(this, "Buku dikembalikan!");
                refreshTable();
            } else {
                JOptionPane.showMessageDialog(this, "Pilih peminjaman terlebih dahulu!");
            }
        });

        // Event listener untuk tombol Perbarui
        refreshButton.addActionListener(e -> refreshTable());

        // Muat data awal
        refreshTable();
    }

    private void clearForm() {
        isbnField.setText("");
        borrowerField.setText("");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        LibraryManager manager = new LibraryManager();
        List<Loan> loans = manager.getAllLoans();
        for (Loan loan : loans) {
            String dueStatus = getDueStatus(loan);
            tableModel.addRow(new Object[]{
                    loan.getLoanId(),
                    loan.getBookIsbn(),
                    loan.getBorrowerName(),
                    loan.getBorrowDate().toString(),
                    loan.getDueDate().toString(),
                    loan.isReturned() ? "Dikembalikan" : "Dipinjam",
                    loan.getFine(),
                    dueStatus
            });
        }
    }

    private String getDueStatus(Loan loan) {
        if (loan.isReturned()) {
            return "Sudah Dikembalikan";
        }
        LocalDate today = LocalDate.now();
        long daysToDue = ChronoUnit.DAYS.between(today, loan.getDueDate());
        if (today.isAfter(loan.getDueDate())) {
            return "Terlambat!";
        } else if (daysToDue <= 2) {
            return "Segera Kembali!";
        } else {
            return "Belum Jatuh Tempo";
        }
    }
}