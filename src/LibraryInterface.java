import javax.swing.*;
import java.awt.*;

public class LibraryInterface extends JFrame {
    public LibraryInterface() {
        setTitle("Perpustakaan Digital");
        setSize(900, 600); // Ukuran lebih besar untuk tampilan profesional
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel utama dengan warna latar belakang
        getContentPane().setBackground(new Color(245, 245, 245)); // Abu-abu muda

        // Tabbed pane dengan ikon
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Roboto", Font.PLAIN, 14)); // Font modern

        // Tambah tab dengan ikon
        tabbedPane.addTab("Buku", new ImageIcon("icons/book.png"), new BookOperations());
        tabbedPane.addTab("Peminjaman", new ImageIcon("icons/loan.png"), new LoanOperations());
        tabbedPane.addTab("Statistik", new ImageIcon("icons/stats.png"), new StatisticsPanel());

        // Tambah tabbed pane ke frame
        add(tabbedPane);
    }
}