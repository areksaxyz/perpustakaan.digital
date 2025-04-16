import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class BookOperations extends JPanel {
    private JTextField isbnField, titleField, authorField, yearField, subjectField, filePathField;
    private JComboBox<String> typeComboBox;
    private JTextField searchField;
    private JComboBox<String> searchByComboBox;
    private JButton addButton, searchButton, refreshButton, readButton;
    private JTable bookTable;
    private DefaultTableModel tableModel;

    public BookOperations() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // Form input
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createTitledBorder("Tambah Buku"));
        formPanel.setBackground(Color.WHITE);

        isbnField = new JTextField();
        titleField = new JTextField();
        authorField = new JTextField();
        yearField = new JTextField();
        subjectField = new JTextField();
        typeComboBox = new JComboBox<>(new String[]{"Digital", "Fisik"});
        filePathField = new JTextField();
        addButton = new JButton("Tambah Buku");
        addButton.setBackground(new Color(0, 123, 255));
        addButton.setForeground(Color.WHITE);
        addButton.setFont(new Font("SansSerif", Font.PLAIN, 12));

        formPanel.add(new JLabel("ISBN:"));
        formPanel.add(isbnField);
        formPanel.add(new JLabel("Judul:"));
        formPanel.add(titleField);
        formPanel.add(new JLabel("Penulis:"));
        formPanel.add(authorField);
        formPanel.add(new JLabel("Tahun:"));
        formPanel.add(yearField);
        formPanel.add(new JLabel("Subjek:"));
        formPanel.add(subjectField);
        formPanel.add(new JLabel("Tipe:"));
        formPanel.add(typeComboBox);
        formPanel.add(new JLabel("Path File (untuk Digital):"));
        formPanel.add(filePathField);
        formPanel.add(new JLabel(""));
        formPanel.add(addButton);

        // Form pencarian
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchField = new JTextField(20);
        searchByComboBox = new JComboBox<>(new String[]{"Judul", "Penulis", "Subjek"});
        searchButton = new JButton("Cari");
        searchButton.setBackground(new Color(0, 123, 255));
        searchButton.setForeground(Color.WHITE);
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        searchPanel.add(new JLabel("Cari:"));
        searchPanel.add(searchField);
        searchPanel.add(searchByComboBox);
        searchPanel.add(searchButton);

        // Tabel untuk daftar buku
        String[] columns = {"ISBN", "Judul", "Penulis", "Tahun", "Subjek", "Tipe", "Path File", "Tersedia"};
        tableModel = new DefaultTableModel(columns, 0);
        bookTable = new JTable(tableModel);
        bookTable.setRowHeight(25);
        bookTable.getTableHeader().setBackground(new Color(0, 123, 255));
        bookTable.getTableHeader().setForeground(Color.WHITE);
        bookTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(bookTable);

        // Tombol aksi
        readButton = new JButton("Baca");
        readButton.setBackground(new Color(0, 123, 255));
        readButton.setForeground(Color.WHITE);
        readButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        refreshButton = new JButton("Perbarui Daftar");
        refreshButton.setBackground(new Color(0, 123, 255));
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.add(readButton);
        buttonPanel.add(refreshButton);

        // Tambahkan komponen ke panel
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(formPanel, BorderLayout.NORTH);
        topPanel.add(searchPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event listener untuk tombol Tambah
        addButton.addActionListener(e -> {
            try {
                String isbn = isbnField.getText();
                String title = titleField.getText();
                String author = authorField.getText();
                int year = Integer.parseInt(yearField.getText());
                String subject = subjectField.getText();
                String type = (String) typeComboBox.getSelectedItem();
                String filePath = filePathField.getText();

                if (isbn.isEmpty() || title.isEmpty() || author.isEmpty() || subject.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Harap isi semua kolom wajib!");
                    return;
                }
                if (type.equals("Digital") && (filePath.isEmpty() || !new File(filePath).exists())) {
                    JOptionPane.showMessageDialog(this, "File PDF untuk buku digital tidak ditemukan!");
                    return;
                }
                if (type.equals("Fisik")) {
                    filePath = null;
                }

                Book book = new Book(isbn, title, author, year, subject, type, filePath);
                LibraryManager manager = new LibraryManager();
                manager.addBook(book);

                JOptionPane.showMessageDialog(this, "Buku berhasil ditambahkan!");
                clearForm();
                refreshTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Tahun harus angka!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        // Event listener untuk tombol Cari
        searchButton.addActionListener(e -> {
            String query = searchField.getText();
            String field = (String) searchByComboBox.getSelectedItem();
            if (query.isEmpty()) {
                refreshTable();
            } else {
                searchBooks(query, field);
            }
        });

        // Event listener untuk tombol Baca
        readButton.addActionListener(e -> {
            int selectedRow = bookTable.getSelectedRow();
            if (selectedRow >= 0) {
                String isbn = (String) tableModel.getValueAt(selectedRow, 0);
                String type = (String) tableModel.getValueAt(selectedRow, 5);
                String filePath = (String) tableModel.getValueAt(selectedRow, 6);
                String available = (String) tableModel.getValueAt(selectedRow, 7);

                if (!available.equals("Ya")) {
                    JOptionPane.showMessageDialog(this, "Buku sedang dipinjam!");
                    return;
                }
                if (!type.equals("Digital")) {
                    JOptionPane.showMessageDialog(this, "Hanya buku digital yang dapat dibaca!");
                    return;
                }
                if (filePath == null || filePath.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "File PDF tidak ditemukan untuk buku ini!");
                    return;
                }

                String studentName = JOptionPane.showInputDialog(this, "Masukkan nama siswa:");
                if (studentName == null || studentName.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama siswa tidak boleh kosong!");
                    return;
                }

                try {
                    PDFViewer viewer = new PDFViewer(filePath, studentName, isbn);
                    viewer.setVisible(true);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Gagal membuka PDF: " + ex.getMessage());
                }
            } else {
                JOptionPane.showMessageDialog(this, "Pilih buku terlebih dahulu!");
            }
        });

        // Event listener untuk tombol Perbarui
        refreshButton.addActionListener(e -> refreshTable());

        // Muat data awal
        refreshTable();
    }

    private void clearForm() {
        isbnField.setText("");
        titleField.setText("");
        authorField.setText("");
        yearField.setText("");
        subjectField.setText("");
        typeComboBox.setSelectedIndex(0);
        filePathField.setText("");
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        LibraryManager manager = new LibraryManager();
        List<Book> books = manager.getAllBooks();
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getSubject(),
                    book.getType(),
                    book.getFilePath(),
                    book.isAvailable() ? "Ya" : "Tidak"
            });
        }
    }

    private void searchBooks(String query, String field) {
        tableModel.setRowCount(0);
        LibraryManager manager = new LibraryManager();
        List<Book> books = manager.searchBooks(query, field.toLowerCase());
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Buku tidak ditemukan!", "Info", JOptionPane.INFORMATION_MESSAGE);
        }
        for (Book book : books) {
            tableModel.addRow(new Object[]{
                    book.getIsbn(),
                    book.getTitle(),
                    book.getAuthor(),
                    book.getYear(),
                    book.getSubject(),
                    book.getType(),
                    book.getFilePath(),
                    book.isAvailable() ? "Ya" : "Tidak"
            });
        }
    }
}
