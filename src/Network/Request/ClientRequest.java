package Network.Request;

public class ClientRequest implements Request {

    private String lastName;
    private String firstName;
    private boolean isNew;

    public ClientRequest(String lastName, String firstName, boolean isNew) {
        this.lastName = lastName;
        this.firstName = firstName;
        this.isNew = isNew;
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

    public boolean isNew() {
        return isNew;
    }

    public void setNew(boolean aNew) {
        isNew = aNew;
    }
}
