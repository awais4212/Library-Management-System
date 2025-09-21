import java.io.*;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.*;

// Book Class
class Book implements Serializable {
    private static final long serialVersionUID = 1L;
    private int bookId;
    private String title;
    private String author;
    private boolean isIssued;
    private LocalDate issueDate;

    public Book(int bookId, String title, String author) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.isIssued = false;
    }

    public void issueBook(LocalDate date) {
        isIssued = true;
        issueDate = date;
    }

    public void returnBook() {
        isIssued = false;
        issueDate = null;
    }

    public boolean isAvailable() {
        return !isIssued;
    }

    public String getTitle() {
        return title;
    }

    public int getBookId() {
        return bookId;
    }

    public String getAuthor() {
        return author;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void displayBookDetails() {
        System.out.println("ID: " + bookId + ", Title: " + title + ", Author: " + author + ", Available: " + (isIssued ? "No" : "Yes"));
    }
}

// Abstract User Class
abstract class User implements Serializable {
    private static final long serialVersionUID = 1L;
    protected int userId;
    protected String name;
    protected String userType;
    protected int maxBooks;
    protected int maxDays;
    protected List<Book> issuedBooks;

    public User(int userId, String name, String userType, int maxBooks, int maxDays) {
        this.userId = userId;
        this.name = name;
        this.userType = userType;
        this.maxBooks = maxBooks;
        this.maxDays = maxDays;
        this.issuedBooks = new ArrayList<>();
    }

    public boolean canIssueMoreBooks() {
        return issuedBooks.size() < maxBooks;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public abstract void viewIssuedBooks();
}

// Learner Class
class Learner extends User {
    public Learner(int userId, String name) {
        super(userId, name, "Learner", 2, 5);
    }

    @Override
    public void viewIssuedBooks() {
        System.out.println(name + " has issued the following books:");
        for (Book book : issuedBooks) {
            book.displayBookDetails();
        }
    }
}

// Instructor Class
class Instructor extends User {
    public Instructor(int userId, String name) {
        super(userId, name, "Instructor", 5, 10);
    }

    @Override
    public void viewIssuedBooks() {
        System.out.println(name + " has issued the following books:");
        for (Book book : issuedBooks) {
            book.displayBookDetails();
        }
    }
}

// Library Class
class Library {
    private List<Book> books = new ArrayList<>();
    private List<User> users = new ArrayList<>();
    private String[] quotes = {
            "A room without books is like a body without a soul.",
            "You’ll meet a character today who changes your perspective.",
            "Remember, every great reader started with a single page.",
            "Reading is to the mind what exercise is to the body.",
            "Books are uniquely portable magic.",
            "The only thing you absolutely have to know is the location of the library.",
            "So many books, so little time."
    };

    public void addBook(Book book) {
        books.add(book);
    }

    public void addUser(User user) {
        users.add(user);
    }

    public void issueBook(int bookId, int userId) {
        User user = getUserById(userId);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        if (!user.canIssueMoreBooks()) {
            System.out.println(user.name + " has reached the book issue limit.");
            return;
        }

        for (Book book : books) {
            if (bookId == book.getBookId() && book.isAvailable()) {
                book.issueBook(LocalDate.now());
                user.issuedBooks.add(book);
                System.out.println("Book issued successfully to " + user.name + ".");
                return;
            }
        }
        System.out.println("Book not available.");
    }

    public void returnBook(int bookId, int userId) {
        User user = getUserById(userId);
        if (user == null) {
            System.out.println("User not found.");
            return;
        }

        for (Book book : user.issuedBooks) {
            if (bookId == book.getBookId()) {
                LocalDate issueDate = book.getIssueDate();
                long daysIssued = ChronoUnit.DAYS.between(issueDate, LocalDate.now());
                if (daysIssued > user.getMaxDays()) {
                    System.out.println("Return overdue by " + (daysIssued - user.getMaxDays()) + " days. Fine applied: $10.");
                }
                book.returnBook();
                user.issuedBooks.remove(book);
                System.out.println("Book returned successfully by " + user.name + ".");
                return;
            }
        }
        System.out.println("Book not issued by user.");
    }

    public void displayAvailableBooks() {
        System.out.println("Available Books:");
        for (Book book : books) {
            if (book.isAvailable()) {
                book.displayBookDetails();
            }
        }
    }

    public void showDailyQuote() {
        int randomIndex = new Random().nextInt(quotes.length);
        System.out.println("Quote of the Day: " + quotes[randomIndex]);
    }

    public void saveDataToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("library_data.dat"))) {
            oos.writeObject(books);
            oos.writeObject(users);
            System.out.println("Library data saved successfully.");
        } catch (IOException e) {
            System.out.println("Error saving data: " + e.getMessage());
        }
    }

    public void loadDataFromFile() {
        File file = new File("library_data.dat");
        if (!file.exists()) {
            System.out.println("No saved data found. Starting with an empty library.");
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            books = (List<Book>) ois.readObject();
            users = (List<User>) ois.readObject();
            System.out.println("Library data loaded successfully.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading data: " + e.getMessage());
        }
    }

    User getUserById(int userId) {
        for (User user : users) {
            if (user.userId == userId) {
                return user;
            }
        }
        return null;
    }
}

// Main Class
public class LibraryManagementSystem {
    public static void main(String[] args) {
        Library library = new Library();

        // Adding default users
        library.addUser(new Learner(1, "Alice"));
        library.addUser(new Instructor(2, "Bob"));

        library.loadDataFromFile();
        library.showDailyQuote();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\nLibrary Menu:");
            System.out.println("1. Log in as Learner");
            System.out.println("2. Log in as Instructor");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            if (choice == 3) {
                library.saveDataToFile();
                System.out.println("Goodbye!");
                break;
            }

            System.out.print("Enter User ID: ");
            int userId = scanner.nextInt();
            User currentUser = library.getUserById(userId);

            if (currentUser == null || (choice == 1 && !(currentUser instanceof Learner)) || (choice == 2 && !(currentUser instanceof Instructor))) {
                System.out.println("Invalid User ID or Role.");
                continue;
            }

            while (true) {
                System.out.println("--------------------------------");
                System.out.println("|  LIBRARY MANAGEMENT SYSTEM   |");
                System.out.println("--------------------------------");
                System.out.println("|              Menu            |");
                System.out.println("--------------------------------");
                System.out.println("|1. Add Book                   |");
                System.out.println("|2. Issue Book                 |");
                System.out.println("|3. Return Book                |");
                System.out.println("|4. View Issued Books          |");
                System.out.println("|5. Display Available Books    |");
                System.out.println("|6. Save Data                  |");
                System.out.println("|7. Load Data                  |");
                System.out.println("|8. Logout                     |");
                System.out.println("|______________________________|");
                System.out.print("Choose an option: ");

                int userChoice = scanner.nextInt();
                switch (userChoice) {
                    case 1:
                        System.out.print("Enter Book ID: ");
                        int bookId = scanner.nextInt();
                        scanner.nextLine();  // Consume newline
                        System.out.print("Enter Title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter Author: ");
                        String author = scanner.nextLine();
                        library.addBook(new Book(bookId, title, author));
                        System.out.println("Book added successfully.");
                        break;
                    case 2:
                        System.out.print("Enter Book ID to issue: ");
                        bookId = scanner.nextInt();
                        library.issueBook(bookId, userId);
                        break;
                    case 3:
                        System.out.print("Enter Book ID to return: ");
                        bookId = scanner.nextInt();
                        library.returnBook(bookId, userId);
                        break;
                    case 4:
                        currentUser.viewIssuedBooks();
                        break;
                    case 5:
                        library.displayAvailableBooks();
                        break;
                    case 6:
                        library.saveDataToFile();
                        System.out.println("Data saved successfully.");
                        break;
                    case 7:
                        library.loadDataFromFile();
                        System.out.println("Data loaded successfully.");
                        break;
                    case 8:
                        System.out.println("Logged out. See you next time!");
                        break;
                    default:
                        System.out.println("Invalid option. Try again.");
                }

                if (userChoice == 8) break;
            }
        }
    }
}
