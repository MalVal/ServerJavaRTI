package Server.Model.DataAcessObject;

import Server.Model.DataBase.DataBaseConnection;
import Server.Model.Entities.Client;
import Server.Model.SearchViewModel.ClientSearchVM;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ClientDAO {

    private final DataBaseConnection db;

    public ClientDAO(DataBaseConnection db) {
        this.db = db;
    }

    public ArrayList<Client> loadClient(ClientSearchVM b) throws SQLException {

        ArrayList<Client> clients = new ArrayList<>();

        String sql ="SELECT * " +
                "FROM clients ";

        if(b != null)
        {
            String where =  " WHERE 1=1 ";
            if(b.getId() != null)
            {
                where += "AND clients.id = ? ";
            }
            if(b.getLastName() != null)
            {
                where += "AND clients.last_name LIKE ? ";
            }
            if(b.getFirstName() != null)
            {
                where += "AND clients.first_name LIKE ? ";
            }
            sql += where;
        }

        PreparedStatement stmt = this.db.getConnection().prepareStatement(sql);

        if(b != null)
        {
            int i = 0;
            if(b.getId() != null)
            {
                i++;
                stmt.setInt(i, b.getId());
            }
            if(b.getLastName() != null)
            {
                i++;
                stmt.setString(i, "%" + b.getLastName() + "%");
            }
            if(b.getFirstName() != null)
            {
                i++;
                stmt.setString(i, "%" + b.getFirstName() + "%");
            }
        }

        ResultSet rs = stmt.executeQuery();
        while (rs.next())
        {
            Client client = new Client();

            client.setId(rs.getInt("id"));
            client.setLastName(rs.getString("last_name"));
            client.setFirstName(rs.getString("first_name"));

            clients.add(client);
        }
        stmt.close();

        return clients;
    }

    public void save(Client c) throws Exception {

        String sql;
        if (c != null) {
            if (c.getLastName() == null || c.getFirstName() == null) {
                throw new Exception("Lastname and/or firstname is missing !");
            }

            ClientDAO dao = new ClientDAO(this.db);
            ArrayList<Client> arrayClients = dao.loadClient(new ClientSearchVM(null, c.getLastName(), c.getFirstName()));
            if(arrayClients.size() != 0) {
                throw new Exception("Client already exists !");
            }

            sql = "INSERT INTO clients ("
                    + "last_name, "
                    + "first_name "
                    + ") VALUES ("
                    + "?, "
                    + "? "
                    + ")";
            PreparedStatement pStmt = this.db.getConnection().prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            pStmt.setString(1,c.getLastName());
            pStmt.setString(2,c.getFirstName());
            pStmt.executeUpdate();
            ResultSet rs = pStmt.getGeneratedKeys();
            rs.next();
            c.setId((int) rs.getLong(1));
            rs.close();
            pStmt.close();
        }
    }
}