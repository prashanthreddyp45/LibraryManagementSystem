package service;

import dao.bookIssueDAO;
import model.bookissue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class bookIssueService {

    private final bookIssueDAO dao = new bookIssueDAO();

    // Issue a book
    public boolean issueBook(int bookId, int memberId) {

        if (bookId <= 0 || memberId <= 0) {
            System.out.println("Invalid book ID or member ID!");
            return false;
        }

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(7);

        bookissue issue = new bookissue(
                0,
                bookId,
                memberId,
                issueDate,
                dueDate,
                null,
                BigDecimal.ZERO
        );

        return dao.issueBook(issue);
    }

    // View all issue records for one member
    public List<bookissue> getIssuesByMemberId(int memberId) {

        if (memberId <= 0) {
            return List.of();
        }

        return dao.getIssuesWithBookDetails(memberId);
    }

    // Return a book
    public boolean returnBook(int issueId, int memberId) {

        if (issueId <= 0 || memberId <= 0) {
            System.out.println(
                    "Invalid issue ID or member ID!"
            );
            return false;
        }

        return dao.returnBook(
                issueId,
                memberId,
                LocalDate.now()
        );
    }
}


// changr