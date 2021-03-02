package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.db.DB;
import play.libs.Json;
import tools.DBUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by mac on 14/10/2020.
 */
public class Bank {

    private String code;

    private String libelle;

    public Bank() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public ObjectNode toObjectNode(){
        ObjectNode node = Json.newObject();
        node.put("code", code);
        node.put("libelle", libelle);
        return node;
    }

    public static ArrayList<Bank> getBanks() {
        ArrayList<Bank> countries = new ArrayList<>();
        java.sql.Connection connection = null;
        Statement st = null;
        ResultSet res = null;
        String req = "SELECT * FROM " + BANK.TABLE + " ";
        try {
            connection = DB.getConnection();
            st = connection.createStatement();
            res = st.executeQuery(req);
            while (res.next()) {
                countries.add(getBankObect(res));
            }
        } catch (SQLException e) {
            Logger.error("getUser SQLException " + e.getMessage());
        } finally {
            DBUtils.closeQuietly(connection, st, res);
        }
        return countries;
    }

    private static Bank getBankObect(ResultSet r) throws SQLException {

        Bank bank = new Bank();

        bank.setCode(r.getString(BANK.CODE));
        bank.setLibelle(r.getString(BANK.LIBELLE));

        return bank;
    }

    public class BANK {

        public static final String TABLE = "bank";

        public static final String CODE = "codebank";

        public static final String LIBELLE = "libelle";
    }

}
