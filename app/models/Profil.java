package models;

import play.Logger;
import play.db.DB;
import tools.DBUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by bosco on 09/05/2019.
 */
public class Profil {

    public static final String ID_PROFIL = "id";

    public static final String CODE_PROFIL = "CODE";

    public static final String LIBELLE_PROFIL = "LIBELLE";

    private String idProfil;

    private String codeProfil;

    private String libelleProfil;

    public Profil() {
    }

    public String getIdProfil() {
        return idProfil;
    }

    public void setIdProfil(String idProfil) {
        this.idProfil = idProfil;
    }

    public String getLibelleProfil() {
        return libelleProfil;
    }

    public void setLibelleProfil(String libelleProfil) {
        this.libelleProfil = libelleProfil;
    }

    public String getCodeProfil() {
        return codeProfil;
    }

    public void setCodeProfil(String codeProfil) {
        this.codeProfil = codeProfil;
    }

    @Override
    public String toString() {
        return "Profil{" +
                "idProfil='" + idProfil + '\'' +
                ", libelleProfil='" + libelleProfil + '\'' +
                ", codeProfil='" + codeProfil + '\'' +
                '}';
    }

    public static ArrayList<Profil> getProfils() {
        StringBuilder req = new StringBuilder(
                "select " + ID_PROFIL + ", " + LIBELLE_PROFIL + ", " + CODE_PROFIL + " " +
                        "from profil p "
        );
        Logger.info(req.toString());
        ArrayList<Profil> listProfil = new ArrayList<Profil>();
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
                listProfil.add(getProfilObject(resultSet));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, statement, resultSet);
        }
        return listProfil;
    }

    private static Profil getProfilObject(ResultSet r) throws SQLException {

        Profil profil = new Profil();

        profil.setIdProfil(r.getString(ID_PROFIL));
        profil.setLibelleProfil(r.getString(LIBELLE_PROFIL));
        profil.setCodeProfil(r.getString(CODE_PROFIL));

        return profil;
    }

}
