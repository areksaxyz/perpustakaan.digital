import java.time.LocalDate;

public class Loan {
    private String loanId;
    private String bookIsbn;
    private String borrowerName;
    private String borrowerClass;
    private String nim;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private boolean returned;
    private double fine;
    private boolean finePaid;

    public Loan(String loanId, String bookIsbn, String borrowerName, String borrowerClass, String nim, LocalDate borrowDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.bookIsbn = bookIsbn;
        this.borrowerName = borrowerName;
        this.borrowerClass = borrowerClass;
        this.nim = nim;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = false;
        this.returnDate = null;
        this.fine = 0.0;
        this.finePaid = false;
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

    public String getBorrowerClass() {
        return borrowerClass;
    }

    public void setBorrowerClass(String borrowerClass) {
        this.borrowerClass = borrowerClass;
    }

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public boolean isFinePaid() {
        return finePaid;
    }

    public void setFinePaid(boolean finePaid) {
        this.finePaid = finePaid;
    }

    public void extendDueDate(int days) {
        this.dueDate = this.dueDate.plusDays(days);
    }
}
