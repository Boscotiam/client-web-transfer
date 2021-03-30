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
public class DepotGuichet {

    private String reference, codeGuichet, guichet, compte, montant, date, etat;

    public DepotGuichet() {
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

    public String getCodeGuichet() {
        return codeGuichet;
    }

    public void setCodeGuichet(String codeGuichet) {
        this.codeGuichet = codeGuichet;
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

    public String getGuichet() {
        return guichet;
    }

    public void setGuichet(String guichet) {
        this.guichet = guichet;
    }

    public ObjectNode toObjectNode(){
        ObjectNode node = Json.newObject();
        node.put("reference", reference);
        node.put("guichet", guichet);
        node.put("compte", compte);
        node.put("montant", montant);
        node.put("date", date);
        node.put("etat", etat);
        return node;
    }

    public String toString(){
        return toObjectNode().toString();
    }

    public static StringBuilder requestDepotGuichet(String debut, String fin, String profil, String projection){

        String profilAdminSender = Play.application().configuration().getString("profil.sender");
        String profilAdminMixte = Play.application().configuration().getString("profil.mixte");

        StringBuilder req = new StringBuilder(
                "SELECT " + projection + " " +
                        "FROM " + DEPOTGUICHET.TABLE + " "
        );

        String consumerId = play.mvc.Controller.ctx().session().get(Const.SESSION_CONSUMER_ID);

        if(!debut.isEmpty()){
            req.append("WHERE " + DepotGuichet.DEPOTGUICHET.DATE + " >= '" + debut + "' ");
        }
        if(!fin.isEmpty() && debut.isEmpty()){
            req.append("WHERE " + DepotGuichet.DEPOTGUICHET.DATE + " <= '" + fin + "' ");
        }
        if(!fin.isEmpty() && !debut.isEmpty()){
            req.append("AND " + DepotGuichet.DEPOTGUICHET.DATE + " <= '" + fin + "' ");
        }

        if(fin.isEmpty() && debut.isEmpty()){
            if (profil.equals(profilAdminSender)){
                req.append("WHERE " + DepotGuichet.DEPOTGUICHET.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
            if (profil.equals(profilAdminMixte)){
                req.append("WHERE " + DepotGuichet.DEPOTGUICHET.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
        }else{
            if (profil.equals(profilAdminSender)){
                req.append("AND " + DepotGuichet.DEPOTGUICHET.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
            if (profil.equals(profilAdminMixte)){
                req.append("AND " + DepotGuichet.DEPOTGUICHET.CONSUMER_ID + " = '" + consumerId +  "' ");
            }
        }

        req.append(" ORDER BY " + DepotGuichet.DEPOTGUICHET.DATE + " DESC ");
        return req;
    }

    public static ArrayList<DepotGuichet> getDepotGuichets(String debut, String fin, String profil,
                                             int page, int perPage, boolean all) {
        String projection = DEPOTGUICHET.TRANSACTION + ", " + DEPOTGUICHET.CODE_GUICHET + ", " +
                DEPOTGUICHET.GUICHET + ", " + DEPOTGUICHET.ACCOUNT + ", " +
                DEPOTGUICHET.MONTANT + ", " + DEPOTGUICHET.DATE + ", " + DEPOTGUICHET.ETAT;

        StringBuilder req = requestDepotGuichet(debut, fin, profil, projection);

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

        ArrayList<DepotGuichet> compenses = new ArrayList<>();
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
                compenses.add(getDepotGuichetObject(res));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, ps, res);
        }
        return compenses;
    }

    private static DepotGuichet getDepotGuichetObject(ResultSet r) throws SQLException {

        DepotGuichet depot = new DepotGuichet();

        depot.setReference(r.getString(DEPOTGUICHET.TRANSACTION));
        depot.setCodeGuichet(r.getString(DEPOTGUICHET.CODE_GUICHET));
        depot.setGuichet(r.getString(DEPOTGUICHET.GUICHET));
        depot.setCompte(r.getString(DEPOTGUICHET.ACCOUNT));
        depot.setMontant(Utils.formatAmount(r.getDouble(DEPOTGUICHET.MONTANT)));
        depot.setDate(r.getString(DEPOTGUICHET.DATE));
        depot.setEtat(r.getString(DEPOTGUICHET.ETAT));

        return depot;
    }

    public class DEPOTGUICHET{

        public static final String TABLE = "depotguichet";

        public static final String TRANSACTION = "codetransaction";

        public static final String ACCOUNT = "account";

        public static final String CONSUMER_ID = "consumerid";

        public static final String CODE_GUICHET = "guichet";

        public static final String GUICHET = "f_get_guichet_by_code(guichet)";

        public static final String MONTANT = "montant";

        public static final String DATE = "dateoperation";

        public static final String ETAT = "etat";

    }
}
