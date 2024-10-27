package Client.Model.Entities;

public class CaddyItem {

    private Integer id;
    private Integer caddyId;
    private Integer bookId;
    private Integer quantity;

    public CaddyItem() {
        this.id = null;
        this.caddyId = null;
        this.bookId = null;
        this.quantity = null;
    }

    public CaddyItem(Integer id, Integer caddyId, Integer bookId, Integer quantity) {
        this.id = id;
        this.caddyId = caddyId;
        this.bookId = bookId;
        this.quantity = quantity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "CaddyItem{" +
                "id=" + id +
                ", caddyId=" + caddyId +
                ", bookId=" + bookId +
                ", quantity=" + quantity +
                '}';
    }
}
