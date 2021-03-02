package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.i18n.Messages;
import play.libs.F;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import tools.Const;
import tools.DBUtils;
import tools.Log;
import tools.Utils;

import java.util.ArrayList;

import static play.libs.F.Promise.promise;
import static tools.Const.SESSION_CONNECTED;
import static tools.Const.SESSION_PROFIL;

/**
 * Created by mac on 24/02/2021.
 */
public class PartnerManager extends Controller {

    public  F.Promise<Result> getPartners(String name, int page, int perPage){
        return promise(() -> getPartnerPromise(name, page, perPage));
    }

    private static Result getPartnerPromise(String name, int page, int per_page) {

        ObjectNode node = Json.newObject();
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) {
            node = Utils.getObjectNode(301, "Session Error");
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }

        if (!Const.PROFIL_ADMIN.equals(session(SESSION_PROFIL)) ) {
            node.put("result", "nok");
            node.put("message", Messages.get("error.bad.profil"));
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }

        String user = session(Const.SESSION_USER_NAME);
        Log.logActionHeader(user, "getPartnerPromise");
        Log.logActionInput(request().queryString());

        ArrayList<Partner> partners;

        int total = 0;
        partners = Partner.getPartners(name, page, per_page, false);

        if(partners.size() == 0){
            node.put("code", 201);
            node.put("message", Messages.get("label.empty.list"));
            node.put("countries", Json.toJson(Country.getCountries()));
            Log.logActionOutput(node.toString());
            return ok(node);
        }

        node = Json.newObject();

        total = DBUtils.getTotalRows(Partner.requestPartner(name, "COUNT(*) as total").toString());

        int pageNumber = total / per_page;
        if (total % per_page > 0) {
            pageNumber++;
        }

        node.put("code", 200);
        node.put("total_page", pageNumber);
        node.put("current_page", page);
        node.put("total", total);
        node.put("per_page", per_page);
        node.put("partners", Json.toJson(partners));
        node.put("countries", Json.toJson(Country.getCountries()));
        node.put("banks", Json.toJson(Bank.getBanks()));
        Log.logActionOutput(node.toString());
        return ok(node);
    }

    public F.Promise<Result> addPartner() {
        return promise(() -> {
            ObjectNode node = Json.newObject();
            String session = session(SESSION_CONNECTED);
            if (session == null) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.session"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            String user = session(Const.SESSION_USER_NAME);
            Log.logActionHeader(user, "addPartner");
            Log.logActionInputJson(request().body().asJson().toString());

            if (!Const.PROFIL_ADMIN.equals(session(SESSION_PROFIL)) ) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            JsonNode json = request().body().asJson();

            String name = json.findPath("name").textValue();
            String type = json.findPath("type").textValue();
            String adress = json.findPath("adress").textValue();
            String telephone = json.findPath("telephone").textValue();
            String email = json.findPath("email").textValue();
            String country = json.findPath("country").textValue();

            String bank = json.findPath("bank").textValue();
            String guichet = json.findPath("guichet").textValue();
            String account = json.findPath("account").textValue();
            String rib = json.findPath("rib").textValue();

            if (!Utils.checkData(name, type, adress, telephone, email, country)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
            AddPartnerResult result = Partner.addPartner(name, type, adress, telephone, email, country);

            if (result.getResult() == 0) {
                node.put("code", 201);
                node.put("message", result.getMessage());
                return ok(node);
            } else {

                if(Utils.checkData(bank, guichet, account, rib)){
                    Partner.addBankPartner(result.getConsumerId(), result.getCompteCharge(),
                            bank, guichet, account, rib);
                }

                node.put("code", 200);
                node.put("message", result.getMessage());
                String message = Messages.get("message.hi") + " " + name + ", " +
                        Messages.get("message.create.partner") + "." ;
                DBUtils.saveNotification(telephone, "SMS", Messages.get("message.header"), message);
                DBUtils.saveNotification(email, "MAIL", Messages.get("message.header"), message);
            }
            Log.logActionOutput(node.toString());
            return ok(node);

        });
    }

    public F.Promise<Result> depotSender() {
        return promise(() -> {
            ObjectNode node = Json.newObject();
            String session = session(SESSION_CONNECTED);
            if (session == null) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.session"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            String user = session(Const.SESSION_USER_NAME);
            Log.logActionHeader(user, "depotSender");
            Log.logActionInputJson(request().body().asJson().toString());

            if (!Const.PROFIL_ADMIN.equals(session(SESSION_PROFIL)) ) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            JsonNode json = request().body().asJson();

            String partner = json.findPath("partner").textValue();
            String telephone = json.findPath("telephone").textValue();
            String email = json.findPath("email").textValue();
            String montant = json.findPath("montant").textValue();
            String idUser = session(Const.SESSION_USER_ID);

            if (!Utils.checkData(partner, telephone, email, montant, idUser)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
            DepotResult result = Partner.depotExecute(partner, Double.parseDouble(montant), Integer.parseInt(idUser));

            if (result.getResult() == 0) {
                node.put("code", 201);
                node.put("message", result.getMessage());
                return ok(node);
            } else {


                /*Bonjour, vous venez de recevoir un dépôt SUNRISE TRANSFER. Nouvelle balance de votre compte charge: DEPOT21h2186109. message.depot.balance: 5000000.0.*/

                node.put("code", 200);
                node.put("message", result.getMessage());
                String message = Messages.get("message.hi") + ", " +
                        Messages.get("message.depot.partner") + ". " +
                        Messages.get("message.depot.transaction") + ": " + result.getTransaction() + ". " +
                        Messages.get("message.depot.balance") + ": " + result.getBalanceCHARGE() + "." ;
                DBUtils.saveNotification(telephone, "SMS", Messages.get("message.header"), message);
                DBUtils.saveNotification(email, "MAIL", Messages.get("message.header"), message);
            }
            Log.logActionOutput(node.toString());
            return ok(node);

        });
    }

}
