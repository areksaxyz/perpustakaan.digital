public class Book {
    private String isbn;
    private String title;
    private String author;
    private int year;
    private String subject;
    private String type;
    private String filePath;
    private boolean isAvailable;

    public Book(String isbn, String title, String author, int year, String subject, String type, String filePath) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.subject = subject;
        this.type = type;
        this.filePath = filePath;
        this.isAvailable = true;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getYear() {
        return year;
    }

    public String getSubject() {
        return subject;
    }

    public String getType() {
        return type;
    }

    public String getFilePath() {
        return filePath;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        this.isAvailable = available;
    }
}