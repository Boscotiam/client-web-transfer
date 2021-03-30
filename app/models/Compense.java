package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.Play;
import play.db.DB;
import play.libs.Json;
import tools.Const;
import tools.DBUtils;
import tools.Utils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * Created by bosco on 31/01/2021.
 */
public class Compense {

    private String reference, partner, compte, montant, date, etat;

    public Compense() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public String getMontant() {
        return montant;
    }

    public void setMontant(String montant) {
        this.montant = montant;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public ObjectNode toObjectNode(){
        ObjectNode node = Json.newObject();
        node.put("reference", reference);
        node.put("partner", partner);
        node.put("compte", compte);
        node.put("montant", montant);
        node.put("date", date);
        node.put("etat", etat);
        return node;
    }

    public String toString(){
        return toObjectNode().toString();
    }

    public static StringBuilder requestCompense(String debut, String fin, String profil, String projection){

        String profilAdminPayer = Play.application().configuration().getString("profil.payer");
        String profilAdminMixte = Play.application().configuration().getString("profil.mixte");

        StringBuilder req = new StringBuilder(
                "SELECT " + projection + " " +
                        "FROM " + COMPENSE.TABLE + " "
        );

        String consumerId = play.mvc.Controller.ctx().session().get(Const.SESSION_CONSUMER_ID);

        if(!debut.isEmpty()){
            req.append("WHERE " + Compense.COMPENSE.DATE + " >= '" + debut + "' ");
        }
        if(!fin.isEmpty() && debut.isEmpty()){
            req.append("WHERE " + Compense.COMPENSE.DATE + " <= '" + fin + "' ");
        }
        if(!fin.isEmpty() && !debut.isEmpty()){
            req.append("AND " + Compense.COMPENSE.DATE + " <= '" + fin + "' ");
        }

        if(fin.isEmpty() && debut.isEmpty()){
            if (profil.equals(profilAdminPayer) || profil.equals(profilAdminMixte)){
                req.append("WHERE " + Compense.COMPENSE.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
        }else{
            if (profil.equals(profilAdminPayer) || profil.equals(profilAdminMixte)){
                req.append("AND " + Compense.COMPENSE.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
        }

        req.append(" ORDER BY " + Journal.JOURNAL.DATE + " DESC ");
        return req;
    }

    public static ArrayList<Compense> getCompenses(String debut, String fin, String profil,
                                                         int page, int perPage, boolean all) {
        String projection = COMPENSE.TRANSACTION + ", " + COMPENSE.PARTNER + ", " + COMPENSE.ACCOUNT + ", " +
                COMPENSE.MONTANT + ", " + COMPENSE.DATE + ", " + COMPENSE.ETAT;

        StringBuilder req = requestCompense(debut, fin, profil, projection);

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

        ArrayList<Compense> compenses = new ArrayList<>();
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
                compenses.add(getCompenseObject(res));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, ps, res);
        }
        return compenses;
    }

    private static Compense getCompenseObject(ResultSet r) throws SQLException {

        Compense compense = new Compense();

        compense.setReference(r.getString(COMPENSE.TRANSACTION));
        compense.setPartner(r.getString(COMPENSE.PARTNER));
        compense.setCompte(r.getString(COMPENSE.ACCOUNT));
        compense.setMontant(Utils.formatAmount(r.getDouble(COMPENSE.MONTANT)));
        compense.setDate(r.getString(COMPENSE.DATE));
        compense.setEtat(r.getString(COMPENSE.ETAT));

        return compense;
    }

    public class COMPENSE{

        public static final String TABLE = "compense";

        public static final String TRANSACTION = "codetransaction";

        public static final String ACCOUNT = "account";

        public static final String CONSUMER_ID = "consumerid";

        public static final String PARTNER = "f_get_partner_by_consumer_id(consumerid)";

        public static final String MONTANT = "montant";

        public static final String DATE = "dateoperation";

        public static final String ETAT = "etat";
    }
}
