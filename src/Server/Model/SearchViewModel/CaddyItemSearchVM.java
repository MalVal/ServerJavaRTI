package Server.Model.SearchViewModel;

import java.io.Serializable;

public class CaddyItemSearchVM implements Serializable {

    private Integer caddyId;
    private Integer bookId;

    public CaddyItemSearchVM() {
        this.caddyId = null;
        this.bookId = null;
    }

    public CaddyItemSearchVM(Integer caddyId, Integer bookId) {
        this.caddyId = caddyId;
        this.bookId = bookId;
    }

    public Integer getCaddyId() {
        return caddyId;
    }

    public void setCaddyId(Integer caddyId) {
        this.caddyId = caddyId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }
}
