package models;

import play.Logger;
import play.db.DB;
import tools.DBUtils;
import tools.Utils;

import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by bosco on 07/05/2019.
 */
public class User {

    private int idUser;

    private int idProfil;

    private String libelleProfil;

    private int partner;

    private String nom;

    private String prenom;

    private String telephone;

    private String email;

    private String dateCreation;

    private String verrou;

    public User() {

    }


    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public int getIdProfil() {
        return idProfil;
    }

    public void setIdProfil(int idProfil) {
        this.idProfil = idProfil;
    }

    public String getLibelleProfil() {
        return libelleProfil;
    }

    public void setLibelleProfil(String libelleProfil) {
        this.libelleProfil = libelleProfil;
    }

    public int getPartner() {
        return partner;
    }

    public void setPartner(int partner) {
        this.partner = partner;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(String dateCreation) {
        this.dateCreation = dateCreation;
    }

    public String getVerrou() {
        return verrou;
    }

    public void setVerrou(String verrou) {
        this.verrou = verrou;
    }

    public static boolean updatePwdUser(String idUser, String mdp, String oldMdp) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String req;
        try {
            connection = DB.getConnection();
            req = "update user as u SET " + Column.PASSWORD + " = ? "
                    + "where " + Column.ID_USER + " = ? AND " + Column.PASSWORD + " = ? ";

            preparedStatement = connection.prepareStatement(req);
            preparedStatement.setObject(1, Utils.getMD5(mdp));
            preparedStatement.setObject(2, new Integer(idUser));
            preparedStatement.setObject(3, Utils.getMD5(oldMdp));

            int retour = preparedStatement.executeUpdate();
            if (retour == 0) {
                Logger.info("RETOUR UPDATE USER ==> " + retour);
                return false;
            }

            Logger.info("req ==> " + req);
        } catch (SQLException e) {
            Logger.error("updatePwdUser SQLException " + e.getMessage());
            return false;
        } finally {
            DBUtils.closeQuietly(connection, preparedStatement);
        }
        return true;
    }

    public static boolean reinitPassUser(String idUser, String mdp) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String req;
        try {
            connection = DB.getConnection();
            req = "UPDATE user as u SET " + Column.PASSWORD + " = ? "
                    + "WHERE " + Column.ID_USER + " = ?";

            preparedStatement = connection.prepareStatement(req);
            preparedStatement.setObject(1, Utils.getMD5(mdp));
            preparedStatement.setObject(2, idUser);

            int retour = preparedStatement.executeUpdate();
            if (retour == 0) {
                Logger.info("RETOUR UPDATE USER ==> " + retour);
                return false;
            }

            Logger.info("req ==> " + req);
        } catch (SQLException e) {
            Logger.error("REINITILISER MOT DE PASSE SQLException " + e.getMessage());
            return false;
        } finally {
            DBUtils.closeQuietly(connection, preparedStatement);
        }
        return true;
    }

    public static boolean verrouUser(String idUser, String verrou) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        String req = "UPDATE user as u SET " + Column.VEROU + " = ? "
                + "WHERE " + Column.ID_USER + " = ?";
        try {
            connection = DB.getConnection();
            preparedStatement = connection.prepareStatement(req);
            preparedStatement.setObject(1, verrou);
            preparedStatement.setObject(2, idUser);

            int retour = preparedStatement.executeUpdate();
            if (retour == 0) {
                Logger.info("RETOUR VERROU USER ==> " + retour);
                return false;
            }

            Logger.info("req ==> " + req);
        } catch (SQLException e) {
            Logger.error("VERROU USER SQLException " + e.getMessage());
            return false;
        } finally {
            DBUtils.closeQuietly(connection, preparedStatement);
        }
        return true;
    }

    public static ProcedureResult addUser(String nom, String prenom, String telephone, String email,
                                          String login, String pass, int profil, int partner) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_add_user(?,?,?,?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.setString(3, nom);
            callableStatement.setString(4, prenom);
            callableStatement.setString(5, telephone);
            callableStatement.setString(6, email);
            callableStatement.setString(7, login);
            callableStatement.setString(8, pass);
            callableStatement.setInt(9, profil);
            callableStatement.setInt(10, partner);
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

    public static ProcedureResult updateUser(String nom, String prenom, String telephone,
                                          String email, int user) {
        Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_update_user(?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.setString(3, nom);
            callableStatement.setString(4, prenom);
            callableStatement.setString(5, telephone);
            callableStatement.setString(6, email);
            callableStatement.setInt(7, user);
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

    public static ArrayList<User> getUsers(int idClient) {

        String projection = Column.ID_USER + ", " + Column.ID_PROFIL + ", " + Column.LIBELLE_PROFIL + ", " +
                            Column.PARTNER + ", " + Column.NOM + ", " + Column.PRENOM + ", " +
                            Column.TELEPHONE + ", " + Column.EMAIL + ", " + Column.VEROU + ", " +
                            Column.DATE_CREATION + "";
        StringBuilder req = requestgetUsers(idClient, projection);

        Logger.info(req.toString());

        ArrayList<User> listUser = new ArrayList<User>();
        Connection connection = null;
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
                User user = getUserObject(resultSet);
                listUser.add(user);
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, statement, resultSet);
        }
        return listUser;
    }

    private static User getUserObject(ResultSet r) throws SQLException {

        User user = new User();

        user.setIdUser(r.getInt(Column.ID_USER));
        user.setIdProfil(r.getInt(Column.ID_PROFIL));
        user.setLibelleProfil(r.getString(Column.LIBELLE_PROFIL));
        user.setPartner(r.getInt(Column.PARTNER));
        user.setNom(r.getString(Column.NOM));
        user.setPrenom(r.getString(Column.PRENOM));
        user.setTelephone(r.getString(Column.TELEPHONE));
        user.setEmail(r.getString(Column.EMAIL));
        user.setVerrou(r.getString(Column.VEROU));
        user.setDateCreation(r.getString(Column.DATE_CREATION));

        return user;
    }

    public static StringBuilder requestgetUsers(int idClient, String projection){

        StringBuilder req = new StringBuilder(
                "select " + projection + " " +
                        "from user as u " +
                        "inner join profil as p on p.id = u.idprofil "
        );
        req.append("where u.idpartenaire = " + idClient + " ");
        return req;
    }

    public static class Column {

        public static final String ID_USER = "u.id";

        public static final String ID_PROFIL = "idprofil";

        public static final String LIBELLE_PROFIL = "LIBELLE";

        public static final String PARTNER = "idpartenaire";

        public static final String NOM = "nom";

        public static final String PRENOM = "prenom";

        public static final String TELEPHONE = "telephone";

        public static final String EMAIL = "email";

        public static final String DATE_CREATION = "datecreation";

        public static final String VEROU = "verrou";

        public static final String PASSWORD = "password";

    }



}
