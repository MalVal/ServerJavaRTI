package Request;

public class SelectBookRequest implements Request {

    private Integer idBook;
    private String authorLastName;
    private String subjectName;
    private String title;
    private String isbn;
    private Integer publishYear;

    public SelectBookRequest(Integer idBook, String authorLastName, String subjectName, String title, String isbn, Integer publishYear) {
        this.idBook = idBook;
        this.authorLastName = authorLastName;
        this.subjectName = subjectName;
        this.title = title;
        this.isbn = isbn;
        this.publishYear = publishYear;
    }

    public Integer getIdBook() {
        return idBook;
    }

    public void setIdBook(Integer idBook) {
        this.idBook = idBook;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(Integer publishYear) {
        this.publishYear = publishYear;
    }
}
