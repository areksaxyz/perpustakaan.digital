import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class LoanOperations extends JPanel {
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private JTextField borrowerField;
    private JComboBox<String> bookComboBox;
    private JButton borrowButton, extendButton, returnButton, deleteButton, refreshButton;

    public LoanOperations() {
        // Warna tema modern
        Color primaryColor = new Color(25, 118, 210); // Biru profesional
        Color accentColor = new Color(229, 57, 53); // Merah untuk aksen
        Color backgroundColor = new Color(240, 242, 245); // Latar belakang abu-abu lembut
        Color cardColor = Color.WHITE; // Warna kartu untuk panel
        Color textColor = new Color(33, 33, 33); // Teks utama

        // Font modern
        Font titleFont = new Font("Segoe UI", Font.BOLD, 20);
        Font labelFont = new Font("Segoe UI", Font.BOLD, 14);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);
        Font tableFont = new Font("Segoe UI", Font.PLAIN, 14);

        // Atur layout utama
        setLayout(new BorderLayout(20, 20));
        setBackground(backgroundColor);

        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(cardColor);
        JLabel headerLabel = new JLabel("Manajemen Peminjaman");
        headerLabel.setFont(titleFont);
        headerLabel.setForeground(primaryColor);
        headerPanel.add(headerLabel, BorderLayout.WEST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(headerPanel, BorderLayout.NORTH);

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        inputPanel.setBackground(cardColor);
        inputPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createTitledBorder("Pengajuan Peminjaman Online")));

        JLabel bookLabel = new JLabel("Pilih Buku:");
        bookLabel.setFont(labelFont);
        bookLabel.setForeground(textColor);
        bookComboBox = new JComboBox<>();
        bookComboBox.setFont(fieldFont);

        JLabel borrowerLabel = new JLabel("Nama Peminjam:");
        borrowerLabel.setFont(labelFont);
        borrowerLabel.setForeground(textColor);
        borrowerField = new JTextField();
        borrowerField.setFont(fieldFont);
        borrowerField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        borrowButton = createStyledButton("Ajukan Pinjam", primaryColor, buttonFont, null);

        inputPanel.add(bookLabel);
        inputPanel.add(bookComboBox);
        inputPanel.add(borrowerLabel);
        inputPanel.add(borrowerField);
        inputPanel.add(new JLabel());
        inputPanel.add(borrowButton);

        add(inputPanel, BorderLayout.NORTH);

        // Tabel peminjaman
        String[] columnNames = {"ID Peminjaman", "Buku", "Peminjam", "Tanggal Pinjam", "Tanggal Jatuh Tempo", "Status", "Aksi"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6;
            }
        };
        loanTable = new JTable(tableModel);
        loanTable.setRowHeight(35);
        loanTable.setFont(tableFont);
        loanTable.setBackground(Color.WHITE);
        loanTable.setForeground(textColor);
        loanTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        loanTable.getTableHeader().setBackground(primaryColor);
        loanTable.getTableHeader().setForeground(Color.WHITE);
        loanTable.setGridColor(new Color(200, 200, 200));
        loanTable.setSelectionBackground(primaryColor);
        loanTable.setSelectionForeground(Color.WHITE);

        loanTable.getColumn("Aksi").setCellRenderer(new ButtonRenderer("Perpanjang", primaryColor));
        loanTable.getColumn("Aksi").setCellEditor(new ButtonEditor(new JCheckBox(), "Perpanjang", primaryColor));

        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(cardColor);
        tablePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Tombol aksi
        returnButton = createStyledButton("Kembalikan Buku", new Color(255, 165, 0), buttonFont, null);
        deleteButton = createStyledButton("Hapus Peminjaman", accentColor, buttonFont, null);
        refreshButton = createStyledButton("Segarkan", primaryColor, buttonFont, null);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.add(returnButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(refreshButton);
        add(buttonPanel, BorderLayout.SOUTH);
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
            label = (value == null) ? button.getText() : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }

    // Getter untuk komponen (akan digunakan oleh LibraryInterface)
    public JTable getLoanTable() {
        return loanTable;
    }

    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    public JTextField getBorrowerField() {
        return borrowerField;
    }

    public JComboBox<String> getBookComboBox() {
        return bookComboBox;
    }

    public JButton getBorrowButton() {
        return borrowButton;
    }

    public JButton getReturnButton() {
        return returnButton;
    }

    public JButton getDeleteButton() {
        return deleteButton;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }
}
