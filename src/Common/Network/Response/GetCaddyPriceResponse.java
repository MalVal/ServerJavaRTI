package Common.Network.Response;

public class GetCaddyPriceResponse implements Response {
    private Double caddyPrice;

    public GetCaddyPriceResponse() {
        this.caddyPrice = null;
    }

    public GetCaddyPriceResponse(Double caddyPrice) {
        this.caddyPrice = caddyPrice;
    }

    public Double getCaddyPrice() {
        return caddyPrice;
    }

    public void setCaddyPrice(Double caddyPrice) {
        this.caddyPrice = caddyPrice;
    }
}
