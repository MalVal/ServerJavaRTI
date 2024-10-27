package Client.Model.Entities;

import java.io.Serializable;
import java.time.LocalDate;

public class Caddy implements Serializable {

    private Integer id;
    private Integer clientId;
    private LocalDate date;
    private Double amount;
    private Boolean isPayed;

    public Caddy() {
        this.id = null;
        this.clientId = null;
        this.date = null;
        this.amount = null;
        this.isPayed = null;
    }

    public Caddy(Integer id, Integer clientId, LocalDate date, Double amount, Boolean isPayed) {
        this.id = id;
        this.clientId = clientId;
        this.date = date;
        this.amount = amount;
        this.isPayed = isPayed;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer clientId) {
        this.clientId = clientId;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Boolean getPayed() {
        return isPayed;
    }

    public void setPayed(Boolean payed) {
        isPayed = payed;
    }

    @Override
    public String toString() {
        return "Caddies{" +
                "id=" + id +
                ", clientId=" + clientId +
                ", date=" + date +
                ", amount=" + amount +
                ", isPayed=" + isPayed +
                '}';
    }
}
