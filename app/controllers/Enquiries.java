package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import models.*;
import play.Logger;
import play.Play;
import play.i18n.Messages;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WSAuthScheme;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContextExecutor;
import tools.Const;
import tools.DBUtils;
import tools.Log;
import tools.Utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletionStage;

import static play.libs.F.Promise.promise;

/**
 * Created by mac on 19/02/2021.
 */
public class Enquiries extends Controller {

    private final WSClient ws;
    private final ExecutionContextExecutor exec;

    @Inject
    public Enquiries(WSClient ws, ExecutionContextExecutor exec) {
        this.ws = ws;
        this.exec = exec;
    }

    public  F.Promise<Result> getJournal(String debut, String fin, int page, int perPage){
        return promise(() -> getJournalPromise(debut, fin, page, perPage));
    }

    private static Result getJournalPromise(String debut, String fin, int page, int per_page) {

        ObjectNode node = Json.newObject();
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) {
            node = Utils.getObjectNode(301, "Session Error");
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }

        String user = session(Const.SESSION_USER_NAME);
        Log.logActionHeader(user, "getJournalPromise");
        Log.logActionInput(request().queryString());

        ArrayList<Journal> operations;

        int total = 0;
        operations = Journal.getOperations(debut, fin, session(Const.SESSION_PROFIL), page, per_page, false);

        if(operations.size() == 0){
            node.put("code", 201);
            node.put("message", Messages.get("label.empty.list"));
            node.put("totalTransactions", 0);
            node.put("principalBalance", session(Const.SESSION_PARTNER_BALANCE_PRINC));
            node.put("feesBalance", session(Const.SESSION_PARTNER_BALANCE_COMM));
            node.put("chargeBalance", session(Const.SESSION_PARTNER_BALANCE_CHARGE));
            node.put("countries", Json.toJson(Country.getCountries()));
            Log.logActionOutput(node.toString());
            return ok(node);
        }

        node = Json.newObject();

        total = DBUtils.getTotalRows(Journal.requestJournal(debut, fin,
                session(Const.SESSION_PROFIL),
                "COUNT(*) as total").toString());

        int pageNumber = total / per_page;
        if (total % per_page > 0) {
            pageNumber++;
        }

        int totalTransactions = DBUtils.getTotalRows(Transaction.requestTransaction("", "", "",
                session(Const.SESSION_PROFIL),
                "COUNT(*) as total").toString());

        node.put("code", 200);
        node.put("total_page", pageNumber);
        node.put("current_page", page);
        node.put("total", total);
        node.put("per_page", per_page);
        node.put("totalTransactions", totalTransactions);
        node.put("principalBalance", session(Const.SESSION_PARTNER_BALANCE_PRINC));
        node.put("feesBalance", session(Const.SESSION_PARTNER_BALANCE_COMM));
        node.put("chargeBalance", session(Const.SESSION_PARTNER_BALANCE_CHARGE));
        node.put("operations", Json.toJson(operations));
        node.put("countries", Json.toJson(Country.getCountries()));
        Log.logActionOutput(node.toString());
        return ok(node);
    }

    public  F.Promise<Result> getTransactions(String debut, String fin, String partner,  int page, int perPage){
        return promise(() -> getTransactionsPromise(debut, fin, partner, page, perPage));
    }

    private static Result getTransactionsPromise(String debut, String fin, String partner, int page, int per_page) {

        ObjectNode node = Json.newObject();
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) {
            node = Utils.getObjectNode(301, "Session Error");
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }

        String user = session(Const.SESSION_USER_NAME);
        Log.logActionHeader(user, "getTransactionPromise");
        Log.logActionInput(request().queryString());

        ArrayList<Transaction> transactions;

        int total = 0;
        transactions = Transaction.getTransactions(debut, fin, partner, session(Const.SESSION_PROFIL), page, per_page, false);

        if(transactions.size() == 0){
            node = Utils.getObjectNode(201, Messages.get("label.empty.list"));
            Log.logActionOutput(node.toString());
            return ok(node);
        }

        node = Json.newObject();

        total = DBUtils.getTotalRows(Transaction.requestTransaction(debut, fin, partner,
                session(Const.SESSION_PROFIL),
                "COUNT(*) as total").toString());

        int pageNumber = total / per_page;
        if (total % per_page > 0) {
            pageNumber++;
        }

        node.put("code", 200);
        node.put("total_page", pageNumber);
        node.put("current_page", page);
        node.put("total", total);
        node.put("per_page", per_page);
        node.put("transactions", Json.toJson(transactions));
        Log.logActionOutput(node.toString());
        return ok(node);
    }

    public  F.Promise<Result> getCompenses(String debut, String fin, int page, int perPage){
        return promise(() -> getCompensePromise(debut, fin, page, perPage));
    }

    private static Result getCompensePromise(String debut, String fin, int page, int per_page) {

        ObjectNode node = Json.newObject();
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) {
            node = Utils.getObjectNode(301, "Session Error");
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }

        String user = session(Const.SESSION_USER_NAME);
        Log.logActionHeader(user, "getTransactionPromise");
        Log.logActionInput(request().queryString());

        ArrayList<Compense> compenses;

        int total = 0;
        compenses = Compense.getCompenses(debut, fin, session(Const.SESSION_PROFIL), page, per_page, false);

        if(compenses.size() == 0){
            node = Utils.getObjectNode(201, Messages.get("label.empty.list"));
            Log.logActionOutput(node.toString());
            return ok(node);
        }

        node = Json.newObject();

        total = DBUtils.getTotalRows(Compense.requestCompense(debut, fin,
                session(Const.SESSION_PROFIL),
                "COUNT(*) as total").toString());

        int pageNumber = total / per_page;
        if (total % per_page > 0) {
            pageNumber++;
        }

        node.put("code", 200);
        node.put("total_page", pageNumber);
        node.put("current_page", page);
        node.put("total", total);
        node.put("per_page", per_page);
        node.put("compenses", Json.toJson(compenses));
        Log.logActionOutput(node.toString());
        return ok(node);
    }

    public  F.Promise<Result> getDepots(String debut, String fin, int page, int perPage){
        return promise(() -> getDepotPromise(debut, fin, page, perPage));
    }

    private static Result getDepotPromise(String debut, String fin, int page, int per_page) {

        ObjectNode node = Json.newObject();
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) {
            node = Utils.getObjectNode(301, "Session Error");
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }

        String user = session(Const.SESSION_USER_NAME);
        Log.logActionHeader(user, "getDepotPromise");
        Log.logActionInput(request().queryString());

        ArrayList<Depot> depots;

        int total = 0;
        depots = Depot.getDepots(debut, fin, session(Const.SESSION_PROFIL), page, per_page, false);

        if(depots.size() == 0){
            node = Utils.getObjectNode(201, Messages.get("label.empty.list"));
            Log.logActionOutput(node.toString());
            return ok(node);
        }

        node = Json.newObject();

        total = DBUtils.getTotalRows(Depot.requestDepot(debut, fin,
                session(Const.SESSION_PROFIL),
                "COUNT(*) as total").toString());

        int pageNumber = total / per_page;
        if (total % per_page > 0) {
            pageNumber++;
        }

        node.put("code", 200);
        node.put("total_page", pageNumber);
        node.put("current_page", page);
        node.put("total", total);
        node.put("per_page", per_page);
        node.put("depots", Json.toJson(depots));
        Log.logActionOutput(node.toString());
        return ok(node);
    }

    public  F.Promise<Result> getPartnerData() {
        return promise(() -> {

            Log.logActionHeader("getPartnerData");

            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("data.partner.url");

            url += "/" + session(Const.SESSION_CONSUMER_ID);
            url += "/" + session(Const.SESSION_DEVICE_ID);

            //Logger.info("Token: " + session(Const.SESSION_TOKEN_APP));
            Logger.info("Token: " + Const.SESSION_TOKEN_APP);
            Logger.info("URL: " + url);
            if (Const.SESSION_TOKEN_APP == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            servResponse = requestingGetPartnerData(url).thenApply(WSResponse::asJson);
            if (servResponse == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            JsonNode jsonData = servResponse.toCompletableFuture().join();

            PartnerResponse partnerResponse = new PartnerResponse();

            partnerResponse.setCode(jsonData.findPath("code").intValue());
            partnerResponse.setMessage(jsonData.findPath("message").textValue());
            if(partnerResponse.getCode() == Const.HTTP_SUCCESS){

                JsonNode part = jsonData.findPath("partner");
                Partner partnerUser = new Partner();

                partnerUser.setName(part.findPath("name").textValue());
                partnerUser.setType(part.findPath("type").textValue());
                partnerUser.setAdress(part.findPath("adress").textValue());
                partnerUser.setTelephone(part.findPath("telephone").textValue());
                partnerUser.setEmail(part.findPath("email").textValue());
                partnerUser.setCountry(part.findPath("country").textValue());
                partnerUser.setAccountPRINC(part.findPath("accountPRINC").textValue());
                partnerUser.setAccountCOMM(part.findPath("accountCOMM").textValue());
                partnerUser.setAccountCHARGE(part.findPath("accountCHARGE").textValue());
                partnerUser.setBalancePRINC(Utils.formatAmount(part.findPath("balancePRINC").doubleValue()));
                partnerUser.setBalanceCOMM(Utils.formatAmount(part.findPath("balanceCOMM").doubleValue()));
                partnerUser.setBalanceCHARGE(Utils.formatAmount(part.findPath("balanceCHARGE").doubleValue()));

                partnerResponse.setPartner(partnerUser);

                session(Const.SESSION_PARTNER_NAME, partnerResponse.getPartner().getName());
                session(Const.SESSION_PARTNER_TYPE, partnerResponse.getPartner().getType());
                session(Const.SESSION_PARTNER_ADRESS, partnerResponse.getPartner().getAdress());
                session(Const.SESSION_PARTNER_PHONE, partnerResponse.getPartner().getTelephone());
                session(Const.SESSION_PARTNER_EMAIL, partnerResponse.getPartner().getEmail());
                session(Const.SESSION_PARTNER_COUNTRY, partnerResponse.getPartner().getCountry());
                session(Const.SESSION_PARTNER_ACCOUNT_PRINC, partnerResponse.getPartner().getAccountPRINC());
                session(Const.SESSION_PARTNER_ACCOUNT_COMM, partnerResponse.getPartner().getAccountCOMM());
                session(Const.SESSION_PARTNER_ACCOUNT_CHARGE, partnerResponse.getPartner().getAccountCHARGE());
                session(Const.SESSION_PARTNER_BALANCE_PRINC, partnerResponse.getPartner().getBalancePRINC());
                session(Const.SESSION_PARTNER_BALANCE_COMM, partnerResponse.getPartner().getBalanceCOMM());
                session(Const.SESSION_PARTNER_BALANCE_CHARGE, partnerResponse.getPartner().getBalanceCHARGE());

            }

            Log.logActionOutput(partnerResponse.toString());
            return ok(partnerResponse.toObjectNode());
        });

    }

    public  F.Promise<Result> getInfosPayTransfer() {
        return promise(() -> {

            Log.logActionHeader("getInfosPayTransfer");
            Logger.info("JSON REQ: " + request().body());
            JsonNode jsonInput = request().body().asJson();

            String codePayment = jsonInput.findPath("codePayment").textValue();

            ObjectNode json = Json.newObject();

            json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
            json.put("code", codePayment);
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("infos.transfer.url");

            //Logger.info("Token: " + session().get(Const.SESSION_TOKEN_APP));
            Logger.info("Token: " + Const.SESSION_TOKEN_APP);
            Logger.info("consumerId: " + session(Const.SESSION_CONSUMER_ID));
            if (Const.SESSION_TOKEN_APP == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            servResponse = requestingGetInfosPayTransfer(json, url).thenApply(WSResponse::asJson);
            if (servResponse == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            JsonNode jsonData = servResponse.toCompletableFuture().join();

            Logger.info("jsonData: " + jsonData);

            SuccessResponse successResponse = new SuccessResponse();

            successResponse.setCode(jsonData.findPath("code").intValue());
            successResponse.setMessage(jsonData.findPath("message").textValue());

            if(successResponse.getCode() == Const.HTTP_SUCCESS){
                Iterator<JsonNode> recap = jsonData.findPath("recap").elements();
                successResponse.setRecap((ArrayList<PairValue>) SuccessResponse.checkPairValues(recap));
            }

            Log.logActionOutput(successResponse.toString());
            return ok(successResponse.toObjectNode());
        });

    }

    public CompletionStage<WSResponse> requestingGetPartnerData(String url){

        Logger.info("requestinggetPartnerData >>> " + url);
        CompletionStage<WSResponse> eventualResponse = null;

        try {
            Logger.info("requestinggetPartnerData URL: " + url);

            eventualResponse = ws.url(url)
                    //.setHeader("Authorization", session(Const.SESSION_TOKEN_APP))
                    .setHeader("Authorization", Const.SESSION_TOKEN_APP)
                    .setAuth("user",
                            "password",
                            WSAuthScheme.BASIC)
                    .get();

            eventualResponse.thenApplyAsync(response -> ok(response.asJson()),
                    exec);

        } catch (Exception e){
            Logger.error(e.getMessage());
        }

        try {

            if (eventualResponse == null){
                Logger.info("waiting 30s");
                Thread.sleep(30000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Logger.info("val de rep json before to exit " + eventualResponse.toString());

        return eventualResponse;
    }

    public CompletionStage<WSResponse> requestingGetInfosPayTransfer(ObjectNode json, String url){

        Logger.info("requestingGetInfosPayTransfer >>> " + json.toString());
        CompletionStage<WSResponse> eventualResponse = null;

        try {
            Logger.info("requestingGetInfosPayTransfer URL: " + url);

            eventualResponse = ws.url(url)
                    //.setHeader("Authorization", session().get(Const.SESSION_TOKEN_APP))
                    .setHeader("Authorization", Const.SESSION_TOKEN_APP)
                    .setAuth("user",
                            "password",
                            WSAuthScheme.BASIC)
                    .post(json);

            eventualResponse.thenApplyAsync(response -> ok(response.asJson()),
                    exec);

        } catch (Exception e){
            Logger.error(e.getMessage());
        }

        try {

            if (eventualResponse == null){
                Logger.info("waiting 30s");
                Thread.sleep(30000);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //Logger.info("val de rep json before to exit " + eventualResponse.toString());

        return eventualResponse;
    }




}
