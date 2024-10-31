package Server.Model.SearchViewModel;

import java.io.Serializable;

public class CaddyItemSearchVM implements Serializable {

    private Integer caddyId;

    public CaddyItemSearchVM() {
        this.caddyId = null;
    }

    public CaddyItemSearchVM(Integer caddyId) {
        this.caddyId = caddyId;
    }

    public Integer getCaddyId() {
        return caddyId;
    }

    public void setCaddyId(Integer caddyId) {
        this.caddyId = caddyId;
    }
}
