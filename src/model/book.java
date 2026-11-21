package model;

import java.math.BigDecimal;

public class book {

    private int bookId;
    private String bookName;
    private String author;
    private BigDecimal price;
    private int totalCopies;
    private int availableCopies;

    // Constructor
    public book(int bookId, String bookName, String author,
                BigDecimal price, int totalCopies, int availableCopies) {

        this.bookId = bookId;
        this.bookName = bookName;
        this.author = author;
        this.price = price;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
    }

    // Getters
    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthor() {
        return author;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getTotalCopies() {
        return totalCopies;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    // Setters
    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public void setTotalCopies(int totalCopies) {
        this.totalCopies = totalCopies;
    }

    public void setAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }
}