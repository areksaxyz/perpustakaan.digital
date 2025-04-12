import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class LibraryManager {
    private static final String DB_URL = "jdbc:sqlite:storage/library.db";
    private static final double FINE_PER_DAY = 1000.0; // Denda Rp 1.000 per hari

    public LibraryManager() {
        createTables();
    }

    private void createTables() {
        String bookTable = "CREATE TABLE IF NOT EXISTS books (" +
                "isbn TEXT PRIMARY KEY, " +
                "title TEXT, " +
                "author TEXT, " +
                "year INTEGER, " +
                "subject TEXT, " +
                "type TEXT, " +
                "filePath TEXT, " +
                "isAvailable BOOLEAN)";
        String loanTable = "CREATE TABLE IF NOT EXISTS loans (" +
                "loanId TEXT PRIMARY KEY, " +
                "bookIsbn TEXT, " +
                "borrowerName TEXT, " +
                "borrowDate TEXT, " +
                "dueDate TEXT, " +
                "isReturned BOOLEAN, " +
                "fine REAL)";
        String readingHistoryTable = "CREATE TABLE IF NOT EXISTS reading_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentName TEXT, " +
                "bookIsbn TEXT, " +
                "readDate TEXT)"; // Baru: Tabel untuk riwayat baca
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(bookTable);
            stmt.execute(loanTable);
            stmt.execute(readingHistoryTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addBook(Book book) {
        String sql = "INSERT INTO books (isbn, title, author, year, subject, type, filePath, isAvailable) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, book.getIsbn());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setInt(4, book.getYear());
            pstmt.setString(5, book.getSubject());
            pstmt.setString(6, book.getType());
            pstmt.setString(7, book.getFilePath());
            pstmt.setBoolean(8, book.isAvailable());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Book> getAllBooks() {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getString("subject"),
                        rs.getString("type"),
                        rs.getString("filePath")
                );
                book.setAvailable(rs.getBoolean("isAvailable"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public List<Book> searchBooks(String query, String field) {
        List<Book> books = new ArrayList<>();
        String sql = "";
        switch (field.toLowerCase()) {
            case "judul":
                sql = "SELECT * FROM books WHERE title LIKE ?";
                break;
            case "penulis":
                sql = "SELECT * FROM books WHERE author LIKE ?";
                break;
            case "subjek":
                sql = "SELECT * FROM books WHERE subject LIKE ?";
                break;
            default:
                return books;
        }
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, "%" + query + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Book book = new Book(
                        rs.getString("isbn"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year"),
                        rs.getString("subject"),
                        rs.getString("type"),
                        rs.getString("filePath")
                );
                book.setAvailable(rs.getBoolean("isAvailable"));
                books.add(book);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return books;
    }

    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (loanId, bookIsbn, borrowerName, borrowDate, dueDate, isReturned, fine) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loan.getLoanId());
            pstmt.setString(2, loan.getBookIsbn());
            pstmt.setString(3, loan.getBorrowerName());
            pstmt.setString(4, loan.getBorrowDate().toString());
            pstmt.setString(5, loan.getDueDate().toString());
            pstmt.setBoolean(6, loan.isReturned());
            pstmt.setDouble(7, loan.getFine());
            pstmt.executeUpdate();

            // Tandai buku sebagai tidak tersedia
            String updateBook = "UPDATE books SET isAvailable = ? WHERE isbn = ?";
            try (PreparedStatement bookStmt = conn.prepareStatement(updateBook)) {
                bookStmt.setBoolean(1, false);
                bookStmt.setString(2, loan.getBookIsbn());
                bookStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Loan> getAllLoans() {
        List<Loan> loans = new ArrayList<>();
        String sql = "SELECT * FROM loans";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Loan loan = new Loan(
                        rs.getString("loanId"),
                        rs.getString("bookIsbn"),
                        rs.getString("borrowerName"),
                        LocalDate.parse(rs.getString("borrowDate")),
                        LocalDate.parse(rs.getString("dueDate"))
                );
                loan.setReturned(rs.getBoolean("isReturned"));
                loan.setFine(rs.getDouble("fine"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Hitung denda untuk peminjaman yang belum dikembalikan
        for (Loan loan : loans) {
            if (!loan.isReturned()) {
                double fine = calculateFine(loan);
                loan.setFine(fine);
                updateLoanFine(loan.getLoanId(), fine);
            }
        }
        return loans;
    }

    public void extendLoan(String loanId, int days) {
        String sql = "UPDATE loans SET dueDate = ? WHERE loanId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Loan loan = getLoanById(loanId);
            if (loan != null && !loan.isReturned()) {
                LocalDate newDueDate = loan.getDueDate().plusDays(days);
                pstmt.setString(1, newDueDate.toString());
                pstmt.setString(2, loanId);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void returnBook(String loanId) {
        String sql = "UPDATE loans SET isReturned = ?, fine = ? WHERE loanId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            Loan loan = getLoanById(loanId);
            if (loan != null) {
                double finalFine = calculateFine(loan);
                pstmt.setBoolean(1, true);
                pstmt.setDouble(2, finalFine);
                pstmt.setString(3, loanId);
                pstmt.executeUpdate();

                // Tandai buku sebagai tersedia
                String updateBook = "UPDATE books SET isAvailable = ? WHERE isbn = ?";
                try (PreparedStatement bookStmt = conn.prepareStatement(updateBook)) {
                    bookStmt.setBoolean(1, true);
                    bookStmt.setString(2, loan.getBookIsbn());
                    bookStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Baru: Merekam riwayat baca
    public void recordReading(String studentName, String bookIsbn) {
        String sql = "INSERT INTO reading_history (studentName, bookIsbn, readDate) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            pstmt.setString(2, bookIsbn);
            pstmt.setString(3, LocalDate.now().toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Baru: Statistik - Buku populer (berdasarkan peminjaman dan pembacaan)
    public List<Map.Entry<String, Integer>> getPopularBooks() {
        Map<String, Integer> bookCounts = new HashMap<>();

        // Hitung dari peminjaman
        String loanSql = "SELECT bookIsbn FROM loans";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(loanSql)) {
            while (rs.next()) {
                String isbn = rs.getString("bookIsbn");
                bookCounts.put(isbn, bookCounts.getOrDefault(isbn, 0) + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Hitung dari pembacaan
        String readSql = "SELECT bookIsbn FROM reading_history";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(readSql)) {
            while (rs.next()) {
                String isbn = rs.getString("bookIsbn");
                bookCounts.put(isbn, bookCounts.getOrDefault(isbn, 0) + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Urutkan berdasarkan jumlah (descending)
        List<Map.Entry<String, Integer>> sortedBooks = new ArrayList<>(bookCounts.entrySet());
        sortedBooks.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedBooks.subList(0, Math.min(5, sortedBooks.size())); // Top 5
    }

    // Baru: Statistik - Peminjam aktif
    public List<Map.Entry<String, Integer>> getActiveBorrowers() {
        Map<String, Integer> borrowerCounts = new HashMap<>();
        String sql = "SELECT borrowerName FROM loans";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String borrower = rs.getString("borrowerName");
                borrowerCounts.put(borrower, borrowerCounts.getOrDefault(borrower, 0) + 1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Map.Entry<String, Integer>> sortedBorrowers = new ArrayList<>(borrowerCounts.entrySet());
        sortedBorrowers.sort((a, b) -> b.getValue().compareTo(a.getValue()));
        return sortedBorrowers.subList(0, Math.min(5, sortedBorrowers.size())); // Top 5
    }

    // Baru: Rekam jejak siswa
    public List<String> getStudentHistory(String studentName) {
        List<String> history = new ArrayList<>();

        // Riwayat peminjaman
        String loanSql = "SELECT * FROM loans WHERE borrowerName = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(loanSql)) {
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString("bookIsbn");
                String borrowDate = rs.getString("borrowDate");
                String dueDate = rs.getString("dueDate");
                String status = rs.getBoolean("isReturned") ? "Dikembalikan" : "Dipinjam";
                history.add("Peminjaman - ISBN: " + isbn + ", Tgl Pinjam: " + borrowDate + ", Jatuh Tempo: " + dueDate + ", Status: " + status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Riwayat pembacaan
        String readSql = "SELECT * FROM reading_history WHERE studentName = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(readSql)) {
            pstmt.setString(1, studentName);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString("bookIsbn");
                String readDate = rs.getString("readDate");
                history.add("Pembacaan - ISBN: " + isbn + ", Tgl Baca: " + readDate);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return history;
    }

    private Loan getLoanById(String loanId) {
        String sql = "SELECT * FROM loans WHERE loanId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loanId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Loan loan = new Loan(
                        rs.getString("loanId"),
                        rs.getString("bookIsbn"),
                        rs.getString("borrowerName"),
                        LocalDate.parse(rs.getString("borrowDate")),
                        LocalDate.parse(rs.getString("dueDate"))
                );
                loan.setFine(rs.getDouble("fine"));
                return loan;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private double calculateFine(Loan loan) {
        if (loan.isReturned()) {
            return loan.getFine();
        }
        LocalDate today = LocalDate.now();
        if (today.isAfter(loan.getDueDate())) {
            long daysLate = ChronoUnit.DAYS.between(loan.getDueDate(), today);
            return daysLate * FINE_PER_DAY;
        }
        return 0.0;
    }

    private void updateLoanFine(String loanId, double fine) {
        String sql = "UPDATE loans SET fine = ? WHERE loanId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, fine);
            pstmt.setString(2, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Baru: Mendapatkan judul buku berdasarkan ISBN
    public String getBookTitleByIsbn(String isbn) {
        String sql = "SELECT title FROM books WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("title");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Unknown Book";
    }
}