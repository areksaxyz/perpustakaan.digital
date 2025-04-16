import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
            } catch (Exception ex) {
                System.err.println("Gagal memuat FlatLightLaf, menggunakan tema default: " + ex.getMessage());
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception e) {
                    System.err.println("Gagal memuat tema default: " + e.getMessage());
                }
            }
            new LibraryInterface();
        });
    }
}
