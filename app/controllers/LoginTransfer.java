package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import models.*;
import play.Logger;
import play.Play;
import play.libs.F;
import play.libs.Json;
import play.libs.ws.WSAuthScheme;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import play.mvc.Controller;
import play.mvc.Result;
import scala.concurrent.ExecutionContextExecutor;
import tools.Const;
import tools.Log;
import tools.Utils;

import java.util.concurrent.CompletionStage;

import static play.libs.F.Promise.promise;

/**
 * Created by mac on 18/02/2021.
 */
public class LoginTransfer extends Controller {

    private final WSClient ws;
    private final ExecutionContextExecutor exec;

    @Inject
    public LoginTransfer(WSClient ws, ExecutionContextExecutor exec) {
        this.ws = ws;
        this.exec = exec;
    }

    public  F.Promise<Result> connectInTransfer() {
        return promise(() -> {

            Log.logActionHeader("connectInTransfer");
            Logger.info("JSON REQ: " + request().body());
            JsonNode jsonInput = request().body().asJson();

            String login = jsonInput.findPath("login").textValue();
            String password = jsonInput.findPath("password").textValue();

            ObjectNode json = Json.newObject();

            json.put("login", login);
            json.put("password", password);
            json.put("os", Const.OS);
            json.put("version", Const.VERSION);
            json.put("deviceId", session(Const.SESSION_DEVICE_ID));
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("login.url");

            servResponse = requestingService(json, url).thenApply(WSResponse::asJson);
            if (servResponse == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            JsonNode jsonData = servResponse.toCompletableFuture().join();

            Logger.info("jsonData: " + jsonData);

            Connection connection = new Connection();

            connection.setCode(jsonData.findPath("code").intValue());
            connection.setMessage(jsonData.findPath("message").textValue());
            if(connection.getCode() == Const.HTTP_SUCCESS){

                connection.setUserId(jsonData.findPath("userId").intValue());
                connection.setName(jsonData.findPath("name").textValue());
                connection.setSurname(jsonData.findPath("surname").textValue());
                connection.setConsumerId(jsonData.findPath("consumerId").textValue());
                connection.setConsumerSecret(jsonData.findPath("consumerSecret").textValue());
                connection.setProfil(jsonData.findPath("profil").textValue());

                Logger.info("Connection: " + connection.toString());

                session(Const.SESSION_CONNECTED, "activation");
                session(Const.SESSION_USER_ID, String.valueOf(connection.getUserId()));
                session(Const.SESSION_USER_NAME, connection.getName().concat(" ").concat(connection.getSurname()));
                session(Const.SESSION_PROFIL, connection.getProfil());
                session(Const.SESSION_CONSUMER_ID, connection.getConsumerId());
                session(Const.SESSION_CONSUMER_SECRET, connection.getConsumerSecret());

                //authentication();
            }

            Log.logActionOutput(connection.toString());
            return ok(connection.toObjectNode());
        });

    }

    public  F.Promise<Result> authentication() {
        return promise(() -> {

            ObjectNode json = Json.newObject();

            json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
            json.put("consumerSecret", session(Const.SESSION_CONSUMER_SECRET));
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("auth.url");

            servResponse = requestingService(json, url).thenApply(WSResponse::asJson);
            if (servResponse == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            JsonNode jsonData = servResponse.toCompletableFuture().join();

            Authentication authentication = new Authentication();

            authentication.setCode(jsonData.findPath("code").intValue());
            authentication.setMessage(jsonData.findPath("message").textValue());
            if(authentication.getCode() == Const.HTTP_SUCCESS){
                authentication.setToken(jsonData.findPath("token").textValue());
                Logger.info("Token: " + authentication.getToken());
                session(Const.SESSION_TOKEN_APP, authentication.getToken());
                //session().put(Const.SESSION_TOKEN_APP, authentication.getToken());
                //Const.SESSION_TOKEN_APP = authentication.getToken();
            }

            Log.logActionOutput(authentication.toString());
            return ok(authentication.toObjectNode());
        });

    }

    public  F.Promise<Result> activationConnection() {
        return promise(() -> {

            Log.logActionHeader("activationConnection");
            Logger.info("JSON REQ: " + request().body());
            JsonNode jsonInput = request().body().asJson();

            String activation = jsonInput.findPath("activation").textValue();

            ObjectNode json = Json.newObject();

            json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
            json.put("code", activation);
            json.put("deviceId", session(Const.SESSION_DEVICE_ID));
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("activation.url");

            Logger.info("Token: " + Const.SESSION_TOKEN_APP);
            Logger.info("consumerId: " + session(Const.SESSION_CONSUMER_ID));
            Logger.info("deviceId: " + session(Const.SESSION_DEVICE_ID));
            if (Const.SESSION_TOKEN_APP == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            servResponse = requestingServiceWithToken(json, url).thenApply(WSResponse::asJson);
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

                session(Const.SESSION_CONNECTED, "true");

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

    public Result disConnection() {
        Log.logActionHeader("disConnection");
        ObjectNode json = Json.newObject();

        json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
        json.put("deviceId", session(Const.SESSION_DEVICE_ID));
        CompletionStage<JsonNode> servResponse = null;

        String url = Play.application().configuration().getString("disconnect.url");

        Logger.info("Token: " + Const.SESSION_TOKEN_APP);
        Logger.info("consumerId: " + session(Const.SESSION_CONSUMER_ID));
        Logger.info("deviceId: " + session(Const.SESSION_DEVICE_ID));
        if (Const.SESSION_TOKEN_APP == null) {
            ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }
        servResponse = requestingServiceWithToken(json, url).thenApply(WSResponse::asJson);
        if (servResponse == null) {
            ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
            Log.logActionOutput(node.toString());
            return unauthorized(node);
        }
        JsonNode jsonData = servResponse.toCompletableFuture().join();

        SimpleResponse simpleResponse = new SimpleResponse();
        simpleResponse.setCode(jsonData.findPath("code").intValue());
        simpleResponse.setMessage(jsonData.findPath("message").textValue());

        Log.logActionOutput(simpleResponse.toString());
        session().clear();
        return redirect(controllers.routes.HomeController.index());
        //return ok(simpleResponse.toObjectNode());
    }

    public CompletionStage<WSResponse> requestingService(ObjectNode json, String url){

        Logger.info("requestingService >>> " + json.toString());
        CompletionStage<WSResponse> eventualResponse = null;

        try {
            Logger.info("requestingService URL: " + url);

            eventualResponse = ws.url(url)
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

    public CompletionStage<WSResponse> requestingServiceWithToken(ObjectNode json, String url){

        Logger.info("requestingServiceWithToken >>> " + json.toString());
        CompletionStage<WSResponse> eventualResponse = null;

        try {
            Logger.info("requestingServiceWithToken URL: " + url);

            eventualResponse = ws.url(url)
                    .setHeader("Authorization", session().get(Const.SESSION_TOKEN_APP))
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
