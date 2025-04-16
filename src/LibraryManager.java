import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LibraryManager {
    private static final String DB_URL = "jdbc:sqlite:storage/library.db";
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private Map<String, Book> bookCache;

    public LibraryManager() {
        bookCache = new HashMap<>();
        initializeDatabase();
        initializeDefaultBooks();
        loadBookCache();
    }

    private void initializeDatabase() {
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
                "borrowerClass TEXT, " +
                "nim TEXT, " +
                "borrowDate TEXT, " +
                "dueDate TEXT, " +
                "returned BOOLEAN, " +
                "returnDate TEXT, " +
                "fine REAL, " +
                "finePaid BOOLEAN, " +
                "FOREIGN KEY(bookIsbn) REFERENCES books(isbn))";
        String readingHistoryTable = "CREATE TABLE IF NOT EXISTS reading_history (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "studentName TEXT, " +
                "bookIsbn TEXT, " +
                "readDate TEXT, " +
                "FOREIGN KEY(bookIsbn) REFERENCES books(isbn))";

        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(bookTable);
            stmt.execute(loanTable);
            stmt.execute(readingHistoryTable);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    private void initializeDefaultBooks() {
        List<Book> defaultBooks = new ArrayList<>();

        defaultBooks.add(new Book("978-3-16-148410-0", "Algoritma Pemrograman", "Penulis Algoritma", 2020, "Algoritma", "Digital", "http://eprints.umsida.ac.id/9873/5/BE1-ALPO-BukuAjar.pdf"));
        defaultBooks.add(new Book("978-3-16-148411-7", "Pemrograman Java", "Penulis Java", 2019, "Pemrograman", "Digital", "https://digilib.stekom.ac.id/assets/dokumen/ebook/feb_BMuBPtvpXwUkhZqdyUPA7LyV7948c7ZdhjGj8z2EkAjSpNgD_njQSpM_1656322622.pdf"));
        defaultBooks.add(new Book("978-3-16-148412-4", "Dasar Dasar Algoritma", "Penulis Dasar Algoritma", 2021, "Algoritma", "Digital", "URL Tidak Tersedia"));
        defaultBooks.add(new Book("978-3-16-148413-1", "Pemrograman Python", "Penulis Python", 2022, "Pemrograman", "Digital", "https://repository.unikom.ac.id/65984/1/E-Book_Belajar_Pemrograman_Python_Dasar.pdf"));
        defaultBooks.add(new Book("978-3-16-148414-8", "JavaScript", "Penulis JavaScript", 2018, "Pemrograman", "Digital", "https://rahmatfauzi.com/wp-content/uploads/2019/12/W3-JavaScript.pdf"));
        defaultBooks.add(new Book("978-3-16-148415-5", "Matematika Diskrit", "Penulis Matematika", 2015, "Matematika", "Fisik", null));
        defaultBooks.add(new Book("978-3-16-148416-2", "Literasi Digital", "Penulis Literasi", 2023, "Literasi", "Fisik", null));
        defaultBooks.add(new Book("978-3-16-148417-9", "Bahasa Inggris", "Penulis Bahasa Inggris", 2017, "Bahasa", "Fisik", null));
        defaultBooks.add(new Book("978-3-16-148418-6", "Kewarganegaraan", "Penulis Kewarganegaraan", 2016, "Kewarganegaraan", "Fisik", null));

        for (Book book : defaultBooks) {
            if (!bookExists(book.getIsbn())) {
                book.setAvailable(true);
                addBook(book);
            }
        }
    }

    private void loadBookCache() {
        List<Book> books = getAllBooks();
        for (Book book : books) {
            bookCache.put(book.getIsbn(), book);
        }
    }

    private boolean bookExists(String isbn) {
        String sql = "SELECT COUNT(*) FROM books WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("Error checking book existence: " + e.getMessage());
            return false;
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
            bookCache.put(book.getIsbn(), book);
        } catch (SQLException e) {
            System.err.println("Error adding book: " + e.getMessage());
        }
    }

    public void removeBook(String isbn) {
        String sql = "DELETE FROM books WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.executeUpdate();
            bookCache.remove(isbn);
        } catch (SQLException e) {
            System.err.println("Error removing book: " + e.getMessage());
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
            System.err.println("Error retrieving books: " + e.getMessage());
        }
        return books;
    }

    public Book getBookByIsbn(String isbn) {
        return bookCache.getOrDefault(isbn, null);
    }

    public List<Book> searchBooks(String query, String field) {
        List<Book> allBooks = getAllBooks();
        List<Book> filteredBooks = new ArrayList<>();

        for (Book book : allBooks) {
            boolean matches = false;
            switch (field.toLowerCase()) {
                case "judul":
                    if (book.getTitle().toLowerCase().contains(query.toLowerCase())) {
                        matches = true;
                    }
                    break;
                case "penulis":
                    if (book.getAuthor().toLowerCase().contains(query.toLowerCase())) {
                        matches = true;
                    }
                    break;
                case "subjek":
                    if (book.getSubject().toLowerCase().contains(query.toLowerCase())) {
                        matches = true;
                    }
                    break;
            }
            if (matches) {
                filteredBooks.add(book);
            }
        }

        return filteredBooks;
    }

    public void addLoan(Loan loan) {
        String sql = "INSERT INTO loans (loanId, bookIsbn, borrowerName, borrowerClass, nim, borrowDate, dueDate, returned, returnDate, fine, finePaid) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, loan.getLoanId());
            pstmt.setString(2, loan.getBookIsbn());
            pstmt.setString(3, loan.getBorrowerName());
            pstmt.setString(4, loan.getBorrowerClass());
            pstmt.setString(5, loan.getNim());
            pstmt.setString(6, loan.getBorrowDate().format(DATE_FORMATTER));
            pstmt.setString(7, loan.getDueDate().format(DATE_FORMATTER));
            pstmt.setBoolean(8, loan.isReturned());
            pstmt.setString(9, null);
            pstmt.setDouble(10, loan.getFine());
            pstmt.setBoolean(11, loan.isFinePaid());
            pstmt.executeUpdate();

            String updateBookSql = "UPDATE books SET isAvailable = ? WHERE isbn = ?";
            try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {
                updateStmt.setBoolean(1, false);
                updateStmt.setString(2, loan.getBookIsbn());
                updateStmt.executeUpdate();
                Book book = bookCache.get(loan.getBookIsbn());
                if (book != null) {
                    book.setAvailable(false);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error adding loan: " + e.getMessage());
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
                        rs.getString("borrowerClass"),
                        rs.getString("nim"),
                        LocalDate.parse(rs.getString("borrowDate"), DATE_FORMATTER),
                        LocalDate.parse(rs.getString("dueDate"), DATE_FORMATTER)
                );
                loan.setReturned(rs.getBoolean("returned"));
                String returnDateStr = rs.getString("returnDate");
                if (returnDateStr != null) {
                    loan.setReturnDate(LocalDate.parse(returnDateStr, DATE_FORMATTER));
                }
                loan.setFine(rs.getDouble("fine"));
                loan.setFinePaid(rs.getBoolean("finePaid"));
                loans.add(loan);
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving loans: " + e.getMessage());
        }
        return loans;
    }

    public Loan getLoanById(String loanId) {
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
                        rs.getString("borrowerClass"),
                        rs.getString("nim"),
                        LocalDate.parse(rs.getString("borrowDate"), DATE_FORMATTER),
                        LocalDate.parse(rs.getString("dueDate"), DATE_FORMATTER)
                );
                loan.setReturned(rs.getBoolean("returned"));
                String returnDateStr = rs.getString("returnDate");
                if (returnDateStr != null) {
                    loan.setReturnDate(LocalDate.parse(returnDateStr, DATE_FORMATTER));
                }
                loan.setFine(rs.getDouble("fine"));
                loan.setFinePaid(rs.getBoolean("finePaid"));
                return loan;
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving loan: " + e.getMessage());
        }
        return null;
    }

    public void returnBook(String loanId) {
        String sql = "UPDATE loans SET returned = ?, returnDate = ? WHERE loanId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, true);
            pstmt.setString(2, LocalDate.now().format(DATE_FORMATTER));
            pstmt.setString(3, loanId);
            pstmt.executeUpdate();

            Loan loan = getLoanById(loanId);
            if (loan != null) {
                String updateBookSql = "UPDATE books SET isAvailable = ? WHERE isbn = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateBookSql)) {
                    updateStmt.setBoolean(1, true);
                    updateStmt.setString(2, loan.getBookIsbn());
                    updateStmt.executeUpdate();
                    Book book = bookCache.get(loan.getBookIsbn());
                    if (book != null) {
                        book.setAvailable(true);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error returning book: " + e.getMessage());
        }
    }

    public void extendLoan(String loanId, int days) {
        Loan loan = getLoanById(loanId);
        if (loan != null) {
            loan.extendDueDate(days);
            String sql = "UPDATE loans SET dueDate = ? WHERE loanId = ?";
            try (Connection conn = DriverManager.getConnection(DB_URL);
                 PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, loan.getDueDate().format(DATE_FORMATTER));
                pstmt.setString(2, loanId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Error extending loan: " + e.getMessage());
            }
        }
    }

    public void updateLoanFine(String loanId, double fine) {
        String sql = "UPDATE loans SET fine = ? WHERE loanId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, fine);
            pstmt.setString(2, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error updating loan fine: " + e.getMessage());
        }
    }

    public void markFineAsPaid(String loanId) {
        String sql = "UPDATE loans SET finePaid = ? WHERE loanId = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setBoolean(1, true);
            pstmt.setString(2, loanId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error marking fine as paid: " + e.getMessage());
        }
    }

    public String getBookTitleByIsbn(String isbn) {
        Book book = bookCache.get(isbn);
        return book != null ? book.getTitle() : "Unknown Title";
    }

    public String getBookFilePath(String isbn) {
        String sql = "SELECT filePath FROM books WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("filePath");
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving book file path: " + e.getMessage());
        }
        return null;
    }

    public void updateBookFilePath(String isbn, String filePath) {
        String sql = "UPDATE books SET filePath = ? WHERE isbn = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, filePath);
            pstmt.setString(2, isbn);
            pstmt.executeUpdate();
            Book book = bookCache.get(isbn);
            if (book != null) {
                book.setFilePath(filePath);
            }
        } catch (SQLException e) {
            System.err.println("Error updating book file path: " + e.getMessage());
        }
    }

    public boolean canReadBook(String isbn) {
        Book book = bookCache.get(isbn);
        if (book != null) {
            return "Digital".equals(book.getType()) && (book.getFilePath() != null && !book.getFilePath().isEmpty());
        }
        return false;
    }

    public boolean isBookBorrowedByOther(String isbn, String currentUser) {
        String sql = "SELECT borrowerName FROM loans WHERE bookIsbn = ? AND returned = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, isbn);
            pstmt.setBoolean(2, false);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String borrower = rs.getString("borrowerName");
                if (!borrower.equals(currentUser)) {
                    return true;
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking if book is borrowed by other: " + e.getMessage());
        }
        return false;
    }

    public void recordReading(String studentName, String isbn) {
        String sql = "INSERT INTO reading_history (studentName, bookIsbn, readDate) VALUES (?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, studentName);
            pstmt.setString(2, isbn);
            pstmt.setString(3, LocalDate.now().format(DATE_FORMATTER));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Error recording reading: " + e.getMessage());
        }
    }

    public List<String[]> getReadingHistory() {
        List<String[]> history = new ArrayList<>();
        String sql = "SELECT rh.studentName, b.title, rh.readDate " +
                "FROM reading_history rh " +
                "JOIN books b ON rh.bookIsbn = b.isbn " +
                "ORDER BY rh.readDate DESC";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                history.add(new String[]{
                        rs.getString("studentName"),
                        rs.getString("title"),
                        rs.getString("readDate")
                });
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving reading history: " + e.getMessage());
        }
        return history;
    }

    public List<String> getBooksReadByUser(String user) {
        List<String> booksRead = new ArrayList<>();
        String sql = "SELECT DISTINCT bookIsbn FROM reading_history WHERE studentName = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String isbn = rs.getString("bookIsbn");
                String bookTitle = getBookTitleByIsbn(isbn);
                if (bookTitle != null && !bookTitle.equals("Unknown Title")) {
                    booksRead.add(bookTitle);
                }
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving books read by user: " + e.getMessage());
        }
        return booksRead;
    }
}
