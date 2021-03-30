package models;

import play.Logger;
import play.db.DB;
import tools.DBUtils;
import tools.Utils;

import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by mac on 18/03/2021.
 */
public class Guichet {

    private String code;

    private String numeroCompte;

    private String solde;

    private String libelle;
    
    private String partner;

    private String adresse;

    private String dateCreation;

    private String etat;
    
    private String open;

    public Guichet() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNumeroCompte() {
        return numeroCompte;
    }

    public void setNumeroCompte(String numeroCompte) {
        this.numeroCompte = numeroCompte;
    }

    public String getSolde() {
        return solde;
    }

    public void setSolde(String solde) {
        this.solde = solde;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public String getEtat() {
        return etat;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getOpen() {
        return open;
    }

    public void setOpen(String open) {
        this.open = open;
    }

    public static ArrayList<Guichet> getGuichets(String partner) {

        String projection = Column.CODE_GUICHET + ", g." +
                Column.NUMERO_COMPTE + ", " + Column.SOLDE + ", " +
                Column.NOM_GUICHET + ", " + Column.PARTNER + ", " +
                Column.ADRESSE + ", " + Column.DATE_CREATE + ", g." +
                Column.ETAT + ", " + Column.OPEN;
        StringBuilder req = requestgetGuichets(partner, projection);

        Logger.info(req.toString());

        ArrayList<Guichet> listGuichet = new ArrayList<Guichet>();
        java.sql.Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = DB.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(req.toString());

            if (resultSet == null) {
                return new ArrayList<>();
            }

            while (resultSet.next()) {
                Guichet guichet = getGuichetObject(resultSet);
                listGuichet.add(guichet);
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, statement, resultSet);
        }
        return listGuichet;
    }

    private static Guichet getGuichetObject(ResultSet r) throws SQLException {

        Guichet guichet = new Guichet();

        guichet.setCode(r.getString(Column.CODE_GUICHET));
        guichet.setNumeroCompte(r.getString(Column.NUMERO_COMPTE));
        guichet.setSolde(Utils.formatAmount(r.getDouble(Column.SOLDE)));
        guichet.setLibelle(r.getString(Column.NOM_GUICHET));
        guichet.setPartner(r.getString(Column.PARTNER));
        guichet.setAdresse(r.getString(Column.ADRESSE));
        guichet.setDateCreation(r.getString(Column.DATE_CREATE));
        guichet.setEtat(r.getString(Column.ETAT));
        guichet.setOpen(r.getString(Column.OPEN));

        return guichet;
    }

    public static StringBuilder requestgetGuichets(String partner, String projection){

        StringBuilder req = new StringBuilder(
                "select " + projection + " " +
                        "from guichet as g inner join compte as c on c.numerocompte = g.numerocompte "
        );
        req.append("where " + Column.PARTNER + " = '" + partner + "' ");
        return req;
    }

    public static AddGuichetResult addGuichet(String nom, String adresse, String partner, String deviceId) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_create_guichet(?,?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.VARCHAR);
            callableStatement.setString(5, nom);
            callableStatement.setString(6, adresse);
            callableStatement.setString(7, partner);
            callableStatement.setString(8, deviceId);
            callableStatement.execute();
            int value = callableStatement.getInt(1);
            String message = callableStatement.getString(2);
            String account = callableStatement.getString(3);
            String code = callableStatement.getString(4);
            return new AddGuichetResult(value, message, account, code);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return null;
        } finally {
            DBUtils.closeQuietly(connection, callableStatement);
        }
    }

    public static boolean verrou(String code, String verrou) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String req = "UPDATE guichet SET " + Column.ETAT + " = ? "
                + "WHERE " + Column.CODE_GUICHET + " = ?";
        try {
            connection = DB.getConnection();
            preparedStatement = connection.prepareStatement(req);
            preparedStatement.setObject(1, verrou);
            preparedStatement.setObject(2, code);

            int retour = preparedStatement.executeUpdate();
            if (retour == 0) {
                Logger.info("RETOUR VERROU GUICHET ==> " + retour);
                return false;
            }

            Logger.info("req ==> " + req);
        } catch (SQLException e) {
            Logger.error("VERROU GUICHET SQLException " + e.getMessage());
            return false;
        } finally {
            DBUtils.closeQuietly(connection, preparedStatement);
        }
        return true;
    }

    public static DepotResult depotExecute(String consumerId, String guichet, double montant, int user) {
        java.sql.Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_depot_guichet_execute(?,?,?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.DOUBLE);
            callableStatement.registerOutParameter(5, Types.DOUBLE);
            callableStatement.setString(6, consumerId);
            callableStatement.setString(7, guichet);
            callableStatement.setDouble(8, montant);
            callableStatement.setInt(9, user);
            callableStatement.execute();
            int value = callableStatement.getInt(1);
            String message = callableStatement.getString(2);
            String transasction = callableStatement.getString(3);
            double balanceCHARGE = callableStatement.getDouble(5);
            return new DepotResult(value, message, transasction, balanceCHARGE);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return null;
        } finally {
            DBUtils.closeQuietly(connection, callableStatement);
        }
    }

    public static ProcedureResult openGuichet(String device, String guichet, int user) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_connection_guichet(?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.setString(3, device);
            callableStatement.setString(4, guichet);
            callableStatement.setInt(5, user);
            callableStatement.execute();
            int value = callableStatement.getInt(1);
            String message = callableStatement.getString(2);
            return new ProcedureResult(value, message);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return null;
        } finally {
            DBUtils.closeQuietly(connection, callableStatement);
        }
    }

    public static class Column {

        public static final String CODE_GUICHET = "codeguichet";

        public static final String NUMERO_COMPTE = "numerocompte";

        public static final String SOLDE = "solde";

        public static final String NOM_GUICHET = "nomguichet";

        public static final String PARTNER = "partner";

        public static final String ADRESSE = "adresse";

        public static final String DATE_CREATE = "datecreation";

        public static final String ETAT = "etat";

        public static final String OPEN = "open";

    }

}
