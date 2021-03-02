package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.Play;
import play.db.DB;
import play.libs.Json;
import tools.Const;
import tools.DBUtils;
import tools.Utils;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by bosco on 31/01/2021.
 */
public class Journal {

    private  String libelle, montant, sens, date, reference, compte;

    public Journal() {
    }

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getSens() {
        return sens;
    }

    public void setSens(String sens) {
        this.sens = sens;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getCompte() {
        return compte;
    }

    public void setCompte(String compte) {
        this.compte = compte;
    }


    public ObjectNode toObjectNode(){
        ObjectNode node = Json.newObject();
        node.put("libelle",libelle);
        node.put("sens",sens);
        node.put("date",date);
        node.put("reference",reference);
        node.put("compte",compte);
        node.put("montant", montant);
        return node;
    }

    public String toString(){
        return toObjectNode().toString();
    }

    public static StringBuilder requestJournal(String debut, String fin, String profil, String projection){

        String profilAdminSender = Play.application().configuration().getString("profil.sender");
        String profilAdminPayer = Play.application().configuration().getString("profil.payer");
        String profilAdmin = Play.application().configuration().getString("profil.admin");

        StringBuilder req = new StringBuilder(
                "SELECT " + projection + " " +
                        "FROM " + JOURNAL.TABLE + " "
        );

        String comptePrinc = play.mvc.Controller.ctx().session().get(Const.SESSION_PARTNER_ACCOUNT_PRINC);
        String compteComm = play.mvc.Controller.ctx().session().get(Const.SESSION_PARTNER_ACCOUNT_COMM);
        String compteCharge = play.mvc.Controller.ctx().session().get(Const.SESSION_PARTNER_ACCOUNT_CHARGE);

        if(!debut.isEmpty()){
            req.append("WHERE " + JOURNAL.DATE + " >= '" + debut + "' ");
        }
        if(!fin.isEmpty() && debut.isEmpty()){
            req.append("WHERE " + JOURNAL.DATE + " <= '" + fin + "' ");
        }
        if(!fin.isEmpty() && !debut.isEmpty()){
            req.append("AND " + JOURNAL.DATE + " <= '" + fin + "' ");
        }

        if(fin.isEmpty() && debut.isEmpty()){
            if (profil.equals(profilAdminSender) || profil.equals(profilAdminPayer)){
                req.append(" WHERE " + JOURNAL.COMPTE + " IN ('" + comptePrinc + "', '" + compteComm + "', '" + compteCharge + "')");
            }else if (profil.equals(profilAdmin)){
                req.append(" WHERE " + JOURNAL.COMPTE + " NOT LIKE '" + Const.CPT_SERVICE_PRINC +   "%'");
                req.append(" AND " + JOURNAL.COMPTE + " NOT LIKE '" + Const.CPT_SERVICE_COMM +   "%'");
            }
        }else{
            if (profil.equals(profilAdminSender) || profil.equals(profilAdminPayer)){
                req.append(" AND " + JOURNAL.COMPTE + " IN ('" + comptePrinc + "', '" + compteComm + "', '" + compteCharge + "')");
            }
            else if (profil.equals(profilAdmin)){
                req.append(" AND " + JOURNAL.COMPTE + " NOT LIKE '" + Const.CPT_SERVICE_PRINC +   "%'");
                req.append(" AND " + JOURNAL.COMPTE + " NOT LIKE '" + Const.CPT_SERVICE_COMM +   "%'");
            }
        }

        req.append(" ORDER BY " + JOURNAL.DATE + " DESC ");
        return req;
    }

    public static ArrayList<Journal> getOperations(String debut, String fin, String profil,
                               int page, int perPage, boolean all) {
        String projection = JOURNAL.LIBELLE + ", " + JOURNAL.REFERENCE + ", " + JOURNAL.LIBELLECOMPTE + ", " +
                            JOURNAL.MONTANT + ", " + JOURNAL.SENS + ", " + JOURNAL.DATE;

        StringBuilder req = requestJournal(debut, fin, profil, projection);

        if (!all) {
            if (page == 1) {
                req.append(" LIMIT ").append(perPage);
            } else if (page > -1) {
                req.append(" LIMIT ").append((page - 1) * perPage).append(", ")
                        .append(perPage);
            } else {
                req.append(" LIMIT ").append(perPage);
            }
        }

        Logger.info(req.toString());

        ArrayList<Journal> operations = new ArrayList<>();
        java.sql.Connection connection = null;
        Statement ps = null;
        ResultSet res = null;
        try {
            connection = DB.getConnection();
            ps = connection.createStatement();
            res = ps.executeQuery(req.toString());

            if (res == null) {
                return new ArrayList<>();
            }

            while (res.next()) {
                operations.add(getJournalObject(res));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, ps, res);
        }
        return operations;
    }

    private static Journal getJournalObject(ResultSet r) throws SQLException {

        Journal journal = new Journal();

        journal.setLibelle(r.getString(JOURNAL.LIBELLE));
        journal.setReference(r.getString(JOURNAL.REFERENCE));
        journal.setCompte(r.getString(JOURNAL.LIBELLECOMPTE));
        journal.setMontant(Utils.formatAmount(r.getDouble(JOURNAL.MONTANT)));
        journal.setSens(r.getString(JOURNAL.SENS));
        journal.setDate(r.getString(JOURNAL.DATE));

        return journal;
    }


    public class JOURNAL {
        public final static String TABLE = "journal";
        public final static String DATE = "dateoperation";
        public final static String LIBELLE = "libelle";
        public final static String SENS = "sens";
        public final static String MONTANT = "valeur";
        public final static String REFERENCE = "reference";
        public final static String COMPTE = "numerocompte";
        public final static String LIBELLECOMPTE = "f_get_libelle_compte(numerocompte)";
        public final static String IDUSER = "iduser";
    }
}
