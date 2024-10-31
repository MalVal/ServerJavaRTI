package Server.Model.DataAcessObject;

import Server.Model.DataBase.DataBaseConnection;
import Server.Model.Entities.Caddy;
import Server.Model.SearchViewModel.CaddySearchVM;

import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.time.LocalDate;

public class CaddyDAO {

    private final DataBaseConnection connectDB;

    public CaddyDAO(DataBaseConnection connectDB) {
        this.connectDB = connectDB;
    }

    public ArrayList<Caddy> load(CaddySearchVM csvm) throws SQLException {
        String sql = "SELECT * FROM caddies";
        if (csvm != null) {
            String where = " WHERE 1=1 ";
            if (csvm.getId() != null) {
                where += "AND id = ? ";
            }
            sql += where;
        }
        PreparedStatement stmt = connectDB.getConnection().prepareStatement(sql);
        if (csvm != null) {
            if (csvm.getId() != null) {
                stmt.setInt(1, csvm.getId());
            }
        }
        ResultSet rs = stmt.executeQuery();

        ArrayList<Caddy> caddyList = new ArrayList<>();

        while (rs.next()) {
            Integer id = rs.getInt("id");
            Integer clientId = rs.getInt("clientId");
            LocalDate date = rs.getDate("date").toLocalDate();
            Double amount = rs.getDouble("amount");
            Boolean isPayed = rs.getBoolean("isPayed");
            Caddy caddy = new Caddy(id,clientId,date,amount,isPayed);
            caddyList.add(caddy);
        }
        stmt.close();

        return caddyList;
    }

    public void save(Caddy c) throws SQLException {
        String sql;
        if (c != null) {
            if (c.getId() != null) { // UPDATE
                sql = "UPDATE caddies SET "
                        + " clientId = ?, "
                        + " date = ?, "
                        + " amount = ?, "
                        + " isPayed = ? "
                        + " WHERE id = ?";
                PreparedStatement pStmt = connectDB.getConnection().prepareStatement(sql);
                pStmt.setInt(1,c.getClientId());
                pStmt.setDate(2, Date.valueOf(c.getDate()));
                pStmt.setDouble(3,c.getAmount());
                pStmt.setBoolean(4,c.getPayed());
                pStmt.setInt(5,c.getId());
                pStmt.executeUpdate();
                pStmt.close();
            }
            else { // CREATE
                sql = "INSERT INTO caddies ("
                        + "clientId, "
                        + "date, "
                        + "amount, "
                        + "isPayed "
                        + ") VALUES ("
                        + "?, "
                        + "?, "
                        + "?, "
                        + "? "
                        + ")";
                PreparedStatement pStmt = connectDB.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                pStmt.setInt(1,c.getClientId());
                pStmt.setDate(2, Date.valueOf(c.getDate()));
                pStmt.setDouble(3,c.getAmount());
                pStmt.setBoolean(4,c.getPayed());
                pStmt.executeUpdate();
                ResultSet rs = pStmt.getGeneratedKeys();
                rs.next();
                c.setId((int) rs.getLong(1));
                rs.close();
                pStmt.close();
            }
        }
    }

    public void delete(Caddy entity) throws SQLException {
        if (entity != null && entity.getId() != null) {
            this.delete(entity.getId());
        }
    }

    public void delete(Integer id) throws SQLException {
        if (id != null) {
            String sql = "DELETE FROM caddies WHERE id = ?";
            PreparedStatement stmt = connectDB.getConnection().prepareStatement(sql);
            stmt.setInt(1, id);
            stmt.executeUpdate();
            stmt.close();
        }
    }
}
