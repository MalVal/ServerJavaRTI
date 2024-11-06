package Common.Network.Response;

import Common.Model.Entities.CaddyItem;

import java.util.ArrayList;

public class GetCaddyItemResponse implements Response {
    private ArrayList<CaddyItem> caddy;

    public GetCaddyItemResponse() {}

    public GetCaddyItemResponse(ArrayList<CaddyItem> caddy) {
        this.caddy = caddy;
    }

    public ArrayList<CaddyItem> getCaddy() {
        return caddy;
    }

    public void setCaddy(ArrayList<CaddyItem> caddy) {
        this.caddy = caddy;
    }
}
