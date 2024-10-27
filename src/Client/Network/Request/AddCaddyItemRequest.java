package Client.Network.Request;

public class AddCaddyItemRequest implements Request {

    private Integer idBook;
    private Integer quantity;

    public AddCaddyItemRequest(Integer idBook, Integer quantity) {
        this.idBook = idBook;
        this.quantity = quantity;
    }

    public Integer getIdBook() {
        return idBook;
    }

    public void setIdBook(Integer idBook) {
        this.idBook = idBook;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
