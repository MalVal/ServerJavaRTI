package Model.DataAcessObject;

import Model.DataBase.DataBaseConnection;
import Model.Entities.Book;
import Model.SearchViewModel.BookSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookDAO {

    public ArrayList<Book> loadBook(BookSearchVM b, DataBaseConnection bd) throws SQLException {

        ArrayList<Book> books = new ArrayList<>();

        String sql ="SELECT books.id,books.title,books.isbn,books.page_count,books.stock_quantity, books.price, books.publish_year,authors.last_name,subjects.name FROM books INNER JOIN authors INNER JOIN subject on (books.author_id = authors.id) ON (books.subject_id = subjects.id)";

        if(b != null)
        {
            String where =  "where 1=1";
            if(b.getIdBook() != null)
            {
                where += "AND books.id = ?";
            }
            if(b.getAuthorLastName() != null)
            {
                where += "AND books.author_id = ?";
            }
            if(b.getSubjectName() != null)
            {
                where += "AND books.subject_id = ?";
            }
            if(b.getTitle() != null)
            {
                where += "AND books.title = ?";
            }
            if(b.getIsbn()!= null)
            {
                where += "AND books.isbn = ?";
            }
            if(b.getPublishYear() != null)
            {
                where += "AND books.publish_year = ?";
            }
            where += ";";
            sql += where;
        }

        PreparedStatement stmt = bd.getConnection().prepareStatement(sql);

        if(b != null)
        {
            int i =0;
            if(b.getIdBook() != null)
            {
                i++;
                stmt.setInt(i, b.getIdBook());
            }
            if(b.getAuthorLastName() != null)
            {
                i++;
                stmt.setString(i,b.getAuthorLastName());
            }
            if(b.getSubjectName() != null)
            {
                i++;
                stmt.setString(i,b.getSubjectName());
            }
            if(b.getTitle() != null)
            {
                i++;
                stmt.setString(i, "%" + b.getTitle() + "%");
            }
            if(b.getIsbn()!= null)
            {
                i++;
                stmt.setString(i, "%" + b.getIsbn() + "%");
            }
            if(b.getPublishYear() != null)
            {
                i++;
                stmt.setInt(i,b.getPublishYear());
            }
        }
        ResultSet rs = stmt.executeQuery();
        while (rs.next())
        {
            Book book = new Book();

            book.setId(rs.getInt("books.id"));
            book.setAuthorLastName(rs.getString("authors.last_name"));
            book.setSubjectName(rs.getString("subjects.name"));
            book.setTitle(rs.getString("title"));
            book.setIsbn(rs.getString("isbn"));
            book.setPageCount(rs.getInt("page_count"));
            book.setStockQuantity(rs.getInt("stock_quantity"));
            book.setPrice(rs.getFloat("price"));
            book.setPublishYear(rs.getInt("publish_year"));

            books.add(book);
        }

        return books;
    }
}