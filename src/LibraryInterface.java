import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import javax.swing.text.PlainDocument;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import java.util.HashMap;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class LibraryInterface extends JFrame {
    private JTabbedPane tabbedPane;
    private LibraryManager libraryManager;
    private StatisticsPanel statisticsPanel;
    private HashMap<String, String> bookUrlMap = new HashMap<>();
    private JLabel reminderEmptyLabel;
    private JLabel fineEmptyLabel;
    private JLabel totalFineLabel;
    private DefaultTableModel reminderModel;
    private DefaultTableModel fineModel;
    private DefaultTableModel bookTableModel; // Model tabel untuk buku (digunakan di updateBookTable)
    private DefaultTableModel catalogTableModel; // Model tabel untuk katalog (digunakan di updateCatalogPanel)
    private DefaultTableModel fineTableModel;

    // Tambahkan variabel instance untuk field pencarian
    private JTextField titleField;
    private JTextField authorField;
    private JTextField subjectField;

    private String formatRupiah(double amount) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        String formatted = formatter.format(amount);
        // NumberFormat akan menghasilkan "Rp10.000,00", kita pastikan ada spasi setelah "Rp"
        return formatted.replace("Rp", "Rp ").trim();
    }


    public LibraryInterface() {
        libraryManager = new LibraryManager();
        statisticsPanel = new StatisticsPanel(libraryManager);

        // Inisialisasi semua buku
        addBookIfNotExists("978-3-16-148410-0", new Book("Algoritma Pemrograman", "Penulis Algoritma", "Penerbit Algoritma", 2020, "978-3-16-148410-0", "Algoritma", null));
        addBookIfNotExists("978-3-16-148411-7", new Book("Pemrograman Java", "Penulis Java", "Penerbit Java", 2019, "978-3-16-148411-7", "Pemrograman", null));
        addBookIfNotExists("978-3-16-148412-4", new Book("Dasar Dasar Algoritma", "Penulis Dasar Algoritma", "Penerbit Dasar Algoritma", 2021, "978-3-16-148412-4", "Algoritma", null));
        addBookIfNotExists("978-3-16-148413-1", new Book("Pemrograman Python", "Penulis Python", "Penerbit Python", 2022, "978-3-16-148413-1", "Pemrograman", null));
        addBookIfNotExists("978-3-16-148414-8", new Book("Java script", "Penulis JavaScript", "Penerbit JavaScript", 2018, "978-3-16-148414-8", "Pemrograman", null));
        addBookIfNotExists("978-3-16-148415-5", new Book("Matematika Diskrit", "Penulis Matematika", "Penerbit Matematika", 2015, "978-3-16-148415-5", "Matematika", null));
        addBookIfNotExists("978-3-16-148416-2", new Book("Literasi Digital", "Penulis Literasi", "Penerbit Literasi", 2023, "978-3-16-148416-2", "Literasi", null));
        addBookIfNotExists("978-3-16-148417-9", new Book("Bahasa Inggris", "Penulis Bahasa Inggris", "Penerbit Bahasa Inggris", 2017, "978-3-16-148417-9", "Bahasa", null));
        addBookIfNotExists("978-3-16-148418-6", new Book("Kewarganegaraan", "Penulis Kewarganegaraan", "Penerbit Kewarganegaraan", 2016, "978-3-16-148418-6", "Kewarganegaraan", null));

        // Inisialisasi bookUrlMap untuk semua buku digital
        bookUrlMap = new HashMap<>();
        bookUrlMap.put("Algoritma Pemrograman", "http://eprints.umsida.ac.id/9873/5/BE1-ALPO-BukuAjar.pdf");
        bookUrlMap.put("Pemrograman Java", "https://digilib.stekom.ac.id/assets/dokumen/ebook/feb_BMuBPtvpXwUkhZqdyUPA7LyV7948c7ZdhjGj8z2EkAjSpNgD_njQSpM_1656322622.pdf");
        bookUrlMap.put("Dasar Dasar Algoritma", "URL Tidak Tersedia"); // Placeholder URL
        bookUrlMap.put("Pemrograman Python", "https://repository.unikom.ac.id/65984/1/E-Book_Belajar_Pemrograman_Python_Dasar.pdf");
        bookUrlMap.put("Java script", "https://rahmatfauzi.com/wp-content/uploads/2019/12/W3-JavaScript.pdf");

        // Debugging: Cetak isi bookUrlMap untuk memastikan semua buku digital terdaftar
        System.out.println("Isi bookUrlMap:");
        for (Map.Entry<String, String> entry : bookUrlMap.entrySet()) {
            System.out.println("Judul: " + entry.getKey() + ", URL: " + entry.getValue());
        }

        // Lanjutkan inisialisasi UI seperti sebelumnya...
        setTitle("Perpustakaan Digital");
        setSize(1000, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Warna tema profesional
        Color primaryColor = new Color(25, 118, 210);
        Color accentColor = new Color(229, 57, 53);
        Color backgroundColor = new Color(240, 242, 245);
        Color cardColor = Color.WHITE;
        Color textColor = new Color(33, 33, 33);

        // Font modern
        Font titleFont = new Font("Segoe UI", Font.BOLD, 24);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Sidebar
        JPanel sidebarPanel = createSidebar(primaryColor, buttonFont);
        add(sidebarPanel, BorderLayout.WEST);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(cardColor);

        JLabel appLabel = new JLabel("Perpustakaan Digital");
        appLabel.setFont(titleFont);
        appLabel.setForeground(primaryColor);
        titlePanel.add(appLabel);

        JLabel userLabel = new JLabel("Muhammad Arga Reksapati");
        userLabel.setFont(fieldFont);
        userLabel.setForeground(textColor);
        titlePanel.add(userLabel);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        JButton actionButton = createStyledButton("Mulai Presensi", primaryColor, buttonFont, null);
        headerPanel.add(actionButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tabbedPane.setBackground(backgroundColor);
        tabbedPane.addTab("Beranda", createHomePanel(backgroundColor, cardColor, primaryColor, textColor, titleFont, fieldFont));
        tabbedPane.addTab("Katalog Buku", new ImageIcon("icons/catalog.png"), createCatalogPanel(backgroundColor, cardColor, primaryColor, accentColor, textColor, labelFont, fieldFont, tableFont));
        tabbedPane.addTab("Buku", new ImageIcon("icons/book.png"), createBookPanel(backgroundColor, cardColor, primaryColor, accentColor, textColor, labelFont, fieldFont, tableFont));
        tabbedPane.addTab("Peminjaman", new ImageIcon("icons/loan.png"), createLoanPanel(backgroundColor, cardColor, primaryColor, accentColor, textColor, labelFont, fieldFont, tableFont));
        tabbedPane.addTab("Riwayat Baca dan Statistik", new ImageIcon("icons/stats.png"), statisticsPanel = new StatisticsPanel(libraryManager));
        tabbedPane.addTab("Manajemen Denda dan Peringatan", new ImageIcon("icons/fine.png"), createFineManagementPanel(backgroundColor, cardColor, primaryColor, accentColor, textColor, labelFont, fieldFont, tableFont));
        mainPanel.add(tabbedPane, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Panggil checkAutomaticReturns dan update tabel setelah semua tab dibuat
        checkAutomaticReturns();
        updateBookTable();
        updateCatalogPanel();
        updateLoanPanel();
        updateFineManagementPanel();
        setVisible(true);
    }

    // Metode bantu untuk menambahkan buku hanya jika ISBN belum ada
    private void addBookIfNotExists(String isbn, Book book) {
        boolean exists = false;
        for (Book existingBook : libraryManager.getAllBooks()) {
            if (existingBook.getIsbn().equals(isbn)) {
                exists = true;
                break;
            }
        }
        if (!exists) {
            libraryManager.addBook(book);
        }
    }

    private JPanel createSidebar(Color primaryColor, Font buttonFont) {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        sidebarPanel.setBackground(new Color(240, 240, 240));
        sidebarPanel.setPreferredSize(new Dimension(200, getHeight()));
        sidebarPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel menuLabel = new JLabel("MENU UTAMA");
        menuLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        menuLabel.setForeground(primaryColor);
        menuLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebarPanel.add(menuLabel);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JButton berandaButton = createSidebarButton("Beranda", buttonFont);
        JButton catalogButton = createSidebarButton("Katalog Buku", buttonFont);
        JButton bukuButton = createSidebarButton("Buku", buttonFont);
        JButton peminjamanButton = createSidebarButton("Peminjaman", buttonFont);
        JButton historyStatsButton = createSidebarButton("Riwayat & Statistik", buttonFont);
        JButton fineManagementButton = createSidebarButton("Manajemen Denda", buttonFont);

        berandaButton.addActionListener(e -> tabbedPane.setSelectedIndex(0));
        catalogButton.addActionListener(e -> tabbedPane.setSelectedIndex(1));
        bukuButton.addActionListener(e -> tabbedPane.setSelectedIndex(2));
        peminjamanButton.addActionListener(e -> tabbedPane.setSelectedIndex(3));
        historyStatsButton.addActionListener(e -> tabbedPane.setSelectedIndex(4));
        fineManagementButton.addActionListener(e -> tabbedPane.setSelectedIndex(5));

        sidebarPanel.add(berandaButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(catalogButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(bukuButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(peminjamanButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(historyStatsButton);
        sidebarPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidebarPanel.add(fineManagementButton);

        return sidebarPanel;
    }

    private JButton createSidebarButton(String text, Font buttonFont) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(Color.BLACK);
        button.setBackground(Color.WHITE);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 40));
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setFocusPainted(false);
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(200, 200, 200));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
            }
        });
        return button;
    }

    private JButton createStyledButton(String text, Color backgroundColor, Font font, Dimension size) {
        JButton button = new JButton(text);
        button.setFont(font);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        if (size != null) {
            button.setPreferredSize(size);
        }
        return button;
    }

    private JPanel createHomePanel(Color backgroundColor, Color cardColor, Color primaryColor, Color textColor, Font titleFont, Font fieldFont) {
        JPanel homePanel = new JPanel(new BorderLayout());
        homePanel.setBackground(backgroundColor);
        homePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setBackground(cardColor);
        textPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel welcomeLabel = new JLabel("Selamat Datang di Perpustakaan Digital");
        welcomeLabel.setFont(titleFont);
        welcomeLabel.setForeground(primaryColor);
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(welcomeLabel);

        JLabel quoteLabel = new JLabel("Buku adalah jendela dunia.");
        quoteLabel.setFont(fieldFont);
        quoteLabel.setForeground(textColor);
        quoteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        textPanel.add(quoteLabel);

        homePanel.add(textPanel, BorderLayout.NORTH);

        JPanel illustrationsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        illustrationsPanel.setBackground(backgroundColor);
        illustrationsPanel.setBorder(BorderFactory.createEmptyBorder(10, 50, 10, 50));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(cardColor);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel illustrationLabel1 = new JLabel();
        try {
            ImageIcon icon1 = new ImageIcon("illustrations/illustration.png");
            Image image1 = icon1.getImage().getScaledInstance(350, 280, Image.SCALE_SMOOTH);
            illustrationLabel1.setIcon(new ImageIcon(image1));
            illustrationLabel1.setHorizontalAlignment(SwingConstants.CENTER);
            illustrationLabel1.setVerticalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            illustrationLabel1.setText("Ilustrasi 1 tidak ditemukan: " + e.getMessage());
        }
        leftPanel.add(illustrationLabel1);

        JLabel quoteLeft = new JLabel("<html><center>Membaca adalah kunci<br>untuk membuka pintu pengetahuan.</center></html>");
        quoteLeft.setFont(new Font("Georgia", Font.ITALIC, 15));
        quoteLeft.setForeground(primaryColor);
        quoteLeft.setAlignmentX(Component.CENTER_ALIGNMENT);
        quoteLeft.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        quoteLeft.setHorizontalAlignment(SwingConstants.CENTER);
        quoteLeft.setPreferredSize(new Dimension(350, 60));
        leftPanel.add(quoteLeft);

        illustrationsPanel.add(leftPanel);

        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.setBackground(cardColor);
        rightPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel illustrationLabel2 = new JLabel();
        try {
            ImageIcon icon2 = new ImageIcon("illustrations/illustration2.png");
            Image image2 = icon2.getImage().getScaledInstance(350, 280, Image.SCALE_SMOOTH);
            illustrationLabel2.setIcon(new ImageIcon(image2));
            illustrationLabel2.setHorizontalAlignment(SwingConstants.CENTER);
            illustrationLabel2.setVerticalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            illustrationLabel2.setText("Ilustrasi 2 tidak ditemukan: " + e.getMessage());
        }
        rightPanel.add(illustrationLabel2);

        JLabel quoteRight = new JLabel("<html><center>Setiap buku adalah<br>petualangan baru yang menanti.</center></html>");
        quoteRight.setFont(new Font("Georgia", Font.ITALIC, 15));
        quoteRight.setForeground(primaryColor);
        quoteRight.setAlignmentX(Component.CENTER_ALIGNMENT);
        quoteRight.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        quoteRight.setHorizontalAlignment(SwingConstants.CENTER);
        quoteRight.setPreferredSize(new Dimension(350, 60));
        rightPanel.add(quoteRight);

        illustrationsPanel.add(rightPanel);

        homePanel.add(illustrationsPanel, BorderLayout.CENTER);

        return homePanel;
    }

    private JPanel createCatalogPanel(Color backgroundColor, Color cardColor, Color primaryColor, Color accentColor, Color textColor, Font labelFont, Font fieldFont, Font tableFont) {
        JPanel catalogPanel = new JPanel(new BorderLayout());
        catalogPanel.setBackground(backgroundColor);
        catalogPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        JLabel headerLabel = new JLabel("Katalog Buku");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(primaryColor);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        catalogPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel Pencarian
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(cardColor);
        searchPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primaryColor), "Pencarian Buku", 0, 0, labelFont, primaryColor)));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Judul:");
        titleLabel.setFont(labelFont);
        titleLabel.setForeground(textColor);
        titleField = new JTextField();
        titleField.setFont(fieldFont);
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel authorLabel = new JLabel("Penulis:");
        authorLabel.setFont(labelFont);
        authorLabel.setForeground(textColor);
        authorField = new JTextField();
        authorField.setFont(fieldFont);
        authorField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JLabel subjectLabel = new JLabel("Subjek:");
        subjectLabel.setFont(labelFont);
        subjectLabel.setForeground(textColor);
        subjectField = new JTextField();
        subjectField.setFont(fieldFont);
        subjectField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JButton searchButton = createStyledButton("Cari", primaryColor, labelFont, null);
        JButton resetButton = createStyledButton("Reset", accentColor, labelFont, null);

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;
        searchPanel.add(titleLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        searchPanel.add(titleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        searchPanel.add(authorLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        searchPanel.add(authorField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        searchPanel.add(subjectLabel, gbc);

        gbc.gridx = 1;
        gbc.weightx = 1;
        searchPanel.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        searchPanel.add(searchButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        searchPanel.add(resetButton, gbc);

        catalogPanel.add(searchPanel, BorderLayout.NORTH);

        // Tabbed pane untuk memisahkan buku fisik dan digital
        JTabbedPane catalogTabbedPane = new JTabbedPane();
        catalogTabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        catalogTabbedPane.setBackground(backgroundColor);

        // Tabel untuk buku digital
        String[] columnNames = {"Judul", "Penulis", "Tahun Terbit", "Tipe", "Status", "Aksi"};
        DefaultTableModel digitalTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable digitalCatalogTable = new JTable(digitalTableModel);
        digitalCatalogTable.setRowHeight(35);
        digitalCatalogTable.setFont(tableFont);
        digitalCatalogTable.setBackground(Color.WHITE);
        digitalCatalogTable.setForeground(textColor);
        digitalCatalogTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        digitalCatalogTable.getTableHeader().setBackground(primaryColor);
        digitalCatalogTable.getTableHeader().setForeground(Color.WHITE);
        digitalCatalogTable.setGridColor(new Color(200, 200, 200));
        digitalCatalogTable.setSelectionBackground(primaryColor);
        digitalCatalogTable.setSelectionForeground(Color.WHITE);

        digitalCatalogTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        digitalCatalogTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        digitalCatalogTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        digitalCatalogTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        digitalCatalogTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        digitalCatalogTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        digitalCatalogTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer("Baca", primaryColor));
        digitalCatalogTable.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), "Baca", primaryColor));

        JScrollPane digitalScrollPane = new JScrollPane(digitalCatalogTable);
        digitalScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel digitalTablePanel = new JPanel(new BorderLayout());
        digitalTablePanel.setBackground(cardColor);
        digitalTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        digitalTablePanel.add(digitalScrollPane, BorderLayout.CENTER);

        // Tabel untuk buku fisik
        DefaultTableModel physicalTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable physicalCatalogTable = new JTable(physicalTableModel);
        physicalCatalogTable.setRowHeight(35);
        physicalCatalogTable.setFont(tableFont);
        physicalCatalogTable.setBackground(Color.WHITE);
        physicalCatalogTable.setForeground(textColor);
        physicalCatalogTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        physicalCatalogTable.getTableHeader().setBackground(primaryColor);
        physicalCatalogTable.getTableHeader().setForeground(Color.WHITE);
        physicalCatalogTable.setGridColor(new Color(200, 200, 200));
        physicalCatalogTable.setSelectionBackground(primaryColor);
        physicalCatalogTable.setSelectionForeground(Color.WHITE);

        physicalCatalogTable.getColumnModel().getColumn(0).setPreferredWidth(200);
        physicalCatalogTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        physicalCatalogTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        physicalCatalogTable.getColumnModel().getColumn(3).setPreferredWidth(80);
        physicalCatalogTable.getColumnModel().getColumn(4).setPreferredWidth(80);
        physicalCatalogTable.getColumnModel().getColumn(5).setPreferredWidth(100);

        // Ubah tombol "Pinjam" menjadi "Baca" untuk buku fisik
        physicalCatalogTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer("Baca", primaryColor));
        physicalCatalogTable.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), "Baca", primaryColor));

        JScrollPane physicalScrollPane = new JScrollPane(physicalCatalogTable);
        physicalScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel physicalTablePanel = new JPanel(new BorderLayout());
        physicalTablePanel.setBackground(cardColor);
        physicalTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        physicalTablePanel.add(physicalScrollPane, BorderLayout.CENTER);

        // Tambahkan tabel ke tabbed pane
        catalogTabbedPane.addTab("Buku Digital", digitalTablePanel);
        catalogTabbedPane.addTab("Buku Fisik", physicalTablePanel);
        catalogPanel.add(catalogTabbedPane, BorderLayout.CENTER);

        // Isi tabel dengan data awal
        for (Book book : libraryManager.getAllBooks()) {
            String filePath = libraryManager.getBookFilePath(book.getIsbn());
            boolean hasFile = filePath != null && new File(filePath).exists();
            boolean hasUrl = bookUrlMap.containsKey(book.getTitle());
            boolean isDigital = hasFile || hasUrl;
            String type = isDigital ? "Digital" : "Fisik";
            String status = book.isAvailable() ? "Tersedia" : "Dipinjam";
            Object[] rowData = new Object[]{book.getTitle(), book.getAuthor(), book.getYear(), type, status, "Baca"}; // Ubah "Pinjam" menjadi "Baca"
            if (isDigital) {
                digitalTableModel.addRow(rowData);
            } else {
                physicalTableModel.addRow(rowData);
            }
        }

        // Listener untuk pencarian
        ActionListener searchAction = e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String subject = subjectField.getText().trim();

            digitalTableModel.setRowCount(0);
            physicalTableModel.setRowCount(0);

            for (Book book : libraryManager.getAllBooks()) {
                boolean matchesTitle = title.isEmpty() || book.getTitle().toLowerCase().contains(title.toLowerCase());
                boolean matchesAuthor = author.isEmpty() || book.getAuthor().toLowerCase().contains(author.toLowerCase());
                boolean matchesSubject = subject.isEmpty() || book.getSubject().toLowerCase().contains(subject.toLowerCase());

                if (matchesTitle && matchesAuthor && matchesSubject) {
                    String filePath = libraryManager.getBookFilePath(book.getIsbn());
                    boolean hasFile = filePath != null && new File(filePath).exists();
                    boolean hasUrl = bookUrlMap.containsKey(book.getTitle());
                    boolean isDigital = hasFile || hasUrl;
                    String type = isDigital ? "Digital" : "Fisik";
                    String status = book.isAvailable() ? "Tersedia" : "Dipinjam";
                    Object[] rowData = new Object[]{book.getTitle(), book.getAuthor(), book.getYear(), type, status, "Baca"}; // Ubah "Pinjam" menjadi "Baca"
                    if (isDigital) {
                        digitalTableModel.addRow(rowData);
                    } else {
                        physicalTableModel.addRow(rowData);
                    }
                }
            }
        };

        searchButton.addActionListener(searchAction);
        titleField.addActionListener(searchAction);
        authorField.addActionListener(searchAction);
        subjectField.addActionListener(searchAction);

        resetButton.addActionListener(e -> {
            titleField.setText("");
            authorField.setText("");
            subjectField.setText("");
            searchAction.actionPerformed(e);
        });

        return catalogPanel;
    }

    private void filterCatalog(DefaultTableModel tableModel, String title, String author, String subject) {
        tableModel.setRowCount(0);
        List<Book> books = libraryManager.getAllBooks();
        books = books.stream()
                .filter(book -> title.isEmpty() || book.getTitle().toLowerCase().contains(title.toLowerCase()))
                .filter(book -> author.isEmpty() || book.getAuthor().toLowerCase().contains(author.toLowerCase()))
                .filter(book -> subject.isEmpty() || book.getSubject().toLowerCase().contains(subject.toLowerCase()))
                .distinct()
                .collect(Collectors.toList());

        for (Book book : books) {
            String filePath = libraryManager.getBookFilePath(book.getIsbn());
            boolean hasFile = filePath != null && new File(filePath).exists();
            String type = (hasFile || bookUrlMap.containsKey(book.getTitle())) ? "Digital" : "Fisik";
            tableModel.addRow(new Object[]{book.getTitle(), book.getAuthor(), book.getSubject(), type, book.isAvailable() ? "Tersedia" : "Dipinjam", "Detail"});
        }
    }

    private JPanel createBookPanel(Color backgroundColor, Color cardColor, Color primaryColor, Color accentColor, Color textColor, Font labelFont, Font fieldFont, Font tableFont) {
        JPanel bookPanel = new JPanel(new BorderLayout());
        bookPanel.setBackground(backgroundColor);
        bookPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel input untuk menambah buku (diperkecil)
        JPanel inputPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        inputPanel.setBackground(cardColor);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createTitledBorder("Tambah Buku Baru")));

        // Font untuk label (diubah menjadi bold) dan field
        Font smallerLabelFont = new Font("Segoe UI", Font.BOLD, 12); // Ubah ke BOLD
        Font smallerFieldFont = new Font("Segoe UI", Font.PLAIN, 12);

        JLabel titleLabel = new JLabel("Judul:");
        titleLabel.setFont(smallerLabelFont);
        titleLabel.setForeground(textColor);
        JTextField titleField = new JTextField();
        titleField.setFont(smallerFieldFont);
        titleField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        titleField.setPreferredSize(new Dimension(150, 25));

        JLabel authorLabel = new JLabel("Penulis:");
        authorLabel.setFont(smallerLabelFont);
        authorLabel.setForeground(textColor);
        JTextField authorField = new JTextField();
        authorField.setFont(smallerFieldFont);
        authorField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        authorField.setPreferredSize(new Dimension(150, 25));

        JLabel yearLabel = new JLabel("Tahun Terbit:");
        yearLabel.setFont(smallerLabelFont);
        yearLabel.setForeground(textColor);
        JTextField yearField = new JTextField();
        yearField.setFont(smallerFieldFont);
        yearField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        yearField.setPreferredSize(new Dimension(150, 25));

        JLabel typeLabel = new JLabel("Tipe Buku:");
        typeLabel.setFont(smallerLabelFont);
        typeLabel.setForeground(textColor);
        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Digital", "Fisik"});
        typeComboBox.setFont(smallerFieldFont);
        typeComboBox.setPreferredSize(new Dimension(150, 25));

        JLabel urlLabel = new JLabel("URL (jika Digital):");
        urlLabel.setFont(smallerLabelFont);
        urlLabel.setForeground(textColor);
        JTextField urlField = new JTextField();
        urlField.setFont(smallerFieldFont);
        urlField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(3, 3, 3, 3)));
        urlField.setPreferredSize(new Dimension(150, 25));

        JButton addButton = createStyledButton("Tambah Buku", primaryColor, smallerLabelFont, new Dimension(120, 25));

        inputPanel.add(titleLabel);
        inputPanel.add(titleField);
        inputPanel.add(authorLabel);
        inputPanel.add(authorField);
        inputPanel.add(yearLabel);
        inputPanel.add(yearField);
        inputPanel.add(typeLabel);
        inputPanel.add(typeComboBox);
        inputPanel.add(urlLabel);
        inputPanel.add(urlField);
        inputPanel.add(new JLabel());
        inputPanel.add(addButton);

        bookPanel.add(inputPanel, BorderLayout.NORTH);

        // Tabbed pane untuk memisahkan buku fisik dan digital
        JTabbedPane bookTabbedPane = new JTabbedPane();
        bookTabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        bookTabbedPane.setBackground(backgroundColor);

        // Font yang lebih besar untuk tabel
        Font largerTableFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font largerHeaderFont = new Font("Segoe UI", Font.BOLD, 16);

        // Tabel untuk buku digital (dibuat lebih besar dan jelas)
        String[] columnNames = {"Judul", "Penulis", "Tahun Terbit", "Tipe", "Status", "Aksi"};
        DefaultTableModel digitalTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable digitalBookTable = new JTable(digitalTableModel);
        digitalBookTable.setRowHeight(50); // Tinggi baris lebih besar
        digitalBookTable.setFont(largerTableFont);
        digitalBookTable.setBackground(Color.WHITE);
        digitalBookTable.setForeground(textColor);
        digitalBookTable.getTableHeader().setFont(largerHeaderFont);
        digitalBookTable.getTableHeader().setBackground(primaryColor);
        digitalBookTable.getTableHeader().setForeground(Color.WHITE);
        digitalBookTable.setGridColor(new Color(200, 200, 200));
        digitalBookTable.setSelectionBackground(primaryColor);
        digitalBookTable.setSelectionForeground(Color.WHITE);

        // Lebar kolom lebih besar dan proporsional
        digitalBookTable.getColumnModel().getColumn(0).setPreferredWidth(400); // Judul lebih lebar
        digitalBookTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Penulis lebih lebar
        digitalBookTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        digitalBookTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        digitalBookTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        digitalBookTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        digitalBookTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                }
                return c;
            }
        });

        digitalBookTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer("Pinjam", primaryColor));
        digitalBookTable.getColumn("Aksi").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JButton button = new JButton("Pinjam");
            private int row;

            {
                button.setOpaque(true);
                button.setBackground(primaryColor);
                button.setForeground(Color.WHITE);
                button.addActionListener(e -> {
                    String bookTitle = (String) digitalTableModel.getValueAt(row, 0);
                    String isbn = null;
                    for (Book book : libraryManager.getAllBooks()) {
                        if (book.getTitle().equals(bookTitle)) {
                            isbn = book.getIsbn();
                            break;
                        }
                    }
                    if (isbn == null) {
                        JOptionPane.showMessageDialog(bookPanel, "Buku tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String currentUser = "Muhammad Arga Reksapati";
                    boolean isBorrowedByOther = libraryManager.isBookBorrowedByOther(isbn, currentUser);
                    if (isBorrowedByOther) {
                        JOptionPane.showMessageDialog(bookPanel, "Buku ini sedang dipinjam oleh pengguna lain!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    tabbedPane.setSelectedIndex(3);
                    JPanel loanPanel = (JPanel) tabbedPane.getComponentAt(3);
                    JComboBox<String> bookComboBox = (JComboBox<String>) ((JPanel) loanPanel.getComponent(0)).getComponent(1);
                    for (int i = 0; i < bookComboBox.getItemCount(); i++) {
                        if (bookComboBox.getItemAt(i).equals(bookTitle)) {
                            bookComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                this.row = row;
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "Pinjam";
            }
        });

        JScrollPane digitalScrollPane = new JScrollPane(digitalBookTable);
        digitalScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel digitalTablePanel = new JPanel(new BorderLayout());
        digitalTablePanel.setBackground(cardColor);
        digitalTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        digitalTablePanel.add(digitalScrollPane, BorderLayout.CENTER);

        // Tabel untuk buku fisik (dibuat lebih besar dan jelas)
        DefaultTableModel physicalTableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable physicalBookTable = new JTable(physicalTableModel);
        physicalBookTable.setRowHeight(50); // Tinggi baris lebih besar
        physicalBookTable.setFont(largerTableFont);
        physicalBookTable.setBackground(Color.WHITE);
        physicalBookTable.setForeground(textColor);
        physicalBookTable.getTableHeader().setFont(largerHeaderFont);
        physicalBookTable.getTableHeader().setBackground(primaryColor);
        physicalBookTable.getTableHeader().setForeground(Color.WHITE);
        physicalBookTable.setGridColor(new Color(200, 200, 200));
        physicalBookTable.setSelectionBackground(primaryColor);
        physicalBookTable.setSelectionForeground(Color.WHITE);

        // Lebar kolom lebih besar dan proporsional
        physicalBookTable.getColumnModel().getColumn(0).setPreferredWidth(400); // Judul lebih lebar
        physicalBookTable.getColumnModel().getColumn(1).setPreferredWidth(250); // Penulis lebih lebar
        physicalBookTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        physicalBookTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        physicalBookTable.getColumnModel().getColumn(4).setPreferredWidth(120);
        physicalBookTable.getColumnModel().getColumn(5).setPreferredWidth(150);

        physicalBookTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(245, 245, 245));
                }
                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                }
                return c;
            }
        });

        physicalBookTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer("Pinjam", primaryColor));
        physicalBookTable.getColumn("Aksi").setCellEditor(new DefaultCellEditor(new JCheckBox()) {
            private JButton button = new JButton("Pinjam");
            private int row;

            {
                button.setOpaque(true);
                button.setBackground(primaryColor);
                button.setForeground(Color.WHITE);
                button.addActionListener(e -> {
                    String bookTitle = (String) physicalTableModel.getValueAt(row, 0);
                    String isbn = null;
                    for (Book book : libraryManager.getAllBooks()) {
                        if (book.getTitle().equals(bookTitle)) {
                            isbn = book.getIsbn();
                            break;
                        }
                    }
                    if (isbn == null) {
                        JOptionPane.showMessageDialog(bookPanel, "Buku tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    String currentUser = "Muhammad Arga Reksapati";
                    boolean isBorrowedByOther = libraryManager.isBookBorrowedByOther(isbn, currentUser);
                    if (isBorrowedByOther) {
                        JOptionPane.showMessageDialog(bookPanel, "Buku ini sedang dipinjam oleh pengguna lain!", "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    tabbedPane.setSelectedIndex(3);
                    JPanel loanPanel = (JPanel) tabbedPane.getComponentAt(3);
                    JComboBox<String> bookComboBox = (JComboBox<String>) ((JPanel) loanPanel.getComponent(0)).getComponent(1);
                    for (int i = 0; i < bookComboBox.getItemCount(); i++) {
                        if (bookComboBox.getItemAt(i).equals(bookTitle)) {
                            bookComboBox.setSelectedIndex(i);
                            break;
                        }
                    }
                    fireEditingStopped();
                });
            }

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                this.row = row;
                return button;
            }

            @Override
            public Object getCellEditorValue() {
                return "Pinjam";
            }
        });

        JScrollPane physicalScrollPane = new JScrollPane(physicalBookTable);
        physicalScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel physicalTablePanel = new JPanel(new BorderLayout());
        physicalTablePanel.setBackground(cardColor);
        physicalTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        physicalTablePanel.add(physicalScrollPane, BorderLayout.CENTER);

        bookTabbedPane.addTab("Buku Digital", digitalTablePanel);
        bookTabbedPane.addTab("Buku Fisik", physicalTablePanel);
        bookPanel.add(bookTabbedPane, BorderLayout.CENTER);

        JButton deleteButton = createStyledButton("Hapus Buku Terpilih", accentColor, labelFont, null);
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(deleteButton);
        bookPanel.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> {
            String title = titleField.getText().trim();
            String author = authorField.getText().trim();
            String yearText = yearField.getText().trim();
            String type = (String) typeComboBox.getSelectedItem();
            String url = urlField.getText().trim();

            if (title.isEmpty() || author.isEmpty() || yearText.isEmpty()) {
                JOptionPane.showMessageDialog(bookPanel, "Semua kolom harus diisi kecuali URL!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (author.equalsIgnoreCase("Unknown")) {
                JOptionPane.showMessageDialog(bookPanel, "Penulis tidak boleh 'Unknown'!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (type.equals("Digital") && url.isEmpty()) {
                JOptionPane.showMessageDialog(bookPanel, "Buku digital harus memiliki URL!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                int year = Integer.parseInt(yearText);
                if (year < 1500 || year > LocalDate.now().getYear()) {
                    JOptionPane.showMessageDialog(bookPanel, "Tahun terbit tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int bookCount = libraryManager.getAllBooks().size() + 1;
                String isbn;
                boolean isbnExists;
                do {
                    isbn = "ISBN" + String.format("%03d", bookCount);
                    isbnExists = false;
                    for (Book existingBook : libraryManager.getAllBooks()) {
                        if (existingBook.getIsbn().equals(isbn)) {
                            isbnExists = true;
                            bookCount++;
                            break;
                        }
                    }
                } while (isbnExists);

                Book newBook = new Book(isbn, title, author, year, "Unknown", type.equals("Digital") ? "Digital" : "Textbook", null);
                libraryManager.addBook(newBook);

                if (type.equals("Digital")) {
                    bookUrlMap.put(title, url);
                }

                titleField.setText("");
                authorField.setText("");
                yearField.setText("");
                urlField.setText("");
                typeComboBox.setSelectedIndex(0);
                JOptionPane.showMessageDialog(bookPanel, "Buku berhasil ditambahkan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                updateCatalogPanel();
                updateLoanPanel();
                updateStatisticsPanel();
                updateBookTable();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(bookPanel, "Tahun terbit harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        deleteButton.addActionListener(e -> {
            JTable selectedTable = bookTabbedPane.getSelectedIndex() == 0 ? digitalBookTable : physicalBookTable;
            DefaultTableModel selectedModel = bookTabbedPane.getSelectedIndex() == 0 ? digitalTableModel : physicalTableModel;
            int selectedRow = selectedTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(bookPanel, "Pilih buku yang ingin dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(bookPanel, "Apakah Anda yakin ingin menghapus buku ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                String title = (String) selectedModel.getValueAt(selectedRow, 0);
                for (Book book : libraryManager.getAllBooks()) {
                    if (book.getTitle().equals(title)) {
                        boolean isBorrowed = libraryManager.getAllLoans().stream()
                                .anyMatch(loan -> loan.getBookIsbn().equals(book.getIsbn()) && !loan.isReturned());
                        if (isBorrowed) {
                            JOptionPane.showMessageDialog(bookPanel, "Buku sedang dipinjam dan tidak dapat dihapus!", "Error", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        libraryManager.removeBook(book.getIsbn());
                        bookUrlMap.remove(title);
                        updateCatalogPanel();
                        updateLoanPanel();
                        JOptionPane.showMessageDialog(bookPanel, "Buku berhasil dihapus!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                        updateStatisticsPanel();
                        updateBookTable();
                        break;
                    }
                }
            }
        });

        return bookPanel;
    }



    private JPanel createLoanPanel(Color backgroundColor, Color headerColor, Color buttonColor, Color buttonHoverColor, Color warningColor, Font headerFont, Font labelFont, Font buttonFont) {
        JPanel loanPanel = new JPanel(new BorderLayout());
        loanPanel.setBackground(backgroundColor);

        // Panel utama untuk bagian atas (menggabungkan header dan input)
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(backgroundColor);

        JLabel headerLabel = new JLabel("Pinjam Buku");
        headerLabel.setFont(headerFont);
        headerLabel.setForeground(headerColor);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topPanel.add(headerLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel bookLabel = new JLabel("Pilih Buku:");
        bookLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(bookLabel, gbc);

        Map<String, String> bookUrlMap = new HashMap<>();
        JComboBox<String> bookComboBox = new JComboBox<>();
        for (Book book : libraryManager.getAllBooks()) {
            if (book.isAvailable()) {
                String bookType = "Digital".equals(book.getType()) ? "Digital" : "Fisik";
                bookComboBox.addItem(book.getTitle() + " (" + bookType + ")");
                if ("Digital".equals(book.getType()) && book.getFilePath() != null) {
                    bookUrlMap.put(book.getTitle(), book.getFilePath());
                }
            }
        }
        bookComboBox.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        inputPanel.add(bookComboBox, gbc);

        JLabel borrowerLabel = new JLabel("Nama Peminjam:");
        borrowerLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        inputPanel.add(borrowerLabel, gbc);

        JTextField borrowerField = new JTextField();
        borrowerField.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(borrowerField, gbc);

        JLabel classLabel = new JLabel("Kelas:");
        classLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        inputPanel.add(classLabel, gbc);

        JTextField classField = new JTextField();
        classField.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        inputPanel.add(classField, gbc);

        JLabel nimLabel = new JLabel("NIM:");
        nimLabel.setFont(labelFont);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        inputPanel.add(nimLabel, gbc);

        JTextField nimField = new JTextField();
        nimField.setFont(labelFont);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        inputPanel.add(nimField, gbc);

        // Batasi input NIM hanya boleh berisi angka
        PlainDocument doc = (PlainDocument) nimField.getDocument();
        doc.setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                if (string == null) return;
                if (string.matches("\\d*")) {
                    super.insertString(fb, offset, string, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                if (text == null) return;
                if (text.matches("\\d*")) {
                    super.replace(fb, offset, length, text, attrs);
                }
            }
        });

        JButton borrowButton = new JButton("Pinjam");
        borrowButton.setFont(buttonFont);
        borrowButton.setBackground(buttonColor);
        borrowButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        inputPanel.add(borrowButton, gbc);

        JButton returnButton = new JButton("Kembalikan");
        returnButton.setFont(buttonFont);
        returnButton.setBackground(warningColor);
        returnButton.setForeground(Color.WHITE);
        gbc.gridx = 2;
        gbc.gridy = 4;
        inputPanel.add(returnButton, gbc);

        JButton notReturnedButton = new JButton("Belum Dikembalikan");
        notReturnedButton.setFont(buttonFont);
        notReturnedButton.setBackground(buttonHoverColor);
        notReturnedButton.setForeground(Color.WHITE);
        gbc.gridx = 3;
        gbc.gridy = 4;
        inputPanel.add(notReturnedButton, gbc);

        topPanel.add(inputPanel, BorderLayout.CENTER);
        loanPanel.add(topPanel, BorderLayout.NORTH);

        // Tambahkan kolom "Tipe Buku" di tabel peminjaman
        String[] columns = {"ID Peminjaman", "Judul Buku", "Tipe Buku", "Peminjam", "Kelas", "NIM", "Tanggal Pinjam", "Tanggal Kembali", "Status", "Aksi"};
        DefaultTableModel tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Kolom "Aksi" dapat diedit (indeks 9)
            }
        };
        JTable loanTable = new JTable(tableModel);
        loanTable.setRowHeight(35);
        loanTable.setFont(labelFont);
        loanTable.setBackground(Color.WHITE);
        loanTable.setForeground(new Color(33, 33, 33));
        loanTable.getTableHeader().setFont(buttonFont);
        loanTable.getTableHeader().setBackground(buttonColor);
        loanTable.getTableHeader().setForeground(Color.WHITE);
        loanTable.setGridColor(new Color(200, 200, 200));
        loanTable.setSelectionBackground(buttonColor);
        loanTable.setSelectionForeground(Color.WHITE);
        for (int i = 0; i < loanTable.getColumnCount(); i++) {
            loanTable.getColumnModel().getColumn(i).setPreferredWidth(150);
        }

        JScrollPane tableScrollPane = new JScrollPane(loanTable);
        tableScrollPane.setBorder(BorderFactory.createEmptyBorder());
        loanPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Listener untuk tombol "Pinjam"
        borrowButton.addActionListener(e -> {
            int selectedIndex = bookComboBox.getSelectedIndex();
            if (selectedIndex == -1) {
                JOptionPane.showMessageDialog(loanPanel, "Pilih buku yang ingin dipinjam!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedItem = (String) bookComboBox.getSelectedItem();
            String bookTitle = selectedItem.split(" \\(")[0];
            String bookType = selectedItem.split(" \\(")[1].replace(")", "");

            Book selectedBook = libraryManager.getAllBooks().stream()
                    .filter(book -> book.getTitle().equals(bookTitle))
                    .findFirst()
                    .orElse(null);

            if (selectedBook == null) {
                JOptionPane.showMessageDialog(loanPanel, "Buku tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String borrower = borrowerField.getText().trim();
            if (borrower.isEmpty()) {
                JOptionPane.showMessageDialog(loanPanel, "Nama peminjam tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String borrowerClass = classField.getText().trim();
            String nim = nimField.getText().trim();

            if (borrowerClass.isEmpty()) {
                JOptionPane.showMessageDialog(loanPanel, "Kelas tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (nim.isEmpty()) {
                JOptionPane.showMessageDialog(loanPanel, "NIM tidak boleh kosong!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!nim.matches("\\d+")) {
                JOptionPane.showMessageDialog(loanPanel, "NIM hanya boleh berisi angka!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LocalDate borrowDate = LocalDate.now();
            LocalDate dueDate = borrowDate.plusDays(7);
            String loanId = "LOAN" + String.format("%03d", libraryManager.getAllLoans().size() + 1);
            Loan loan = new Loan(loanId, selectedBook.getIsbn(), borrower, borrowerClass, nim, borrowDate, dueDate);
            libraryManager.addLoan(loan);
            selectedBook.setAvailable(false); // Tandai buku sebagai tidak tersedia

            tableModel.addRow(new Object[]{loan.getLoanId(), selectedBook.getTitle(), bookType, borrower, borrowerClass, nim, borrowDate.toString(), dueDate.toString(), "Dipinjam", "Perpanjang"});
            updateBookTable();
            updateCatalogPanel();
            bookComboBox.removeItemAt(selectedIndex);
            borrowerField.setText("");
            classField.setText("");
            nimField.setText("");
            JOptionPane.showMessageDialog(loanPanel, "Pengajuan peminjaman berhasil! Buku akan dipinjam hingga " + dueDate + ".", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            updateFineManagementPanel();
            updateStatisticsPanel();
        });

        // Listener untuk tombol "Kembalikan"
        returnButton.addActionListener(e -> {
            int selectedRow = loanTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(loanPanel, "Pilih peminjaman yang ingin dikembalikan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String loanId = (String) tableModel.getValueAt(selectedRow, 0);
            Loan loan = libraryManager.getLoanById(loanId);
            if (loan != null && !loan.isReturned()) {
                loan.setReturned(true);
                loan.setReturnDate(LocalDate.now());
                libraryManager.returnBook(loanId);
                Book book = libraryManager.getBookByIsbn(loan.getBookIsbn());
                if (book != null) {
                    book.setAvailable(true);
                    String bookType = "Digital".equals(book.getType()) ? "Digital" : "Fisik";
                    bookComboBox.addItem(book.getTitle() + " (" + bookType + ")");
                }
                tableModel.setValueAt(loan.getReturnDate().toString(), selectedRow, 7); // Update Tanggal Kembali
                tableModel.setValueAt("Sudah Dikembalikan", selectedRow, 8); // Update Status
                updateBookTable();
                updateCatalogPanel();
                JOptionPane.showMessageDialog(loanPanel, "Buku berhasil dikembalikan pada " + loan.getReturnDate() + "!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
                updateFineManagementPanel();
                updateStatisticsPanel();
            } else {
                JOptionPane.showMessageDialog(loanPanel, "Peminjaman ini sudah dikembalikan!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        // Listener untuk tombol "Belum Dikembalikan"
        notReturnedButton.addActionListener(e -> {
            int selectedRow = loanTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(loanPanel, "Pilih peminjaman yang ingin ditandai sebagai belum dikembalikan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String loanId = (String) tableModel.getValueAt(selectedRow, 0);
            Loan selectedLoan = libraryManager.getAllLoans().stream()
                    .filter(loan -> loan.getLoanId().equals(loanId))
                    .findFirst()
                    .orElse(null);

            if (selectedLoan == null || selectedLoan.isReturned()) {
                JOptionPane.showMessageDialog(loanPanel, "Peminjaman ini sudah dikembalikan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            updateFineManagementPanel();
            JOptionPane.showMessageDialog(loanPanel, "Peminjaman telah ditambahkan ke Manajemen Denda dan Peringatan!", "Sukses", JOptionPane.INFORMATION_MESSAGE);
            tabbedPane.setSelectedIndex(5);
            updateStatisticsPanel();
        });

        // Inisialisasi tabel dengan semua data peminjaman
        for (Loan loan : libraryManager.getAllLoans()) {
            Book book = libraryManager.getBookByIsbn(loan.getBookIsbn());
            String bookType = "Digital".equals(book.getType()) ? "Digital" : "Fisik";
            String returnDateStr = loan.isReturned() ? loan.getReturnDate().toString() : "Belum Dikembalikan";
            String status = loan.isReturned() ? "Sudah Dikembalikan" : "Dipinjam";
            tableModel.addRow(new Object[]{
                    loan.getLoanId(),
                    libraryManager.getBookTitleByIsbn(loan.getBookIsbn()),
                    bookType,
                    loan.getBorrowerName(),
                    loan.getBorrowerClass(),
                    loan.getNim(),
                    loan.getBorrowDate().toString(),
                    returnDateStr,
                    status,
                    "Perpanjang"
            });
        }

        return loanPanel;
    }

    private boolean isBookInComboBox(JComboBox<String> comboBox, String bookTitle) {
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (comboBox.getItemAt(i).equals(bookTitle)) {
                return true;
            }
        }
        return false;
    }

    private void checkAutomaticReturns() {
        LocalDate today = LocalDate.now();
        for (Loan loan : libraryManager.getAllLoans()) {
            if (!loan.isReturned()) {
                long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                if (daysLate > 30) {
                    double fine = 10000 + (daysLate - 1) * 5000;
                    loan.setFine(fine);
                    libraryManager.returnBook(loan.getLoanId());
                    JOptionPane.showMessageDialog(this, "Peminjaman " + loan.getLoanId() + " telah dikembalikan otomatis karena keterlambatan lebih dari 30 hari. Denda: " + formatRupiah(fine), "Pengembalian Otomatis", JOptionPane.INFORMATION_MESSAGE);
                    updateStatisticsPanel();
                }
            }
        }
        updateBookTable();
        updateCatalogPanel();
        updateLoanPanel();
        updateFineManagementPanel();
    }

    private void updateLoanPanel() {
        JPanel loanPanel = (JPanel) tabbedPane.getComponentAt(3);

        // Komponen ke-1 adalah JScrollPane (tableScrollPane)
        JScrollPane tableScrollPane = (JScrollPane) loanPanel.getComponent(1);
        JTable loanTable = (JTable) tableScrollPane.getViewport().getView();
        DefaultTableModel loanTableModel = (DefaultTableModel) loanTable.getModel();

        // Kosongkan tabel sebelum mengisi ulang
        loanTableModel.setRowCount(0);

        // Ambil semua pinjaman (baik yang sudah dikembalikan maupun yang belum)
        List<Loan> loans = libraryManager.getAllLoans();
        for (Loan loan : loans) {
            Book book = libraryManager.getBookByIsbn(loan.getBookIsbn());
            String bookType = "Digital".equals(book.getType()) ? "Digital" : "Fisik";
            String returnDateStr = loan.isReturned() ? loan.getReturnDate().toString() : "Belum Dikembalikan";
            String status = loan.isReturned() ? "Sudah Dikembalikan" : "Dipinjam";
            Object[] rowData = new Object[]{
                    loan.getLoanId(),
                    libraryManager.getBookTitleByIsbn(loan.getBookIsbn()),
                    bookType, // Kolom Tipe Buku
                    loan.getBorrowerName(),
                    loan.getBorrowerClass(),
                    loan.getNim(),
                    loan.getBorrowDate().toString(),
                    returnDateStr,
                    status,
                    "Perpanjang"
            };
            loanTableModel.addRow(rowData);
        }

        // Perbarui daftar buku yang tersedia untuk dipinjam di JComboBox
        JPanel topPanel = (JPanel) loanPanel.getComponent(0);
        JComboBox<String> bookComboBox = (JComboBox<String>) ((JPanel) topPanel.getComponent(1)).getComponent(1);
        bookComboBox.removeAllItems();
        for (Book book : libraryManager.getAllBooks()) {
            if (book.isAvailable()) {
                String bookType = "Digital".equals(book.getType()) ? "Digital" : "Fisik";
                bookComboBox.addItem(book.getTitle() + " (" + bookType + ")");
            }
        }
    }

    private JPanel createFineManagementPanel(Color backgroundColor, Color cardColor, Color primaryColor, Color accentColor, Color textColor, Font labelFont, Font fieldFont, Font tableFont) {
        JPanel fineManagementPanel = new JPanel(new BorderLayout());
        fineManagementPanel.setBackground(backgroundColor);
        fineManagementPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        JLabel headerLabel = new JLabel("Manajemen Denda dan Peringatan");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        headerLabel.setForeground(primaryColor);
        headerPanel.add(headerLabel, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("Segarkan", primaryColor, labelFont, null);
        refreshButton.addActionListener(e -> {
            updateFineManagementPanel();
            updateStatisticsPanel();
        });
        headerPanel.add(refreshButton, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        fineManagementPanel.add(headerPanel, BorderLayout.NORTH);

        JTabbedPane subTabbedPane = new JTabbedPane();
        subTabbedPane.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subTabbedPane.setBackground(backgroundColor);

        JPanel reminderPanel = new JPanel(new BorderLayout());
        reminderPanel.setBackground(backgroundColor);

        String[] reminderColumns = {"ID Peminjaman", "Buku", "Peminjam", "Tanggal Pinjam", "Hari Terlambat", "Aksi"};
        reminderModel = new DefaultTableModel(reminderColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5;
            }
        };
        JTable reminderTable = new JTable(reminderModel);
        reminderTable.setRowHeight(35);
        reminderTable.setFont(tableFont);
        reminderTable.setBackground(Color.WHITE);
        reminderTable.setForeground(textColor);
        reminderTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        reminderTable.getTableHeader().setBackground(primaryColor);
        reminderTable.getTableHeader().setForeground(Color.WHITE);
        reminderTable.setGridColor(new Color(200, 200, 200));
        reminderTable.setSelectionBackground(primaryColor);
        reminderTable.setSelectionForeground(Color.WHITE);

        reminderTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        reminderTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        reminderTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        reminderTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        reminderTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        reminderTable.getColumnModel().getColumn(5).setPreferredWidth(120);

        reminderTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer("Kirim Pengingat", primaryColor));
        reminderTable.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), "Kirim Pengingat", primaryColor) {
            @Override
            public Object getCellEditorValue() {
                if (isPushed) {
                    String loanId = (String) reminderTable.getValueAt(row, 0);
                    String borrower = (String) reminderTable.getValueAt(row, 2);
                    String bookTitle = (String) reminderTable.getValueAt(row, 1);
                    Loan loan = libraryManager.getLoanById(loanId);
                    if (loan != null && !loan.isReturned() && !loan.isFinePaid()) {
                        double currentFine = loan.getFine();
                        double newFine = currentFine + 10000;
                        loan.setFine(newFine);
                        libraryManager.updateLoanFine(loanId, newFine);
                        JOptionPane.showMessageDialog(fineManagementPanel, "Pengingat dikirim ke " + borrower + " untuk mengembalikan '" + bookTitle + "'! Denda bertambah Rp 10.000. Total denda: " + formatRupiah(newFine));
                    }
                }
                isPushed = false;
                return label;
            }
        });

        JScrollPane reminderScrollPane = new JScrollPane(reminderTable);
        reminderScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel reminderTablePanel = new JPanel(new BorderLayout());
        reminderTablePanel.setBackground(cardColor);
        reminderTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        reminderTablePanel.add(reminderScrollPane, BorderLayout.CENTER);
        reminderPanel.add(reminderTablePanel, BorderLayout.CENTER);

        reminderEmptyLabel = new JLabel("Belum ada peminjaman yang perlu diingatkan.", SwingConstants.CENTER);
        reminderEmptyLabel.setFont(fieldFont);
        reminderEmptyLabel.setForeground(Color.GRAY);
        reminderPanel.add(reminderEmptyLabel, BorderLayout.SOUTH);
        reminderEmptyLabel.setVisible(reminderModel.getRowCount() == 0);

        JPanel finePanel = new JPanel(new BorderLayout());
        finePanel.setBackground(backgroundColor);

        String[] fineColumns = {"Peminjam", "Buku", "Denda (Rp)", "Status", "Aksi"};
        fineModel = new DefaultTableModel(fineColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4;
            }
        };
        JTable fineTable = new JTable(fineModel);
        fineTable.setRowHeight(35);
        fineTable.setFont(tableFont);
        fineTable.setBackground(Color.WHITE);
        fineTable.setForeground(textColor);
        fineTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        fineTable.getTableHeader().setBackground(primaryColor);
        fineTable.getTableHeader().setForeground(Color.WHITE);
        fineTable.setGridColor(new Color(200, 200, 200));
        fineTable.setSelectionBackground(primaryColor);
        fineTable.setSelectionForeground(Color.WHITE);

        fineTable.getColumnModel().getColumn(0).setPreferredWidth(150);
        fineTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        fineTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        fineTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        fineTable.getColumnModel().getColumn(4).setPreferredWidth(120);

        fineTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer("Tandai Lunas", primaryColor) {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                String fineStr = (String) table.getValueAt(row, 2);
                double fine = fineStr.equals("Rp 0,00") ? 0 : Double.parseDouble(fineStr.replace("Rp ", "").replace(".", "").replace(",", "."));
                setText(fine > 0 ? "Tandai Lunas" : "Tidak Ada Denda");
                setBackground(fine > 0 ? primaryColor : Color.GRAY);
                return this;
            }
        });
        fineTable.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), "Tandai Lunas", primaryColor) {
            @Override
            public Object getCellEditorValue() {
                if (isPushed) {
                    String fineStr = (String) table.getValueAt(row, 2);
                    double fine = fineStr.equals("Rp 0,00") ? 0 : Double.parseDouble(fineStr.replace("Rp ", "").replace(".", "").replace(",", "."));
                    if (fine <= 0) {
                        JOptionPane.showMessageDialog(fineManagementPanel, "Tidak ada denda untuk ditagih!");
                    } else {
                        String borrower = (String) table.getValueAt(row, 0);
                        String bookTitle = (String) table.getValueAt(row, 1);
                        for (Loan loan : libraryManager.getAllLoans()) {
                            if (loan.getBorrowerName().equals(borrower) && libraryManager.getBookTitleByIsbn(loan.getBookIsbn()).equals(bookTitle)) {
                                libraryManager.markFineAsPaid(loan.getLoanId());
                                JOptionPane.showMessageDialog(fineManagementPanel, "Denda untuk " + borrower + " telah ditandai sebagai lunas!");
                                updateFineManagementPanel();
                                updateStatisticsPanel();
                                break;
                            }
                        }
                    }
                }
                isPushed = false;
                return label;
            }
        });

        JScrollPane fineScrollPane = new JScrollPane(fineTable);
        fineScrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel fineTablePanel = new JPanel(new BorderLayout());
        fineTablePanel.setBackground(cardColor);
        fineTablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        fineTablePanel.add(fineScrollPane, BorderLayout.CENTER);
        finePanel.add(fineTablePanel, BorderLayout.CENTER);

        JPanel fineInfoPanel = new JPanel(new BorderLayout());
        fineInfoPanel.setBackground(backgroundColor);
        fineInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel fineRuleLabel = new JLabel("Aturan Denda: Rp 10.000 untuk 1 hari keterlambatan, tambah Rp 5.000 per hari berikutnya.");
        fineRuleLabel.setFont(fieldFont);
        fineRuleLabel.setForeground(primaryColor);
        fineInfoPanel.add(fineRuleLabel, BorderLayout.WEST);

        totalFineLabel = new JLabel("Total Denda: " + formatRupiah(0));
        totalFineLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        totalFineLabel.setForeground(accentColor);
        fineInfoPanel.add(totalFineLabel, BorderLayout.EAST);

        finePanel.add(fineInfoPanel, BorderLayout.SOUTH);

        fineEmptyLabel = new JLabel("Belum ada denda yang perlu ditagih.", SwingConstants.CENTER);
        fineEmptyLabel.setFont(fieldFont);
        fineEmptyLabel.setForeground(Color.GRAY);
        finePanel.add(fineEmptyLabel, BorderLayout.NORTH);
        fineEmptyLabel.setVisible(fineModel.getRowCount() == 0);

        subTabbedPane.addTab("Pengingat Pengembalian", reminderPanel);
        subTabbedPane.addTab("Sistem Denda", finePanel);

        fineManagementPanel.add(subTabbedPane, BorderLayout.CENTER);
        return fineManagementPanel;
    }

    private void updateFineManagementPanel() {
        JTabbedPane subTabbedPane = (JTabbedPane) ((JPanel) tabbedPane.getComponentAt(5)).getComponent(1);

        // Update tab Pengingat Pengembalian
        JPanel reminderPanel = (JPanel) subTabbedPane.getComponentAt(0);
        JScrollPane reminderScrollPane = (JScrollPane) ((JPanel) reminderPanel.getComponent(0)).getComponent(0);
        JTable reminderTable = (JTable) reminderScrollPane.getViewport().getView();
        reminderModel.setRowCount(0);

        LocalDate today = LocalDate.now();
        for (Loan loan : libraryManager.getAllLoans()) {
            if (!loan.isReturned() && !loan.isFinePaid()) {
                long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), today);
                reminderModel.addRow(new Object[]{
                        loan.getLoanId(),
                        libraryManager.getBookTitleByIsbn(loan.getBookIsbn()),
                        loan.getBorrowerName(),
                        loan.getBorrowDate().toString(),
                        daysLate > 0 ? daysLate : 0,
                        "Kirim Pengingat"
                });
            }
        }

        reminderEmptyLabel.setVisible(reminderModel.getRowCount() == 0);

        // Update tab Sistem Denda
        JPanel finePanel = (JPanel) subTabbedPane.getComponentAt(1);
        JScrollPane fineScrollPane = (JScrollPane) ((JPanel) finePanel.getComponent(0)).getComponent(0);
        JTable fineTable = (JTable) fineScrollPane.getViewport().getView();
        fineModel.setRowCount(0);

        double totalFine = 0;
        for (Loan loan : libraryManager.getAllLoans()) {
            if (!loan.isReturned() && !loan.isFinePaid()) {
                double fine = loan.getFine();
                totalFine += fine;
                fineModel.addRow(new Object[]{
                        loan.getBorrowerName(),
                        libraryManager.getBookTitleByIsbn(loan.getBookIsbn()),
                        formatRupiah(fine), // Gunakan formatRupiah untuk kolom denda
                        fine > 0 ? "Belum Lunas" : "Lunas",
                        fine > 0 ? "Tandai Lunas" : "Tidak Ada Denda"
                });
            }
        }

        totalFineLabel.setText("Total Denda: " + formatRupiah(totalFine)); // Gunakan formatRupiah untuk label total denda
        fineEmptyLabel.setVisible(fineModel.getRowCount() == 0);
    }

    private void updateBookTable() {
        JPanel bookPanel = (JPanel) tabbedPane.getComponentAt(2);
        JTabbedPane bookTabbedPane = (JTabbedPane) bookPanel.getComponent(1);

        // Update tabel buku digital
        JScrollPane digitalScrollPane = (JScrollPane) ((JPanel) bookTabbedPane.getComponentAt(0)).getComponent(0);
        JTable digitalBookTable = (JTable) digitalScrollPane.getViewport().getView();
        DefaultTableModel digitalTableModel = (DefaultTableModel) digitalBookTable.getModel();

        // Update tabel buku fisik
        JScrollPane physicalScrollPane = (JScrollPane) ((JPanel) bookTabbedPane.getComponentAt(1)).getComponent(0);
        JTable physicalBookTable = (JTable) physicalScrollPane.getViewport().getView();
        DefaultTableModel physicalTableModel = (DefaultTableModel) physicalBookTable.getModel();

        // Kosongkan kedua tabel sebelum mengisi ulang
        digitalTableModel.setRowCount(0);
        physicalTableModel.setRowCount(0);

        // Ambil semua buku
        List<Book> allBooks = libraryManager.getAllBooks();

        // Pisahkan buku digital dan fisik berdasarkan bookUrlMap
        List<Book> digitalBooks = new ArrayList<>();
        List<Book> physicalBooks = new ArrayList<>();

        for (Book book : allBooks) {
            boolean isDigital = bookUrlMap.containsKey(book.getTitle());
            if (isDigital) {
                digitalBooks.add(book);
            } else {
                physicalBooks.add(book);
            }
        }

        // Urutkan buku berdasarkan judul
        digitalBooks.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
        physicalBooks.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));

        // Isi tabel buku digital
        for (Book book : digitalBooks) {
            String type = "Digital";
            String status = book.isAvailable() ? "Tersedia" : "Dipinjam";
            Object[] rowData = new Object[]{book.getTitle(), book.getAuthor(), book.getYear(), type, status, "Baca"};
            digitalTableModel.addRow(rowData);
        }

        // Isi tabel buku fisik
        for (Book book : physicalBooks) {
            String type = "Fisik";
            String status = book.isAvailable() ? "Tersedia" : "Dipinjam";
            Object[] rowData = new Object[]{book.getTitle(), book.getAuthor(), book.getYear(), type, status, "Pinjam"};
            physicalTableModel.addRow(rowData);
        }
    }

    private void updateCatalogPanel() {
        JPanel catalogPanel = (JPanel) tabbedPane.getComponentAt(1);
        JTabbedPane catalogTabbedPane = (JTabbedPane) catalogPanel.getComponent(2);

        // Update tabel buku digital
        JScrollPane digitalScrollPane = (JScrollPane) ((JPanel) catalogTabbedPane.getComponentAt(0)).getComponent(0);
        JTable digitalCatalogTable = (JTable) digitalScrollPane.getViewport().getView();
        DefaultTableModel digitalTableModel = (DefaultTableModel) digitalCatalogTable.getModel();

        // Update tabel buku fisik
        JScrollPane physicalScrollPane = (JScrollPane) ((JPanel) catalogTabbedPane.getComponentAt(1)).getComponent(0);
        JTable physicalCatalogTable = (JTable) physicalScrollPane.getViewport().getView();
        DefaultTableModel physicalTableModel = (DefaultTableModel) physicalCatalogTable.getModel();

        // Kosongkan kedua tabel sebelum mengisi ulang
        digitalTableModel.setRowCount(0);
        physicalTableModel.setRowCount(0);

        // Ambil kriteria pencarian dari field pencarian menggunakan variabel instance
        String title = titleField.getText().trim();
        String author = authorField.getText().trim();
        String subject = subjectField.getText().trim();

        // Ambil semua buku
        List<Book> allBooks = libraryManager.getAllBooks();

        // Pisahkan buku digital dan fisik berdasarkan bookUrlMap
        List<Book> digitalBooks = new ArrayList<>();
        List<Book> physicalBooks = new ArrayList<>();

        for (Book book : allBooks) {
            System.out.println("Memproses buku - Judul: " + book.getTitle() + ", Penulis: " + book.getAuthor() + ", Tipe: " + book.getType());

            boolean matchesTitle = title.isEmpty() || book.getTitle().toLowerCase().contains(title.toLowerCase());
            boolean matchesAuthor = author.isEmpty() || book.getAuthor().toLowerCase().contains(author.toLowerCase());
            boolean matchesSubject = subject.isEmpty() || book.getSubject().toLowerCase().contains(subject.toLowerCase());

            if (matchesTitle && matchesAuthor && matchesSubject) {
                boolean isDigital = bookUrlMap.containsKey(book.getTitle());
                if (isDigital) {
                    digitalBooks.add(book);
                } else {
                    physicalBooks.add(book);
                }
            }
        }

        System.out.println("Buku Digital:");
        for (Book book : digitalBooks) {
            System.out.println("- " + book.getTitle());
        }
        System.out.println("Buku Fisik:");
        for (Book book : physicalBooks) {
            System.out.println("- " + book.getTitle());
        }

        // Urutkan buku berdasarkan judul
        digitalBooks.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));
        physicalBooks.sort((b1, b2) -> b1.getTitle().compareToIgnoreCase(b2.getTitle()));

        // Isi tabel buku digital (dengan tombol "Baca")
        for (Book book : digitalBooks) {
            String type = "Digital";
            String status = book.isAvailable() ? "Tersedia" : "Dipinjam";
            Object[] rowData = new Object[]{book.getTitle(), book.getAuthor(), book.getYear(), type, status, "Baca"};
            digitalTableModel.addRow(rowData);
        }

        // Isi tabel buku fisik (dengan tombol "Baca")
        for (Book book : physicalBooks) {
            String type = "Fisik";
            String status = book.isAvailable() ? "Tersedia" : "Dipinjam";
            Object[] rowData = new Object[]{book.getTitle(), book.getAuthor(), book.getYear(), type, status, "Baca"};
            physicalTableModel.addRow(rowData);
        }
    }

    private void updateStatisticsPanel() {
        statisticsPanel.updateStatistics();
    }

    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer(String text, Color bgColor) {
            setOpaque(true);
            setBackground(bgColor);
            setForeground(Color.WHITE);
            setText(text);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        protected JButton button;
        protected String label;
        protected boolean isPushed;
        protected int row;
        protected JTable table;

        public ButtonEditor(JCheckBox checkBox, String text, Color bgColor) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setText(text);
            button.addActionListener(e -> fireEditingStopped());
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.table = table;
            label = (value == null) ? button.getText() : value.toString();
            button.setText(label);
            isPushed = true;
            this.row = row;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed && button.getText().equals("Baca")) {
                String bookTitle = (String) table.getValueAt(row, 0);
                readBook(bookTitle);
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    private void readBook(String bookTitle) {
        // Cari buku berdasarkan judul
        Book selectedBook = null;
        String isbn = null;
        for (Book book : libraryManager.getAllBooks()) {
            if (book.getTitle().equals(bookTitle)) {
                selectedBook = book;
                isbn = book.getIsbn();
                break;
            }
        }

        if (selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Buku tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Cek tipe buku (Digital atau Fisik) hanya berdasarkan URL
        boolean hasUrl = bookUrlMap.containsKey(bookTitle);
        boolean isDigital = hasUrl;

        // Tangani buku fisik terlebih dahulu
        if (!isDigital) {
            String currentUser = "Muhammad Arga Reksapati";
            boolean isBorrowedByOther = libraryManager.isBookBorrowedByOther(isbn, currentUser);
            if (isBorrowedByOther) {
                JOptionPane.showMessageDialog(this, "Buku fisik ini sedang dipinjam oleh pengguna lain dan tidak dapat dibaca!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Buku ini adalah buku fisik dan tidak dapat dibaca secara online.", "Info", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Buat panel untuk pembaca buku (hanya untuk buku digital)
        JPanel readerPanel = new JPanel(new BorderLayout());
        readerPanel.setBackground(Color.WHITE);

        // Panel untuk menampilkan halaman buku
        JPanel pagePanel = new JPanel(new CardLayout());
        pagePanel.setBackground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(pagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);

        // Panel untuk tombol navigasi samping
        JPanel navigationPanel = new JPanel(new BorderLayout());
        navigationPanel.setBackground(Color.WHITE);

        JButton sidePrevButton = new JButton("<");
        sidePrevButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sidePrevButton.setBackground(new Color(33, 150, 243)); // Warna biru profesional
        sidePrevButton.setForeground(Color.WHITE);
        sidePrevButton.setFocusPainted(false);
        sidePrevButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        sidePrevButton.setPreferredSize(new Dimension(50, 50));
        sidePrevButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efek hover untuk tombol navigasi
        sidePrevButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sidePrevButton.setBackground(new Color(25, 118, 210)); // Warna biru lebih gelap saat hover
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                sidePrevButton.setBackground(new Color(33, 150, 243));
            }
        });

        JButton sideNextButton = new JButton(">");
        sideNextButton.setFont(new Font("Segoe UI", Font.BOLD, 24));
        sideNextButton.setBackground(new Color(33, 150, 243));
        sideNextButton.setForeground(Color.WHITE);
        sideNextButton.setFocusPainted(false);
        sideNextButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        sideNextButton.setPreferredSize(new Dimension(50, 50));
        sideNextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Efek hover untuk tombol navigasi
        sideNextButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                sideNextButton.setBackground(new Color(25, 118, 210));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                sideNextButton.setBackground(new Color(33, 150, 243));
            }
        });

        navigationPanel.add(sidePrevButton, BorderLayout.WEST);
        navigationPanel.add(scrollPane, BorderLayout.CENTER);
        navigationPanel.add(sideNextButton, BorderLayout.EAST);

        JPanel controlPanel = new JPanel();
        controlPanel.setBackground(new Color(33, 150, 243)); // Warna biru profesional
        controlPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        controlPanel.setForeground(Color.WHITE);

        // Tambahkan efek shadow pada panel kontrol
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0, 0, 50), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel pageLabel = new JLabel("Halaman 1");
        pageLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        pageLabel.setForeground(Color.WHITE);

        JLabel loadingLabel = new JLabel("Memuat PDF...");
        loadingLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        loadingLabel.setForeground(Color.WHITE);

        JButton zoomInButton = new JButton("Zoom In");
        zoomInButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        zoomInButton.setBackground(Color.WHITE);
        zoomInButton.setForeground(new Color(33, 150, 243));
        zoomInButton.setFocusPainted(false);
        zoomInButton.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 2));

        JButton zoomOutButton = new JButton("Zoom Out");
        zoomOutButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        zoomOutButton.setBackground(Color.WHITE);
        zoomOutButton.setForeground(new Color(33, 150, 243));
        zoomOutButton.setFocusPainted(false);
        zoomOutButton.setBorder(BorderFactory.createLineBorder(new Color(33, 150, 243), 2));

        JButton closeButton = new JButton("Tutup");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(229, 57, 53)); // Warna merah untuk tombol tutup
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        JButton minimizeButton = new JButton("-");
        minimizeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        minimizeButton.setBackground(new Color(255, 193, 7)); // Warna kuning untuk tombol minimize
        minimizeButton.setForeground(Color.WHITE);
        minimizeButton.setFocusPainted(false);
        minimizeButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));

        controlPanel.add(pageLabel);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(zoomInButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(zoomOutButton);
        controlPanel.add(Box.createHorizontalStrut(20));
        controlPanel.add(loadingLabel);
        controlPanel.add(Box.createHorizontalGlue());
        controlPanel.add(minimizeButton);
        controlPanel.add(Box.createHorizontalStrut(10));
        controlPanel.add(closeButton);

        readerPanel.add(controlPanel, BorderLayout.NORTH);
        readerPanel.add(navigationPanel, BorderLayout.CENTER);

        double zoomFactor = 1.0;
        final double[] zoomFactorArray = {zoomFactor};
        final int[] currentPage = {0};
        final List<JLabel> pageLabels = new ArrayList<>();

        PDDocument document = null;
        int totalPages = 0;

        try {
            // Ambil URL buku dari bookUrlMap
            String pdfUrl = bookUrlMap.get(bookTitle);
            if (pdfUrl == null) {
                throw new Exception("URL untuk buku '" + bookTitle + "' tidak ditemukan.");
            }

            loadingLabel.setText("Mengunduh buku: " + bookTitle + "...");

            // Pastikan direktori ebooks/ ada
            File ebooksDir = new File("ebooks/");
            if (!ebooksDir.exists()) {
                if (!ebooksDir.mkdirs()) {
                    throw new Exception("Gagal membuat direktori ebooks/. Pastikan aplikasi memiliki izin untuk menulis di direktori ini.");
                }
            }

            // Buat nama file yang aman untuk sistem operasi
            String safeBookTitle = bookTitle.replaceAll("[^a-zA-Z0-9.-]", "_");
            String tempFilePath = "ebooks/temp_" + System.currentTimeMillis() + ".pdf";
            File tempFile = new File(tempFilePath);

            // Unduh file dari URL
            try (InputStream in = new URL(pdfUrl).openStream()) {
                Files.copy(in, Paths.get(tempFilePath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new Exception("Gagal mengunduh file PDF dari " + pdfUrl + ": " + e.getMessage());
            }

            // Validasi file yang diunduh
            if (!tempFile.exists() || tempFile.length() == 0) {
                throw new Exception("File PDF gagal diunduh atau kosong.");
            }

            // Baca dokumen sementara untuk memastikan validitas
            try (PDDocument tempDoc = PDDocument.load(tempFile)) {
                if (tempDoc.getNumberOfPages() == 0) {
                    throw new Exception("File PDF yang diunduh tidak memiliki halaman.");
                }
            } catch (IOException e) {
                throw new Exception("File PDF yang diunduh tidak valid: " + e.getMessage());
            }

            // Ganti nama file menggunakan Files.move
            String filePath = "ebooks/" + safeBookTitle + ".pdf";
            File pdfFile = new File(filePath);
            if (pdfFile.exists()) {
                if (!pdfFile.delete()) {
                    throw new Exception("Gagal menghapus file PDF lama di " + filePath);
                }
            }

            try {
                Files.move(Paths.get(tempFilePath), Paths.get(filePath), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new Exception("Gagal mengganti nama file PDF dari " + tempFilePath + " ke " + filePath + ": " + e.getMessage());
            }

            // Validasi file akhir
            if (!pdfFile.exists() || pdfFile.length() == 0) {
                throw new Exception("File PDF gagal disimpan di " + filePath);
            }

            // Update file path di library manager
            libraryManager.updateBookFilePath(isbn, filePath);
            loadingLabel.setText("Buku berhasil diunduh! Memuat PDF...");

            // Buka dokumen untuk pembacaan
            document = PDDocument.load(new File(filePath));
            totalPages = document.getNumberOfPages();
            if (totalPages == 0) {
                throw new Exception("Dokumen PDF tidak memiliki halaman.");
            }

        } catch (Exception e) {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            loadingLabel.setVisible(false);
            JLabel errorLabel = new JLabel("Error: " + e.getMessage());
            errorLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            errorLabel.setForeground(Color.RED);
            errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            JButton retryButton = new JButton("Coba Lagi");
            retryButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            retryButton.setBackground(new Color(33, 150, 243));
            retryButton.setForeground(Color.WHITE);
            retryButton.setFocusPainted(false);
            retryButton.addActionListener(ev -> readBook(bookTitle));
            pagePanel.add(errorLabel, "error");
            pagePanel.add(retryButton, "retry");
            pagePanel.revalidate();
            pagePanel.repaint();
            System.err.println("Error saat memuat PDF: " + e.getMessage());
            e.printStackTrace();
            return;
        }

        final PDDocument finalDocument = document;
        final int finalTotalPages = totalPages;
        PDFRenderer pdfRenderer = new PDFRenderer(finalDocument);

        // Pramuat semua halaman
        for (int page = 0; page < finalTotalPages; page++) {
            try {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, (float) (96 * zoomFactorArray[0]));
                JLabel pageLabelImage = new JLabel(new ImageIcon(image));
                pageLabelImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                pageLabels.add(pageLabelImage);
                pagePanel.add(pageLabelImage, String.valueOf(page));
            } catch (IOException e) {
                System.err.println("Error rendering page " + page + ": " + e.getMessage());
            }
        }

        // Tampilkan halaman pertama
        CardLayout cardLayout = (CardLayout) pagePanel.getLayout();
        cardLayout.show(pagePanel, String.valueOf(currentPage[0]));
        pageLabel.setText("Halaman " + (currentPage[0] + 1) + " / " + finalTotalPages);
        loadingLabel.setVisible(false);

        // Listener untuk navigasi halaman (digunakan oleh tombol samping)
        ActionListener prevPageListener = e -> {
            if (currentPage[0] > 0) {
                loadingLabel.setVisible(true);
                loadingLabel.setText("Memuat halaman...");
                final int targetPage = currentPage[0] - 1;

                // Efek transisi sederhana: fade out lalu fade in
                Timer fadeOutTimer = new Timer(20, new ActionListener() {
                    float opacity = 1.0f;

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        opacity -= 0.1f;
                        if (opacity <= 0) {
                            ((Timer) evt.getSource()).stop();
                            currentPage[0] = targetPage;
                            cardLayout.show(pagePanel, String.valueOf(currentPage[0]));
                            pageLabel.setText("Halaman " + (currentPage[0] + 1) + " / " + finalTotalPages);

                            // Fade in
                            Timer fadeInTimer = new Timer(20, new ActionListener() {
                                float opacity = 0.0f;

                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    opacity += 0.1f;
                                    if (opacity >= 1.0f) {
                                        ((Timer) evt.getSource()).stop();
                                        loadingLabel.setVisible(false);
                                    }
                                }
                            });
                            fadeInTimer.start();
                        }
                    }
                });
                fadeOutTimer.start();
            }
        };

        ActionListener nextPageListener = e -> {
            if (currentPage[0] < finalTotalPages - 1) {
                loadingLabel.setVisible(true);
                loadingLabel.setText("Memuat halaman...");
                final int targetPage = currentPage[0] + 1;

                // Efek transisi sederhana: fade out lalu fade in
                Timer fadeOutTimer = new Timer(20, new ActionListener() {
                    float opacity = 1.0f;

                    @Override
                    public void actionPerformed(ActionEvent evt) {
                        opacity -= 0.1f;
                        if (opacity <= 0) {
                            ((Timer) evt.getSource()).stop();
                            currentPage[0] = targetPage;
                            cardLayout.show(pagePanel, String.valueOf(currentPage[0]));
                            pageLabel.setText("Halaman " + (currentPage[0] + 1) + " / " + finalTotalPages);

                            // Fade in
                            Timer fadeInTimer = new Timer(20, new ActionListener() {
                                float opacity = 0.0f;

                                @Override
                                public void actionPerformed(ActionEvent evt) {
                                    opacity += 0.1f;
                                    if (opacity >= 1.0f) {
                                        ((Timer) evt.getSource()).stop();
                                        loadingLabel.setVisible(false);
                                    }
                                }
                            });
                            fadeInTimer.start();
                        }
                    }
                });
                fadeOutTimer.start();
            }
        };

        // Tambahkan listener ke tombol navigasi samping
        sidePrevButton.addActionListener(prevPageListener);
        sideNextButton.addActionListener(nextPageListener);

        zoomInButton.addActionListener(e -> {
            zoomFactorArray[0] = Math.min(zoomFactorArray[0] + 0.1, 2.0);
            loadingLabel.setVisible(true);
            loadingLabel.setText("Memperbesar...");
            pageLabels.clear();
            pagePanel.removeAll();
            for (int page = 0; page < finalTotalPages; page++) {
                try {
                    BufferedImage image = pdfRenderer.renderImageWithDPI(page, (float) (96 * zoomFactorArray[0]));
                    JLabel pageLabelImage = new JLabel(new ImageIcon(image));
                    pageLabelImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                    pageLabels.add(pageLabelImage);
                    pagePanel.add(pageLabelImage, String.valueOf(page));
                } catch (IOException ex) {
                    System.err.println("Error rendering page " + page + ": " + ex.getMessage());
                }
            }
            cardLayout.show(pagePanel, String.valueOf(currentPage[0]));
            loadingLabel.setVisible(false);
        });

        zoomOutButton.addActionListener(e -> {
            zoomFactorArray[0] = Math.max(zoomFactorArray[0] - 0.1, 0.1);
            loadingLabel.setVisible(true);
            loadingLabel.setText("Memperkecil...");
            pageLabels.clear();
            pagePanel.removeAll();
            for (int page = 0; page < finalTotalPages; page++) {
                try {
                    BufferedImage image = pdfRenderer.renderImageWithDPI(page, (float) (96 * zoomFactorArray[0]));
                    JLabel pageLabelImage = new JLabel(new ImageIcon(image));
                    pageLabelImage.setAlignmentX(Component.CENTER_ALIGNMENT);
                    pageLabels.add(pageLabelImage);
                    pagePanel.add(pageLabelImage, String.valueOf(page));
                } catch (IOException ex) {
                    System.err.println("Error rendering page " + page + ": " + ex.getMessage());
                }
            }
            cardLayout.show(pagePanel, String.valueOf(currentPage[0]));
            loadingLabel.setVisible(false);
        });

        closeButton.addActionListener(e -> {
            try {
                if (finalDocument != null) {
                    finalDocument.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(readerPanel);
            frame.dispose();
            updateStatisticsPanel();
        });

        minimizeButton.addActionListener(e -> {
            JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(readerPanel);
            frame.setState(Frame.ICONIFIED);
        });

        // Tampilkan pembaca dalam JFrame dengan mode full screen (untuk buku digital)
        JFrame readerFrame = new JFrame("Membaca: " + bookTitle);

        // Dapatkan ukuran layar
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        // Atur ukuran frame sesuai dengan ukuran layar
        readerFrame.setSize(screenSize);

        // Atur lokasi frame ke (0,0) untuk memastikan full screen
        readerFrame.setLocation(0, 0);

        // Hilangkan dekorasi jendela (title bar dan border) untuk full screen
        readerFrame.setUndecorated(true);

        readerFrame.add(readerPanel);
        readerFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        readerFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                try {
                    if (finalDocument != null) {
                        finalDocument.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                updateStatisticsPanel();
            }
        });

        readerFrame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryInterface());
    }
}
