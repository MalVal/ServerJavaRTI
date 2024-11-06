package Client.Controller;

public interface ClientInterface {
    void connection(String lastname, String firstname, Boolean isNew);
    void retrieveBooks(Integer idBook, String authorLastName, String subjectName, String title, String isbn, Integer publishYear);
    void addToCaddy(Integer idBook, Integer quantity);
}
