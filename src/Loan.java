import java.time.LocalDate;

public class Loan {
    private String loanId;
    private String bookIsbn;
    private String borrowerName;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean isReturned;
    private double fine;

    public Loan(String loanId, String bookIsbn, String borrowerName, LocalDate borrowDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.bookIsbn = bookIsbn;
        this.borrowerName = borrowerName;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.isReturned = false;
        this.fine = 0.0;
    }

    public String getLoanId() {
        return loanId;
    }

    public String getBookIsbn() {
        return bookIsbn;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return isReturned;
    }

    public double getFine() {
        return fine;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public void setReturned(boolean returned) {
        isReturned = returned;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }
}