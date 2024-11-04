package Server.Model.DataAcessObject;

import Server.Model.DataBase.DataBaseConnection;
import Server.Model.Entities.Caddy;
import Server.Model.Entities.CaddyItem;
import Server.Model.SearchViewModel.CaddyItemSearchVM;
import Server.Model.SearchViewModel.CaddySearchVM;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

public class CaddyItemDAO {

    private final DataBaseConnection dataBaseConnection;

    public CaddyItemDAO(DataBaseConnection dataBaseConnection) {
        this.dataBaseConnection = dataBaseConnection;
    }

    public ArrayList<CaddyItem> load(CaddyItemSearchVM csvm) throws SQLException {
        String sql = "SELECT * FROM caddy_items";
        if (csvm != null) {
            String where = " WHERE 1=1 ";
            if (csvm.getCaddyId() != null) {
                where += "AND caddyId = ? ";
            }
            if(csvm.getBookId() != null) {
                where += "AND bookId = ? ";
            }
            sql += where;
        }
        PreparedStatement stmt = dataBaseConnection.getConnection().prepareStatement(sql);
        if (csvm != null) {
            int i = 1;
            if (csvm.getCaddyId() != null) {
                stmt.setInt(i, csvm.getCaddyId());
                i++;
            }
            if (csvm.getBookId() != null) {
                stmt.setInt(i, csvm.getBookId());
            }
        }
        ResultSet rs = stmt.executeQuery();

        ArrayList<CaddyItem> caddyItems = new ArrayList<>();

        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer caddyId = rs.getInt("caddyId");
            Integer bookId = rs.getInt("bookId");
            Integer quantity = rs.getInt("quantity");
            CaddyItem caddyItem = new CaddyItem(id,caddyId,bookId,quantity);
            caddyItems.add(caddyItem);
        }
        stmt.close();

        return caddyItems;
    }

    public void save(CaddyItem c) throws SQLException {
        String sql;
        if (c != null) {
            if (c.getId() != null) { // UPDATE
                sql = "UPDATE caddy_items SET "
                        + " caddyId = ?, "
                        + " bookId = ?, "
                        + " quantity = ?, "
                        + " WHERE id = ?";
                PreparedStatement pStmt = dataBaseConnection.getConnection().prepareStatement(sql);
                pStmt.setInt(1,c.getCaddyId());
                pStmt.setInt(2, c.getBookId());
                pStmt.setInt(3,c.getQuantity());
                pStmt.setInt(4, c.getId());
                pStmt.executeUpdate();
                pStmt.close();
            }
            else { // CREATE
                sql = "INSERT INTO caddy_items (caddyId, bookId, quantity) VALUES (?,?,?)";
                PreparedStatement pStmt = dataBaseConnection.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                pStmt.setInt(1,c.getCaddyId());
                pStmt.setInt(2, c.getBookId());
                pStmt.setInt(3,c.getQuantity());
                pStmt.executeUpdate();
                ResultSet rs = pStmt.getGeneratedKeys();
                rs.next();
                c.setId((int) rs.getLong(1));
                rs.close();
                pStmt.close();
            }
        }
    }

    public void delete(CaddyItem entity) throws SQLException {
        if (entity != null && entity.getId() != null) {
            this.delete(entity.getId());
        }
    }

    public void delete(Integer id) throws SQLException {
        if (id != null) {
            String sql = "DELETE FROM caddy_items WHERE id = ?";
            PreparedStatement stmt = dataBaseConnection.getConnection().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        }
    }
}
