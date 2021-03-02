package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.inject.Inject;
import models.PairValue;
import models.SuccessResponse;
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.CompletionStage;

import static play.libs.F.Promise.promise;

/**
 * Created by mac on 22/02/2021.
 */
public class Operations extends Controller {

    private final WSClient ws;
    private final ExecutionContextExecutor exec;

    @Inject
    public Operations(WSClient ws, ExecutionContextExecutor exec) {
        this.ws = ws;
        this.exec = exec;
    }

    public  F.Promise<Result> checkFees() {
        return promise(() -> {

            Log.logActionHeader("checkFees");
            Logger.info("JSON REQ: " + request().body());
            JsonNode jsonInput = request().body().asJson();

            String montant = jsonInput.findPath("montant").textValue();
            String destination = jsonInput.findPath("destination").textValue();

            ObjectNode json = Json.newObject();

            json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
            json.put("service", Const.SERVICE_TRANSFER);
            json.put("amount", Double.parseDouble(montant));
            json.put("country", destination);
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("fees.transfer.url");

            //Logger.info("Token: " + session().get(Const.SESSION_TOKEN_APP));
            Logger.info("Token: " + Const.SESSION_TOKEN_APP);
            Logger.info("consumerId: " + session(Const.SESSION_CONSUMER_ID));
            if (Const.SESSION_TOKEN_APP == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            servResponse = requestingService(json, url).thenApply(WSResponse::asJson);
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
                ArrayList<PairValue> recap = new ArrayList<PairValue>();
                PairValue message = new PairValue("Result", jsonData.findPath("message").textValue());
                PairValue service = new PairValue("Service", jsonData.findPath("service").textValue());
                PairValue amount = new PairValue("Amount", Utils.formatAmount(jsonData.findPath("amount").doubleValue()));
                PairValue fees = new PairValue("Fees", Utils.formatAmount(jsonData.findPath("fees").doubleValue()));
                PairValue country = new PairValue("Country", jsonData.findPath("country").textValue());
                recap.add(message);
                recap.add(service);
                recap.add(amount);
                recap.add(fees);
                recap.add(country);
                successResponse.setRecap(recap);
            }

            Log.logActionOutput(successResponse.toString());
            return ok(successResponse.toObjectNode());
        });

    }

    public  F.Promise<Result> sendTransfer() {
        return promise(() -> {

            Log.logActionHeader("sendTransfer");
            Logger.info("JSON REQ: " + request().body());
            JsonNode jsonInput = request().body().asJson();

            String transmetter = jsonInput.findPath("transmetter").textValue();
            String transmetterIdentify = jsonInput.findPath("transmetterIdentify").textValue();
            String beneficiary = jsonInput.findPath("beneficiary").textValue();
            String mobile = jsonInput.findPath("mobile").textValue();
            String montant = jsonInput.findPath("montant").textValue();
            String destination = jsonInput.findPath("destination").textValue();

            ObjectNode json = Json.newObject();

            json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
            json.put("montant", Double.parseDouble(montant));
            json.put("transmetter", transmetter);
            json.put("transmetterIdentify", transmetterIdentify);
            json.put("beneficiary", beneficiary);
            json.put("mobile", mobile);
            json.put("user", Integer.parseInt(session(Const.SESSION_USER_ID)));
            json.put("destination", destination);
            json.put("deviceId", session(Const.SESSION_DEVICE_ID));
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("send.transfer.url");

            //Logger.info("Token: " + session().get(Const.SESSION_TOKEN_APP));
            Logger.info("Token: " + Const.SESSION_TOKEN_APP);
            Logger.info("consumerId: " + session(Const.SESSION_CONSUMER_ID));
            Logger.info("deviceId: " + session(Const.SESSION_DEVICE_ID));
            if (Const.SESSION_TOKEN_APP == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            servResponse = requestingService(json, url).thenApply(WSResponse::asJson);
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

    public  F.Promise<Result> payTransfer() {
        return promise(() -> {

            Log.logActionHeader("getInfosPayTransfer");
            Logger.info("JSON REQ: " + request().body());
            JsonNode jsonInput = request().body().asJson();

            String codePayment = jsonInput.findPath("codePayment").textValue();
            String beneficiaryIdentify = jsonInput.findPath("beneficiaryIdentify").textValue();

            ObjectNode json = Json.newObject();

            json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
            json.put("codePayment", codePayment);
            json.put("beneficiaryIdentify", beneficiaryIdentify);
            json.put("user", Integer.parseInt(session(Const.SESSION_USER_ID)));
            json.put("deviceId", session(Const.SESSION_DEVICE_ID));
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("pay.transfer.url");

            //Logger.info("Token: " + session().get(Const.SESSION_TOKEN_APP));
            Logger.info("Token: " + Const.SESSION_TOKEN_APP);
            Logger.info("consumerId: " + session(Const.SESSION_CONSUMER_ID));
            if (Const.SESSION_TOKEN_APP == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            servResponse = requestingService(json, url).thenApply(WSResponse::asJson);
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

    public  F.Promise<Result> initCompense() {
        return promise(() -> {

            Log.logActionHeader("initCompense");
            Logger.info("JSON REQ: " + request().body());
            JsonNode jsonInput = request().body().asJson();

            String montant = jsonInput.findPath("montant").textValue();

            ObjectNode json = Json.newObject();

            json.put("consumerId", session(Const.SESSION_CONSUMER_ID));
            json.put("montant", Double.parseDouble(montant));
            json.put("deviceId", session(Const.SESSION_DEVICE_ID));
            CompletionStage<JsonNode> servResponse = null;

            String url = Play.application().configuration().getString("init.transfer.url");

            //Logger.info("Token: " + session().get(Const.SESSION_TOKEN_APP));
            Logger.info("Token: " + Const.SESSION_TOKEN_APP);
            Logger.info("consumerId: " + session(Const.SESSION_CONSUMER_ID));
            Logger.info("deviceId: " + session(Const.SESSION_DEVICE_ID));
            if (Const.SESSION_TOKEN_APP == null) {
                ObjectNode node = Utils.getObjectNode(301, "WS Client Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            servResponse = requestingService(json, url).thenApply(WSResponse::asJson);
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

    public CompletionStage<WSResponse> requestingService(ObjectNode json, String url){

        Logger.info("requestingService >>> " + json.toString());
        CompletionStage<WSResponse> eventualResponse = null;

        try {
            Logger.info("requestingService URL: " + url);

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
