package Server.Model.SearchViewModel;

import java.io.Serializable;

public class ClientSearchVM implements Serializable {

    private Integer id;
    private String lastName;
    private String firstName;

    public ClientSearchVM() {
        this.id = null;
        this.lastName = null;
        this.firstName = null;
    }

    public ClientSearchVM(Integer id, String lastName, String firstName) {
        this.id = id;
        this.lastName = lastName;
        this.firstName = firstName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
}
