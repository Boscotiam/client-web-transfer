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
 * Created by mac on 21/02/2021.
 */
public class Transaction {

    private String transaction;

    private String sender;

    private String montant;

    private String date;

    private String transmetter;

    private String transmetterTel;

    private String beneficiaire;

    private String beneficiaireTel;

    private String etat;

    private String payer;

    private String payReference;

    private String datePay;

    public Transaction() {
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
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

    public String getTransmetter() {
        return transmetter;
    }

    public void setTransmetter(String transmetter) {
        this.transmetter = transmetter;
    }

    public String getTransmetterTel() {
        return transmetterTel;
    }

    public void setTransmetterTel(String transmetterTel) {
        this.transmetterTel = transmetterTel;
    }

    public String getBeneficiaire() {
        return beneficiaire;
    }

    public void setBeneficiaire(String beneficiaire) {
        this.beneficiaire = beneficiaire;
    }

    public String getBeneficiaireTel() {
        return beneficiaireTel;
    }

    public void setBeneficiaireTel(String beneficiaireTel) {
        this.beneficiaireTel = beneficiaireTel;
    }

    public String getEtat() {
        return etat;
    }

    public void setEtat(String etat) {
        this.etat = etat;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getPayReference() {
        return payReference;
    }

    public void setPayReference(String payReference) {
        this.payReference = payReference;
    }

    public String getDatePay() {
        return datePay;
    }

    public void setDatePay(String datePay) {
        this.datePay = datePay;
    }

    public ObjectNode toObjectNode(){
        ObjectNode objectNode = Json.newObject();
        objectNode.put("transaction", transaction);
        objectNode.put("sender", sender);
        objectNode.put("montant", montant);
        objectNode.put("date", date);
        objectNode.put("transmetter", transmetter);
        objectNode.put("transmetterTel", transmetterTel);
        objectNode.put("beneficiaire", beneficiaire);
        objectNode.put("beneficiaireTel", beneficiaireTel);
        objectNode.put("etat", etat);
        objectNode.put("payer", payer);
        objectNode.put("payReference", payReference);
        objectNode.put("datePay", datePay);
        return objectNode;
    }

    @Override
    public String toString() {
        return toObjectNode().toString();
    }

    public static StringBuilder requestTransaction(String debut, String fin, String partner, String profil, String projection){

        String profilAdminSender = Play.application().configuration().getString("profil.sender");
        String profilAdminPayer = Play.application().configuration().getString("profil.payer");
        String profilAdminMixte = Play.application().configuration().getString("profil.mixte");
        String profilAdmin = Play.application().configuration().getString("profil.admin");

        StringBuilder req = new StringBuilder(
                "SELECT " + projection + " " +
                        "FROM " + TRANSACTION.TABLE + " "
        );

        String consumerId = play.mvc.Controller.ctx().session().get(Const.SESSION_CONSUMER_ID);

        if(!debut.isEmpty()){
            req.append("WHERE " + TRANSACTION.DATE + " >= '" + debut + "' ");
        }
        if(!fin.isEmpty() && debut.isEmpty()){
            req.append("WHERE " + TRANSACTION.DATE + " <= '" + fin + "' ");
        }
        if(!fin.isEmpty() && !debut.isEmpty()){
            req.append("AND " + TRANSACTION.DATE + " <= '" + fin + "' ");
        }

        if(fin.isEmpty() && debut.isEmpty()){
            if (profil.equals(profilAdminSender)){
                req.append("WHERE " + TRANSACTION.CONSUMER_ID_SENDER + " = '" + consumerId +  "' ");
            }
            if (profil.equals(profilAdminPayer)){
                req.append("WHERE " + TRANSACTION.CONSUMER_ID_PAYER + " = '" + consumerId +  "' ");
            }
            if (profil.equals(profilAdminMixte)){
                req.append("WHERE (" + TRANSACTION.CONSUMER_ID_SENDER + " = '" + consumerId +  "' OR " + TRANSACTION.CONSUMER_ID_PAYER + " = '" + consumerId + "')");
            }
            if (profil.equals(profilAdmin) && !partner.isEmpty()){
                req.append("WHERE (" + TRANSACTION.CONSUMER_ID_SENDER + " = '" + partner +  "' ");
                req.append("OR " + TRANSACTION.CONSUMER_ID_PAYER + " = '" + partner +  "') ");
            }
        }else{
            if (profil.equals(profilAdminSender)){
                req.append("AND " + TRANSACTION.CONSUMER_ID_SENDER + " = '" + consumerId +  "' ");
            }
            if (profil.equals(profilAdminPayer)){
                req.append("AND " + TRANSACTION.CONSUMER_ID_PAYER + " = '" + consumerId +  "' ");
            }
            if (profil.equals(profilAdminMixte)){
                req.append("AND (" + TRANSACTION.CONSUMER_ID_SENDER + " = '" + consumerId +  "' OR " + TRANSACTION.CONSUMER_ID_PAYER + " = '" + consumerId + "')");
            }
            if (profil.equals(profilAdmin) && !partner.isEmpty()){
                req.append("AND (" + TRANSACTION.CONSUMER_ID_SENDER + " = '" + partner +  "' ");
                req.append("OR " + TRANSACTION.CONSUMER_ID_PAYER + " = '" + partner +  "') ");
            }
        }

        req.append(" ORDER BY " + Transaction.TRANSACTION.DATE + " DESC ");
        return req;
    }

    public static ArrayList<Transaction> getTransactions(String debut, String fin, String partner, String profil,
                                                   int page, int perPage, boolean all) {
        String projection = TRANSACTION.TRANSACTION + ", " + TRANSACTION.SENDER + ", " + TRANSACTION.MONTANT + ", " +
                TRANSACTION.DATE + ", " + TRANSACTION.TRANSMETTER + ", " + TRANSACTION.TRANSMETTER_TEL + ", " +
                TRANSACTION.BENEFICIAIRE + ", " + TRANSACTION.BENEFICIAIRE_TEL + ", " +
                TRANSACTION.ETAT + ", " + TRANSACTION.PAYER + ", " +
                TRANSACTION.PAY_REF + ", " + TRANSACTION.PAY_DATE;

        StringBuilder req = requestTransaction(debut, fin, partner, profil, projection);

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

        ArrayList<Transaction> transactions = new ArrayList<>();
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
                transactions.add(getTransactionObject(res));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, ps, res);
        }
        return transactions;
    }

    private static Transaction getTransactionObject(ResultSet r) throws SQLException {

        Transaction transaction = new Transaction();

        transaction.setTransaction(r.getString(TRANSACTION.TRANSACTION));
        transaction.setSender(r.getString(TRANSACTION.SENDER));
        transaction.setMontant(Utils.formatAmount(r.getDouble(TRANSACTION.MONTANT)));
        transaction.setDate(r.getString(TRANSACTION.DATE));
        transaction.setTransmetter(r.getString(TRANSACTION.TRANSMETTER));
        transaction.setTransmetterTel(r.getString(TRANSACTION.TRANSMETTER_TEL));
        transaction.setBeneficiaire(r.getString(TRANSACTION.BENEFICIAIRE));
        transaction.setBeneficiaireTel(r.getString(TRANSACTION.BENEFICIAIRE_TEL));
        transaction.setEtat(r.getString(TRANSACTION.ETAT));
        transaction.setPayer((r.getString(TRANSACTION.PAYER) == null || r.getString(TRANSACTION.PAYER).isEmpty()) ? "NOT PAYED" : r.getString(TRANSACTION.PAYER));
        transaction.setPayReference((r.getString(TRANSACTION.PAY_REF) == null || r.getString(TRANSACTION.PAY_REF).isEmpty()) ? "NOT PAYED" : r.getString(TRANSACTION.PAY_REF));
        transaction.setDatePay((r.getString(TRANSACTION.PAY_DATE) == null || r.getString(TRANSACTION.PAY_DATE).isEmpty()) ? "NOT PAYED" : r.getString(TRANSACTION.PAY_DATE));


        return transaction;
    }

    public static ProcedureResult updateTransaction(String transaction, String transmetter, String transmetterTel,
                                             String beneficiaire, String beneficiaireTel) {
        java.sql.Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_update_data_transaction(?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.setString(3, transaction);
            callableStatement.setString(4, transmetter);
            callableStatement.setString(5, transmetterTel);
            callableStatement.setString(6, beneficiaire);
            callableStatement.setString(7, beneficiaireTel);
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

    public class TRANSACTION{

        public static final String TABLE = "transaction";

        public static final String TRANSACTION = "codetransaction";

        public static final String CONSUMER_ID_SENDER = "consumeridsender";

        public static final String SENDER = "f_get_partner_by_consumer_id(consumeridsender)";

        public static final String MONTANT = "montant";

        public static final String DATE = "dateoperation";

        public static final String TRANSMETTER = "transmetter";

        public static final String TRANSMETTER_TEL = "transmettertel";

        public static final String BENEFICIAIRE = "beneficiaire";

        public static final String BENEFICIAIRE_TEL = "beneficiairetel";

        public static final String ETAT = "etat";

        public static final String PAY_REF = "referencepayment";

        public static final String CONSUMER_ID_PAYER = "consumeridpayer";

        public static final String PAYER = "f_get_partner_by_consumer_id(consumeridpayer)";

        public static final String PAY_DATE = "datepaiement";

    }
}
