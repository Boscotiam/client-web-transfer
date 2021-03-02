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
public class Country {

    private String code;

    private String libelle;

    public Country() {
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

    public static ArrayList<Country> getCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        java.sql.Connection connection = null;
        Statement st = null;
        ResultSet res = null;
        String req = "SELECT * FROM " + COUNTRY.TABLE + " ";
        try {
            connection = DB.getConnection();
            st = connection.createStatement();
            res = st.executeQuery(req);
            while (res.next()) {
                countries.add(getCountryObect(res));
            }
        } catch (SQLException e) {
            Logger.error("getUser SQLException " + e.getMessage());
        } finally {
            DBUtils.closeQuietly(connection, st, res);
        }
        return countries;
    }

    private static Country getCountryObect(ResultSet r) throws SQLException {

        Country country = new Country();

        country.setCode(r.getString(COUNTRY.CODE));
        country.setLibelle(r.getString(COUNTRY.LIBELLE));

        return country;
    }

    public class COUNTRY {

        public static final String TABLE = "pays";

        public static final String CODE = "codepays";

        public static final String LIBELLE = "nompays";
    }

}
