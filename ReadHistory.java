import java.time.LocalDate;
import java.io.Serializable;

public class ReadHistory implements Serializable {
    private String bookTitle;
    private String reader;
    private LocalDate readDate;

    public ReadHistory(String bookTitle, String reader, LocalDate readDate) {
        this.bookTitle = bookTitle;
        this.reader = reader;
        this.readDate = readDate;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public String getReader() {
        return reader;
    }

    public LocalDate getReadDate() {
        return readDate;
    }
}
