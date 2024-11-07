package Client.Controller;

public interface ClientInterface {
    void connection(String lastname, String firstname, Boolean isNew);
    void retrieveBooks(Integer idBook, String authorLastName, String subjectName, String title, String isbn, Integer publishYear);
    void retrieveCaddy();
    void addToCaddy(Integer idBook, Integer quantity);
    void removeFromCaddy(Integer idBook, Integer quantity);
    void cancelCaddy();
    void payCaddy();
}
