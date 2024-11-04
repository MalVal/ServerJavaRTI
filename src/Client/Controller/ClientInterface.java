package Client.Controller;

import Common.Model.SearchViewModel.BookSearchVM;

public interface ClientInterface {
    void connection(String lastname, String firstname, Boolean isNew);
    void retreiveBooks(Integer idBook, String authorLastName, String subjectName, String title, String isbn, Integer publishYear);
}
