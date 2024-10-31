package Server.Model.SearchViewModel;

import java.io.Serializable;

public class CaddySearchVM implements Serializable {

    private Integer id;

    public CaddySearchVM() {
        this.id = null;
    }

    public CaddySearchVM(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
