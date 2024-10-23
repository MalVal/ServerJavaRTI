package Server.Model.DataAcessObject;

import Server.Model.DataBase.DataBaseConnection;
import Server.Model.Entities.Book;
import Server.Model.SearchViewModel.BookSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BookDAO {

    public ArrayList<Book> loadBook(BookSearchVM b, DataBaseConnection bd) throws SQLException {

        ArrayList<Book> books = new ArrayList<>();

        String sql ="SELECT books.id, books.title, books.isbn, books.page_count, books.stock_quantity, books.price, books.publish_year, authors.last_name, subjects.name " +
                "FROM books " +
                "INNER JOIN authors ON (books.author_id = authors.id) " +
                "INNER JOIN subjects ON (books.subject_id = subjects.id)";

        if(b != null)
        {
            String where =  " WHERE 1=1 ";
            if(b.getIdBook() != null)
            {
                where += "AND books.id = ? ";
            }
            if(b.getAuthorLastName() != null)
            {
                where += "AND authors.last_name LIKE ? ";
            }
            if(b.getSubjectName() != null)
            {
                where += "AND subjects.name LIKE ? ";
            }
            if(b.getTitle() != null)
            {
                where += "AND books.title LIKE ? ";
            }
            if(b.getIsbn()!= null)
            {
                where += "AND books.isbn LIKE ? ";
            }
            if(b.getPublishYear() != null)
            {
                where += "AND books.publish_year = ? ";
            }
            sql += where;
        }

        PreparedStatement stmt = bd.getConnection().prepareStatement(sql);

        if(b != null)
        {
            int i = 0;
            if(b.getIdBook() != null)
            {
                i++;
                stmt.setInt(i, b.getIdBook());
            }
            if(b.getAuthorLastName() != null)
            {
                i++;
                stmt.setString(i, "%" + b.getAuthorLastName() + "%");
            }
            if(b.getSubjectName() != null)
            {
                i++;
                stmt.setString(i, "%" + b.getSubjectName() + "%");
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
                stmt.setInt(i, b.getPublishYear());
            }
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next())
        {
            Book book = new Book();

            book.setId(rs.getInt("id"));
            book.setAuthorLastName(rs.getString("last_name"));
            book.setSubjectName(rs.getString("name"));
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