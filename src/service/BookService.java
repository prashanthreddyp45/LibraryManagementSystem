package service;

import dao.bookDAO;
import model.book;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BookService {

    private final bookDAO dao = new bookDAO();

    // Add a book
    public boolean addBook(book book) {

        if (!isValidBook(book)) {
            System.out.println("Invalid book details!");
            return false;
        }

        return dao.addBook(book);
    }

    // View all books
    public List<book> getAllBooks() {

        return dao.getAllBooks();
    }

    // View only available books
    public List<book> getAvailableBooks() {

        List<book> books = dao.getAllBooks();

        List<book> availableBooks = new ArrayList<>();

        for (book b : books) {

            if (b.getAvailableCopies() > 0) {
                availableBooks.add(b);
            }
        }

        return availableBooks;
    }

    // Search books by name
    public List<book> searchByName(String name) {

        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return dao.searchBooksByName(name.trim());
    }

    // Search books by author
    public List<book> searchByAuthor(String author) {

        if (author == null || author.trim().isEmpty()) {
            return new ArrayList<>();
        }

        return dao.searchBooksByAuthor(author.trim());
    }

    // Update a book
    public boolean updateBook(book book) {

        if (book == null || book.getBookId() <= 0
                || !isValidBook(book)) {
            System.out.println("Invalid book details!");
            return false;
        }

        return dao.updateBook(book);
    }

    // Delete a book
    public boolean deleteBook(int bookId) {

        if (bookId <= 0) {
            System.out.println("Invalid book ID!");
            return false;
        }

        return dao.deleteBook(bookId);
    }

    // Validate book details
    private boolean isValidBook(book book) {

        if (book == null) {
            return false;
        }

        String name = book.getBookName();

        if (name == null || name.trim().isEmpty()) {
            return false;
        }

        String author = book.getAuthor();

        if (author == null || author.trim().isEmpty()) {
            return false;
        }

        BigDecimal price = book.getPrice();

        if (price == null
                || price.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }

        if (book.getTotalCopies() <= 0) {
            return false;
        }

        if (book.getAvailableCopies() < 0) {
            return false;
        }

        if (book.getAvailableCopies()
                > book.getTotalCopies()) {
            return false;
        }

        return true;
    }
}