package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.db.DB;
import play.libs.Json;
import tools.DBUtils;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by bosco on 14/10/2020.
 */
public class Partner {

    private int id;

    private String name;

    private String type;

    private String adress;

    private String telephone;

    private String email;

    private String country;

    private String consumerId;

    private String accountPRINC;

    private String accountCOMM;

    private String accountCHARGE;

    private String balancePRINC;

    private String balanceCOMM;

    private String balanceCHARGE;

    private String codeBank;

    private String codeGuichet;

    private String accountBank;

    private String rib;

    public Partner() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getAccountCHARGE() {
        return accountCHARGE;
    }

    public void setAccountCHARGE(String accountCHARGE) {
        this.accountCHARGE = accountCHARGE;
    }

    public String getAccountPRINC() {
        return accountPRINC;
    }

    public void setAccountPRINC(String accountPRINC) {
        this.accountPRINC = accountPRINC;
    }

    public String getAccountCOMM() {
        return accountCOMM;
    }

    public void setAccountCOMM(String accountCOMM) {
        this.accountCOMM = accountCOMM;
    }

    public String getBalancePRINC() {
        return balancePRINC;
    }

    public void setBalancePRINC(String balancePRINC) {
        this.balancePRINC = balancePRINC;
    }

    public String getBalanceCOMM() {
        return balanceCOMM;
    }

    public void setBalanceCOMM(String balanceCOMM) {
        this.balanceCOMM = balanceCOMM;
    }

    public String getBalanceCHARGE() {
        return balanceCHARGE;
    }

    public void setBalanceCHARGE(String balanceCHARGE) {
        this.balanceCHARGE = balanceCHARGE;
    }

    public String getCodeBank() {
        return codeBank;
    }

    public void setCodeBank(String codeBank) {
        this.codeBank = codeBank;
    }

    public String getCodeGuichet() {
        return codeGuichet;
    }

    public void setCodeGuichet(String codeGuichet) {
        this.codeGuichet = codeGuichet;
    }

    public String getAccountBank() {
        return accountBank;
    }

    public void setAccountBank(String accountBank) {
        this.accountBank = accountBank;
    }

    public String getRib() {
        return rib;
    }

    public void setRib(String rib) {
        this.rib = rib;
    }

    public ObjectNode toObjectNode(){
        ObjectNode objectNode = Json.newObject();
        objectNode.put("name", name);
        objectNode.put("type", type);
        objectNode.put("adress", adress);
        objectNode.put("telephone", telephone);
        objectNode.put("email", email);
        objectNode.put("country", country);
        objectNode.put("consumerId", consumerId);
        objectNode.put("accountCHARGE", accountCHARGE);
        objectNode.put("accountPRINC", accountPRINC);
        objectNode.put("accountCOMM", accountCOMM);
        objectNode.put("balanceCHARGE", balanceCHARGE);
        objectNode.put("balancePRINC", balancePRINC);
        objectNode.put("balanceCOMM", balanceCOMM);
        return objectNode;
    }

    @Override
    public String toString() {
        return toObjectNode().toString();
    }

    public static ArrayList<Partner> getPartners(String name, int page, int perPage, boolean all) {

        String projection = PARTNER.ID + ", " + PARTNER.NAME + ", " + PARTNER.TYPE + ", " + PARTNER.ADRESSE + ", " +
                PARTNER.TELEPHONE + ", " + PARTNER.EMAIL + ", " + PARTNER.COUNTRY + ", " + PARTNER.CONSUMER_ID + ", " +
                PARTNER.ACCOUNTS + ", " + PARTNER.SOLDES + ", " + PARTNER.BANK_INFOS + "";

        StringBuilder req = requestPartner(name, projection);

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

        ArrayList<Partner> partners = new ArrayList<>();
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
                partners.add(getPartnerObject(res));
            }
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return new ArrayList<>();
        } finally {
            DBUtils.closeQuietly(connection, ps, res);
        }
        return partners;
    }

    public static StringBuilder requestPartner(String name, String projection){


        StringBuilder req = new StringBuilder(
                "SELECT " + projection + " " +
                        "FROM " + PARTNER.TABLE + " "
        );

        if(!name.isEmpty()){
            req.append("WHERE " +PARTNER.NAME + " like '%" + name + "%' ");
        }

        return req;
    }

    private static Partner getPartnerObject(ResultSet r) throws SQLException {

        Partner partner = new Partner();

        partner.setId(r.getInt(PARTNER.ID));
        partner.setName(r.getString(PARTNER.NAME));
        partner.setType(r.getString(PARTNER.TYPE));
        partner.setAdress(r.getString(PARTNER.ADRESSE));
        partner.setTelephone(r.getString(PARTNER.TELEPHONE));
        partner.setEmail(r.getString(PARTNER.EMAIL));
        partner.setCountry(r.getString(PARTNER.COUNTRY));
        partner.setConsumerId(r.getString(PARTNER.CONSUMER_ID));
        String accounts = r.getString(PARTNER.ACCOUNTS);
        String soldes = r.getString(PARTNER.SOLDES);
        String bankdata = r.getString(PARTNER.BANK_INFOS);
        Logger.info("Accounts: " + accounts);
        Logger.info("Soldes: " + soldes);
        Logger.info("Bank data: " + bankdata);
        String[] tabAccounts = accounts.split("\\|");
        String[] tabBalances = soldes.split("\\|");
        partner.setAccountPRINC(tabAccounts[0]);
        partner.setAccountCOMM(tabAccounts[1]);
        partner.setAccountCHARGE(tabAccounts[2]);
        partner.setBalancePRINC(tabBalances[0]);
        partner.setBalanceCOMM(tabBalances[1]);
        partner.setBalanceCHARGE(tabBalances[2]);
        if(!bankdata.isEmpty()){
            String[] tabBank = bankdata.split("\\|");
            partner.setCodeBank(tabBank[0]);
            partner.setCodeGuichet(tabBank[1]);
            partner.setAccountBank(tabBank[2]);
            partner.setRib(tabBank[3]);
        }else{
            partner.setCodeBank("");
            partner.setCodeGuichet("");
            partner.setAccountBank("");
            partner.setRib("");
        }

        return partner;
    }

    public static AddPartnerResult addPartner(String nom, String type, String adress, String telephone,
                                          String email, String country) {
        java.sql.Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_create_compte_partenaire(?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.VARCHAR);
            callableStatement.registerOutParameter(5, Types.VARCHAR);
            callableStatement.registerOutParameter(6, Types.VARCHAR);
            callableStatement.registerOutParameter(7, Types.VARCHAR);
            callableStatement.setString(8, nom);
            callableStatement.setString(9, type);
            callableStatement.setString(10, adress);
            callableStatement.setString(11, telephone);
            callableStatement.setString(12, email);
            callableStatement.setString(13, country);
            callableStatement.execute();
            int value = callableStatement.getInt(1);
            String message = callableStatement.getString(2);
            String consumerId = callableStatement.getString(3);
            String accountCHARGE = callableStatement.getString(7);
            return new AddPartnerResult(value, message, consumerId, accountCHARGE);
        } catch (SQLException e) {
            Logger.error(e.getMessage());
            return null;
        } finally {
            DBUtils.closeQuietly(connection, callableStatement);
        }
    }

    public static ProcedureResult addBankPartner(String consumerId, String accountCHARGE, String bank,
                                                  String guichet, String account, String rib) {
        java.sql.Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_add_bank_infos_partenaire(?,?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.setString(3, accountCHARGE);
            callableStatement.setString(4, consumerId);
            callableStatement.setString(5, bank);
            callableStatement.setString(6, guichet);
            callableStatement.setString(7, account);
            callableStatement.setString(8, rib);
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

    /*DELIMITER ;;
    CREATE DEFINER=`root`@`localhost` PROCEDURE `ps_depot_execute`(
    OUT resultat INT, OUT message VARCHAR(100), OUT VARCODETRANSACTION VARCHAR(30),
    OUT VARCOMMISSION DOUBLE, OUT VARSOLDECHARGEFIN DOUBLE,
    IN VARPARTNER VARCHAR(250), IN VARAMOUNT DOUBLE, IN VARUSER INT)*/

    public static DepotResult depotExecute(String consumerId, double montant, int user) {
        java.sql.Connection connection = null;
        CallableStatement callableStatement = null;
        String req = "{ call ps_depot_execute(?,?,?,?,?,?,?,?)}";
        try {
            connection = DB.getConnection();
            callableStatement = connection.prepareCall(req);
            callableStatement.registerOutParameter(1, Types.INTEGER);
            callableStatement.registerOutParameter(2, Types.VARCHAR);
            callableStatement.registerOutParameter(3, Types.VARCHAR);
            callableStatement.registerOutParameter(4, Types.DOUBLE);
            callableStatement.registerOutParameter(5, Types.DOUBLE);
            callableStatement.setString(6, consumerId);
            callableStatement.setDouble(7, montant);
            callableStatement.setInt(8, user);
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

    public class PARTNER{

        public static final String TABLE = "partenaire";

        public static final String ID = "id";

        public static final String NAME = "nom";

        public static final String TYPE = "type";

        public static final String TELEPHONE = "telephone";

        public static final String EMAIL = "email";

        public static final String COUNTRY = "codepays";

        public static final String CONSUMER_ID = "consumerid";

        public static final String ADRESSE = "adresse";

        public static final String ACCOUNTS = "f_get_accounts_partner(consumerid)";

        public static final String SOLDES = "f_get_accounts_soldes_partner(consumerid)";

        public static final String BANK_INFOS = "f_get_bank_infos_partner(consumerid)";

    }
}
