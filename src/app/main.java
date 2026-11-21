package app;

import java.util.Scanner;
import java.util.List;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import org.mindrot.jbcrypt.BCrypt;

import service.LoginService;
import service.RegistrationService;
import service.BookService;
import service.bookIssueService;
import service.memberService;

import dao.userDAO;

import model.user;
import model.book;
import model.bookissue;
import model.member;

import util.Session;

public class main {

    private static final LoginService loginService =
            new LoginService();

    private static final RegistrationService registrationService =
            new RegistrationService();

    private static final BookService bookService =
            new BookService();

    private static final bookIssueService bookIssueService =
            new bookIssueService();

    private static final memberService memberService =
            new memberService();

    private static final userDAO userDAO =
            new userDAO();

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        boolean running = true;

        while (running) {

            System.out.println("\n================================");
            System.out.println("   LIBRARY MANAGEMENT SYSTEM");
            System.out.println("================================");

            System.out.println("1. Login");
            System.out.println("2. Register");
            System.out.println("3. Exit");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input! Enter a number.");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // Login
                case 1: {

                    System.out.print("Enter username: ");
                    String username = sc.nextLine().trim();

                    System.out.print("Enter password: ");
                    String password = sc.nextLine();

                    user loggedInUser =
                            loginService.login(username, password);

                    if (loggedInUser != null) {

                        System.out.println(
                                "Welcome, " + loggedInUser.getUsername()
                        );

                        switch (loggedInUser.getRole()) {

                            case "ADMIN":
                                showAdminMenu(sc);
                                break;

                            case "LIBRARIAN":
                                showLibrarianMenu(sc);
                                break;

                            case "MEMBER":
                                showMemberMenu(sc);
                                break;

                            default:
                                System.out.println("Invalid user role!");
                                Session.logout();
                        }
                    }

                    break;
                }

                // Member Registration
                case 2: {

                    System.out.print("Enter your name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter phone number: ");
                    String phone = sc.nextLine().trim();

                    System.out.print("Enter email: ");
                    String email = sc.nextLine().trim();

                    System.out.print("Create username: ");
                    String newUsername = sc.nextLine().trim();

                    System.out.print("Create password: ");
                    String newPassword = sc.nextLine();

                    user registeredUser =
                            registrationService.registerMember(
                                    name,
                                    phone,
                                    email,
                                    newUsername,
                                    newPassword
                            );

                    if (registeredUser != null) {

                        System.out.println(
                                "Member registration completed successfully!"
                        );

                        System.out.println(
                                "Your User ID: "
                                        + registeredUser.getUserId()
                        );

                    } else {

                        System.out.println(
                                "Registration failed. Please try again."
                        );
                    }

                    break;
                }

                // Exit
                case 3:

                    System.out.println(
                            "Thank you for using our system!"
                    );

                    running = false;
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }

        sc.close();
    }

    // Display book details
    private static void displayBooks(List<book> books) {

        if (books == null || books.isEmpty()) {
            System.out.println("No books found!");
            return;
        }

        for (book b : books) {

            System.out.println("\nBook ID: " + b.getBookId());
            System.out.println("Book Name: " + b.getBookName());
            System.out.println("Author: " + b.getAuthor());
            System.out.println("Price: ₹" + b.getPrice());
            System.out.println("Total Copies: " + b.getTotalCopies());

            System.out.println(
                    "Available Copies: " + b.getAvailableCopies()
            );

            System.out.println("--------------------------------");
        }
    }

    // Display member details
    private static void displayMembers(List<member> members) {

        if (members == null || members.isEmpty()) {
            System.out.println("No members found!");
            return;
        }

        for (member m : members) {

            System.out.println("\nMember ID: " + m.getMemberId());
            System.out.println("Name: " + m.getMemberName());
            System.out.println("Phone: " + m.getPhone());
            System.out.println("Email: " + m.getEmail());
            System.out.println("--------------------------------");
        }
    }

    // Read a positive integer
    private static int readPositiveInt(
            Scanner sc, String message) {

        System.out.print(message);

        if (!sc.hasNextInt()) {
            System.out.println("Invalid number!");
            sc.nextLine();
            return -1;
        }

        int value = sc.nextInt();
        sc.nextLine();

        if (value <= 0) {
            System.out.println("Enter a positive number!");
            return -1;
        }

        return value;
    }

    // Member Menu
    private static void showMemberMenu(Scanner sc) {

        boolean memberRunning = true;

        while (memberRunning) {

            System.out.println("\n================================");
            System.out.println("          MEMBER MENU");
            System.out.println("================================");

            System.out.println("1. View All Available Books");
            System.out.println("2. Search Book by Name");
            System.out.println("3. Search Book by Author");
            System.out.println("4. View My Issued Books");
            System.out.println("5. View My Fine Details");
            System.out.println("6. Logout");
            System.out.println("7. Issue a Book");
            System.out.println("8. Return a Book");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input!");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // View Available Books
                case 1:

                    displayBooks(bookService.getAvailableBooks());
                    break;

                // Search Book by Name
                case 2: {

                    System.out.print("Enter book name: ");
                    String name = sc.nextLine().trim();

                    displayBooks(bookService.searchByName(name));
                    break;
                }

                // Search Book by Author
                case 3: {

                    System.out.print("Enter author name: ");
                    String author = sc.nextLine().trim();

                    displayBooks(bookService.searchByAuthor(author));
                    break;
                }

                // View My Issued Books
                case 4: {

                    user currentUser = Session.getCurrentUser();

                    if (currentUser == null
                            || currentUser.getMemberId() == null) {

                        System.out.println("Member session not found!");
                        break;
                    }

                    List<bookissue> issues =
                            bookIssueService.getIssuesByMemberId(
                                    currentUser.getMemberId()
                            );

                    if (issues.isEmpty()) {

                        System.out.println(
                                "You have no book issue records."
                        );

                        break;
                    }

                    System.out.println(
                            "\n========== MY BOOK ISSUE HISTORY =========="
                    );

                    for (bookissue issue : issues) {

                        System.out.println(
                                "Issue ID: " + issue.getIssueId()
                        );

                        System.out.println(
                                "Book Name: " + issue.getBookName()
                        );

                        System.out.println(
                                "Author: " + issue.getAuthor()
                        );

                        System.out.println(
                                "Issue Date: " + issue.getIssueDate()
                        );

                        System.out.println(
                                "Due Date: " + issue.getDueDate()
                        );

                        if (issue.getReturnDate() == null) {

                            System.out.println(
                                    "Return Date: Not returned"
                            );

                            System.out.println(
                                    "Status: Currently Issued"
                            );

                        } else {

                            System.out.println(
                                    "Return Date: " + issue.getReturnDate()
                            );

                            System.out.println("Status: Returned");
                        }

                        System.out.println("Fine: ₹" + issue.getFine());
                        System.out.println("--------------------------------");
                    }

                    break;
                }

                // View My Fine Details
                case 5: {

                    user currentUser = Session.getCurrentUser();

                    if (currentUser == null
                            || currentUser.getMemberId() == null) {

                        System.out.println("Member session not found!");
                        break;
                    }

                    List<bookissue> issues =
                            bookIssueService.getIssuesByMemberId(
                                    currentUser.getMemberId()
                            );

                    if (issues.isEmpty()) {

                        System.out.println("You have no fine records!");
                        break;
                    }

                    BigDecimal totalFine = BigDecimal.ZERO;

                    System.out.println(
                            "\n========== MY FINE DETAILS =========="
                    );

                    for (bookissue issue : issues) {

                        BigDecimal fine = issue.getFine() == null
                                ? BigDecimal.ZERO
                                : issue.getFine();

                        totalFine = totalFine.add(fine);

                        System.out.println(
                                "Issue ID: " + issue.getIssueId()
                        );

                        System.out.println(
                                "Book Name: " + issue.getBookName()
                        );

                        System.out.println(
                                "Due Date: " + issue.getDueDate()
                        );

                        if (issue.getReturnDate() != null) {

                            System.out.println(
                                    "Return Date: " + issue.getReturnDate()
                            );

                            long lateDays = 0;

                            if (issue.getReturnDate().isAfter(
                                    issue.getDueDate())) {

                                lateDays = ChronoUnit.DAYS.between(
                                        issue.getDueDate(),
                                        issue.getReturnDate()
                                );
                            }

                            System.out.println("Late Days: " + lateDays);
                            System.out.println("Fine Amount: ₹" + fine);

                        } else {

                            System.out.println(
                                    "Return Date: Not returned"
                            );

                            System.out.println(
                                    "Fine Status: Pending until book return"
                            );
                        }

                        System.out.println("--------------------------------");
                    }

                    System.out.println("TOTAL FINE: ₹" + totalFine);
                    break;
                }

                // Logout
                case 6:

                    Session.logout();
                    memberRunning = false;
                    break;

                // Issue a Book
                case 7: {

                    user currentUser = Session.getCurrentUser();

                    if (currentUser == null
                            || currentUser.getMemberId() == null) {

                        System.out.println("Member session not found!");
                        break;
                    }

                    displayBooks(bookService.getAvailableBooks());

                    int bookId = readPositiveInt(
                            sc, "Enter Book ID to issue: "
                    );

                    if (bookId == -1) {
                        break;
                    }

                    boolean success = bookIssueService.issueBook(
                            bookId,
                            currentUser.getMemberId()
                    );

                    if (success) {

                        System.out.println(
                                "Book issue completed successfully!"
                        );

                        System.out.println(
                                "Due date: " + LocalDate.now().plusDays(7)
                        );
                    }

                    break;
                }

                // Return a Book
                case 8: {

                    user currentUser = Session.getCurrentUser();

                    if (currentUser == null
                            || currentUser.getMemberId() == null) {

                        System.out.println("Member session not found!");
                        break;
                    }

                    List<bookissue> issues =
                            bookIssueService.getIssuesByMemberId(
                                    currentUser.getMemberId()
                            );

                    boolean hasActiveBooks = false;

                    System.out.println(
                            "\n========== BOOKS AVAILABLE FOR RETURN =========="
                    );

                    for (bookissue issue : issues) {

                        if (issue.getReturnDate() == null) {

                            hasActiveBooks = true;

                            System.out.println(
                                    "Issue ID: " + issue.getIssueId()
                            );

                            System.out.println(
                                    "Book Name: " + issue.getBookName()
                            );

                            System.out.println(
                                    "Due Date: " + issue.getDueDate()
                            );

                            System.out.println("--------------------------------");
                        }
                    }

                    if (!hasActiveBooks) {

                        System.out.println(
                                "You have no books to return!"
                        );

                        break;
                    }

                    int issueId = readPositiveInt(
                            sc, "Enter Issue ID to return: "
                    );

                    if (issueId == -1) {
                        break;
                    }

                    boolean returned = bookIssueService.returnBook(
                            issueId,
                            currentUser.getMemberId()
                    );

                    if (returned) {
                        System.out.println(
                                "Return process completed successfully!"
                        );
                    }

                    break;
                }

                default:
                    System.out.println("Invalid choice!");
            }
        }
    } 
    // Admin Menu
    private static void showAdminMenu(Scanner sc) {

        boolean adminRunning = true;

        while (adminRunning) {

            System.out.println("\n================================");
            System.out.println("           ADMIN MENU");
            System.out.println("================================");

            System.out.println("1. Add Book");
            System.out.println("2. View All Books");
            System.out.println("3. Search Book by Name");
            System.out.println("4. Search Book by Author");
            System.out.println("5. Update Book");
            System.out.println("6. Delete Book");
            System.out.println("7. View All Members");
            System.out.println("8. Search Member by Name");
            System.out.println("9. Update Member");
            System.out.println("10. Delete Member");
            System.out.println("11. Create Librarian Account");
            System.out.println("12. Logout");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input!");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // Add Book
                case 1: {

                    System.out.print("Enter book name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter author: ");
                    String author = sc.nextLine().trim();

                    System.out.print("Enter price: ");

                    BigDecimal price;

                    try {
                        price = new BigDecimal(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price!");
                        break;
                    }

                    int copies = readPositiveInt(
                            sc, "Enter total copies: "
                    );

                    if (copies == -1) {
                        break;
                    }

                    book newBook = new book(
                            0,
                            name,
                            author,
                            price,
                            copies,
                            copies
                    );

                    if (bookService.addBook(newBook)) {
                        System.out.println("Book added successfully!");
                    } else {
                        System.out.println("Unable to add book!");
                    }

                    break;
                }

                // View All Books
                case 2:

                    displayBooks(bookService.getAllBooks());
                    break;

                // Search Book by Name
                case 3: {

                    System.out.print("Enter book name: ");
                    String name = sc.nextLine().trim();

                    displayBooks(bookService.searchByName(name));
                    break;
                }

                // Search Book by Author
                case 4: {

                    System.out.print("Enter author name: ");
                    String author = sc.nextLine().trim();

                    displayBooks(bookService.searchByAuthor(author));
                    break;
                }

                // Update Book
                case 5: {

                    int bookId = readPositiveInt(
                            sc, "Enter Book ID: "
                    );

                    if (bookId == -1) {
                        break;
                    }

                    book existingBook = null;

                    for (book b : bookService.getAllBooks()) {

                        if (b.getBookId() == bookId) {
                            existingBook = b;
                            break;
                        }
                    }

                    if (existingBook == null) {
                        System.out.println("Book not found!");
                        break;
                    }

                    System.out.print("Enter new book name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter new author: ");
                    String author = sc.nextLine().trim();

                    System.out.print("Enter new price: ");

                    BigDecimal price;

                    try {
                        price = new BigDecimal(sc.nextLine().trim());
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid price!");
                        break;
                    }

                    int totalCopies = readPositiveInt(
                            sc, "Enter new total copies: "
                    );

                    if (totalCopies == -1) {
                        break;
                    }

                    int issuedCopies =
                            existingBook.getTotalCopies()
                            - existingBook.getAvailableCopies();

                    if (totalCopies < issuedCopies) {

                        System.out.println(
                                "Total copies cannot be less than issued copies!"
                        );

                        break;
                    }

                    int availableCopies =
                            totalCopies - issuedCopies;

                    book updatedBook = new book(
                            bookId,
                            name,
                            author,
                            price,
                            totalCopies,
                            availableCopies
                    );

                    if (bookService.updateBook(updatedBook)) {
                        System.out.println("Book updated successfully!");
                    } else {
                        System.out.println("Unable to update book!");
                    }

                    break;
                }

                // Delete Book
                case 6: {

                    int bookId = readPositiveInt(
                            sc, "Enter Book ID to delete: "
                    );

                    if (bookId == -1) {
                        break;
                    }

                    System.out.print(
                            "Are you sure you want to delete? (yes/no): "
                    );

                    String confirm = sc.nextLine().trim();

                    if (confirm.equalsIgnoreCase("yes")) {

                        if (bookService.deleteBook(bookId)) {
                            System.out.println(
                                    "Book deleted successfully!"
                            );
                        } else {
                            System.out.println(
                                    "Unable to delete book. "
                                    + "It may have issue records."
                            );
                        }

                    } else {
                        System.out.println("Delete cancelled.");
                    }

                    break;
                }

                // View All Members
                case 7:

                    displayMembers(memberService.getAllMembers());
                    break;

                // Search Member by Name
                case 8: {

                    System.out.print("Enter member name: ");
                    String name = sc.nextLine().trim();

                    displayMembers(
                            memberService.searchMembersByName(name)
                    );

                    break;
                }

                // Update Member
                case 9: {

                    int memberId = readPositiveInt(
                            sc, "Enter Member ID: "
                    );

                    if (memberId == -1) {
                        break;
                    }

                    member existingMember =
                            memberService.getMemberById(memberId);

                    if (existingMember == null) {
                        System.out.println("Member not found!");
                        break;
                    }

                    System.out.print("Enter new member name: ");
                    String name = sc.nextLine().trim();

                    System.out.print("Enter new phone number: ");
                    String phone = sc.nextLine().trim();

                    System.out.print("Enter new email: ");
                    String email = sc.nextLine().trim();

                    member updatedMember = new member(
                            memberId,
                            name,
                            phone,
                            email
                    );

                    if (memberService.updateMember(updatedMember)) {
                        System.out.println(
                                "Member updated successfully!"
                        );
                    } else {
                        System.out.println(
                                "Unable to update member. "
                                + "Check the details."
                        );
                    }

                    break;
                }

                // Delete Member
                case 10: {

                    int memberId = readPositiveInt(
                            sc, "Enter Member ID to delete: "
                    );

                    if (memberId == -1) {
                        break;
                    }

                    member existingMember =
                            memberService.getMemberById(memberId);

                    if (existingMember == null) {
                        System.out.println("Member not found!");
                        break;
                    }

                    System.out.println(
                            "Member: " + existingMember.getMemberName()
                    );

                    System.out.print(
                            "Are you sure you want to delete? (yes/no): "
                    );

                    String confirm = sc.nextLine().trim();

                    if (confirm.equalsIgnoreCase("yes")) {

                        if (memberService.deleteMember(memberId)) {

                            System.out.println(
                                    "Member and linked login account deleted."
                            );

                        } else {

                            System.out.println(
                                    "Member could not be deleted. "
                                    + "Borrowing history may exist."
                            );
                        }

                    } else {
                        System.out.println("Delete cancelled.");
                    }

                    break;
                }

                // Create Librarian Account
                case 11: {

                    System.out.print("Enter librarian username: ");
                    String username = sc.nextLine().trim();

                    if (!username.matches("^[A-Za-z0-9_.@]+$")) {
                        System.out.println("Invalid username!");
                        break;
                    }

                    if (userDAO.getUserByUsername(username) != null) {
                        System.out.println(
                                "Username already exists!"
                        );
                        break;
                    }

                    System.out.print("Enter librarian password: ");
                    String password = sc.nextLine();

                    if (password.length() < 8) {
                        System.out.println(
                                "Password must contain at least 8 characters!"
                        );
                        break;
                    }

                    System.out.print("Confirm password: ");
                    String confirmPassword = sc.nextLine();

                    if (!password.equals(confirmPassword)) {
                        System.out.println("Passwords do not match!");
                        break;
                    }

                    String passwordHash = BCrypt.hashpw(
                            password,
                            BCrypt.gensalt()
                    );

                    user librarian = new user(
                            0,
                            username,
                            passwordHash,
                            "LIBRARIAN",
                            null
                    );

                    if (userDAO.addUser(librarian)) {

                        System.out.println(
                                "Librarian account created successfully!"
                        );

                        System.out.println(
                                "Username: " + username
                        );

                    } else {

                        System.out.println(
                                "Unable to create librarian account!"
                        );
                    }

                    break;
                }

                // Logout
                case 12:

                    Session.logout();
                    adminRunning = false;
                    break;

                default:
                    System.out.println("Invalid choice!");
            }
        }
    } 
    // Librarian Menu
    private static void showLibrarianMenu(Scanner sc) {

        boolean librarianRunning = true;

        while (librarianRunning) {

            System.out.println("\n================================");
            System.out.println("         LIBRARIAN MENU");
            System.out.println("================================");

            System.out.println("1. View Available Books");
            System.out.println("2. Search Book by Name");
            System.out.println("3. Search Book by Author");
            System.out.println("4. View All Members");
            System.out.println("5. Issue Book to Member");
            System.out.println("6. Return Book for Member");
            System.out.println("7. Logout");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid input!");
                sc.nextLine();
                continue;
            }

            int choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                // View Available Books
                case 1:

                    displayBooks(bookService.getAvailableBooks());
                    break;

                // Search Book by Name
                case 2: {

                    System.out.print("Enter book name: ");
                    String name = sc.nextLine().trim();

                    displayBooks(bookService.searchByName(name));
                    break;
                }

                // Search Book by Author
                case 3: {

                    System.out.print("Enter author name: ");
                    String author = sc.nextLine().trim();

                    displayBooks(bookService.searchByAuthor(author));
                    break;
                }

                // View All Members
                case 4:

                    displayMembers(memberService.getAllMembers());
                    break;

                // Issue Book to Member
                case 5: {

                    int memberId = readPositiveInt(
                            sc, "Enter Member ID: "
                    );

                    if (memberId == -1) {
                        break;
                    }

                    member selectedMember =
                            memberService.getMemberById(memberId);

                    if (selectedMember == null) {
                        System.out.println("Member not found!");
                        break;
                    }

                    System.out.println(
                            "Member Name: "
                                    + selectedMember.getMemberName()
                    );

                    displayBooks(bookService.getAvailableBooks());

                    int bookId = readPositiveInt(
                            sc, "Enter Book ID to issue: "
                    );

                    if (bookId == -1) {
                        break;
                    }

                    boolean success = bookIssueService.issueBook(
                            bookId,
                            memberId
                    );

                    if (success) {

                        System.out.println(
                                "Book issued successfully!"
                        );

                        System.out.println(
                                "Due date: " + LocalDate.now().plusDays(7)
                        );
                    }

                    break;
                }

                // Return Book for Member
                case 6: {

                    int memberId = readPositiveInt(
                            sc, "Enter Member ID: "
                    );

                    if (memberId == -1) {
                        break;
                    }

                    member selectedMember =
                            memberService.getMemberById(memberId);

                    if (selectedMember == null) {
                        System.out.println("Member not found!");
                        break;
                    }

                    List<bookissue> issues =
                            bookIssueService.getIssuesByMemberId(memberId);

                    boolean hasActiveBooks = false;

                    System.out.println(
                            "\n========== ACTIVE ISSUES =========="
                    );

                    for (bookissue issue : issues) {

                        if (issue.getReturnDate() == null) {

                            hasActiveBooks = true;

                            System.out.println(
                                    "Issue ID: " + issue.getIssueId()
                            );

                            System.out.println(
                                    "Book Name: " + issue.getBookName()
                            );

                            System.out.println(
                                    "Due Date: " + issue.getDueDate()
                            );

                            System.out.println("--------------------------------");
                        }
                    }

                    if (!hasActiveBooks) {

                        System.out.println(
                                "This member has no books to return!"
                        );

                        break;
                    }

                    int issueId = readPositiveInt(
                            sc, "Enter Issue ID to return: "
                    );

                    if (issueId == -1) {
                        break;
                    }

                    boolean returned = bookIssueService.returnBook(
                            issueId,
                            memberId
                    );

                    if (returned) {
                        System.out.println(
                                "Book returned successfully!"
                        );
                    }

                    break;
                }

                // Logout
                case 7:

                    Session.logout();
                    librarianRunning = false;
                    break;

                default:

                    System.out.println("Invalid choice!");
            }
        }
    }
}

                        