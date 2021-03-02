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
public class Depot {

    private String reference, partner, compte, montant, date, etat;

    public Depot() {
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

    public static StringBuilder requestDepot(String debut, String fin, String profil, String projection){

        String profilAdminSender = Play.application().configuration().getString("profil.sender");

        StringBuilder req = new StringBuilder(
                "SELECT " + projection + " " +
                        "FROM " + DEPOT.TABLE + " "
        );

        String consumerId = play.mvc.Controller.ctx().session().get(Const.SESSION_CONSUMER_ID);

        if(!debut.isEmpty()){
            req.append("WHERE " + Depot.DEPOT.DATE + " >= '" + debut + "' ");
        }
        if(!fin.isEmpty() && debut.isEmpty()){
            req.append("WHERE " + Depot.DEPOT.DATE + " <= '" + fin + "' ");
        }
        if(!fin.isEmpty() && !debut.isEmpty()){
            req.append("AND " + Depot.DEPOT.DATE + " <= '" + fin + "' ");
        }

        if(fin.isEmpty() && debut.isEmpty()){
            if (profil.equals(profilAdminSender)){
                req.append("WHERE " + Depot.DEPOT.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
        }else{
            if (profil.equals(profilAdminSender)){
                req.append("AND " + Depot.DEPOT.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
        }

        req.append(" ORDER BY " + Journal.JOURNAL.DATE + " DESC ");
        return req;
    }

    public static ArrayList<Depot> getDepots(String debut, String fin, String profil,
                                                   int page, int perPage, boolean all) {
        String projection = DEPOT.TRANSACTION + ", " + DEPOT.PARTNER + ", " + DEPOT.ACCOUNT + ", " +
                DEPOT.MONTANT + ", " + DEPOT.DATE + ", " + DEPOT.ETAT;

        StringBuilder req = requestDepot(debut, fin, profil, projection);

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

        ArrayList<Depot> compenses = new ArrayList<>();
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
                compenses.add(getDepotObject(res));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, ps, res);
        }
        return compenses;
    }

    private static Depot getDepotObject(ResultSet r) throws SQLException {

        Depot depot = new Depot();

        depot.setReference(r.getString(DEPOT.TRANSACTION));
        depot.setPartner(r.getString(DEPOT.PARTNER));
        depot.setCompte(r.getString(DEPOT.ACCOUNT));
        depot.setMontant(Utils.formatAmount(r.getDouble(DEPOT.MONTANT)));
        depot.setDate(r.getString(DEPOT.DATE));
        depot.setEtat(r.getString(DEPOT.ETAT));

        return depot;
    }

    public class DEPOT{

        public static final String TABLE = "depotsender";

        public static final String TRANSACTION = "codetransaction";

        public static final String ACCOUNT = "account";

        public static final String CONSUMER_ID = "consumerid";

        public static final String PARTNER = "f_get_partner_by_consumer_id(consumerid)";

        public static final String MONTANT = "montant";

        public static final String DATE = "dateoperation";

        public static final String ETAT = "etat";
        
    }
}
