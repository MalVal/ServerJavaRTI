package Request;

import Model.SearchViewModel.BookSearchVM;

public class SelectBookRequest implements Request {

    private BookSearchVM bookSearchVM;

    public SelectBookRequest(BookSearchVM bookSearchVM) {
        this.bookSearchVM = bookSearchVM;
    }

    public BookSearchVM getBookSearchVM() {
        return bookSearchVM;
    }

    public void setBookSearchVM(BookSearchVM bookSearchVM) {
        this.bookSearchVM = bookSearchVM;
    }
}
