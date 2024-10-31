package Server.Model.Entities;

import java.io.Serializable;

public class Book implements Serializable {

    private Integer id;
    private String authorLastName;
    private String subjectName;
    private String title;
    private String isbn;
    private Integer pageCount;
    private Integer stockQuantity;
    private Float price;
    private Integer publishYear;

    public Book() {
        this.id = null;
        this.authorLastName = null;
        this.subjectName = null;
        this.title = null;
        this.isbn = null;
        this.pageCount = null;
        this.stockQuantity = null;
        this.price = null;
        this.publishYear = null;
    }

    public Book(int id, String authorLastName, String subjectName, String title, String isbn, int pageCount, int stockQuantity, float price, int publishYear) {
        this.id = id;
        this.authorLastName = authorLastName;
        this.subjectName = subjectName;
        this.title = title;
        this.isbn = isbn;
        this.pageCount = pageCount;
        this.stockQuantity = stockQuantity;
        this.price = price;
        this.publishYear = publishYear;
    }

    public Integer getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthorLastName() {
        return authorLastName;
    }

    public void setAuthorLastName(String authorLastName) {
        this.authorLastName = authorLastName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getPublishYear() {
        return publishYear;
    }

    public void setPublishYear(int publishYear) {
        this.publishYear = publishYear;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", authorLastName='" + authorLastName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", title='" + title + '\'' +
                ", isbn='" + isbn + '\'' +
                ", pageCount=" + pageCount +
                ", stockQuantity=" + stockQuantity +
                ", price=" + price +
                ", publishYear=" + publishYear +
                '}';
    }
}