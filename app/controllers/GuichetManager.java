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
 * Created by bosco on 09/08/2019.
 */
public class GuichetManager extends Controller {

    public F.Promise<Result> getGuichets(String partner) {
        return promise(() -> {
            String session = session(SESSION_CONNECTED);
            ObjectNode node = Json.newObject();
            if (session == null) {
                node = Utils.getObjectNode(301, "Session Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            String user = session(Const.SESSION_USER_NAME);
            Log.logActionHeader(user, "getGuichets");
            Log.logActionInput(request().queryString());

            if (!Const.PROFIL_ADMIN.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_PAYER.equals(session(SESSION_PROFIL))  &&
                    !Const.PROFIL_MIXTE.equals(session(SESSION_PROFIL)) ) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            ArrayList<Guichet> list = null;

            list = Guichet.getGuichets(partner);
            if (list.size() != 0) {
                node.put("code", 200);
                node.put("name", DBUtils.getPartnerNameByConsumerId(partner));
                node.put("guichets", Json.toJson(list));
            } else {
                node.put("code", 201);
                node.put("name", DBUtils.getPartnerNameByConsumerId(partner));
                node.put("message", Messages.get("label.message.empty.guichet"));
            }

            Log.logActionOutput(node.toString());
            return ok(node);
        });

    }

    public F.Promise<Result> addGuichet() {
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
            Log.logActionHeader(user, "addGuichet");
            Log.logActionInputJson(request().body().asJson().toString());

            if (!Const.PROFIL_ADMIN.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_PAYER.equals(session(SESSION_PROFIL))  &&
                    !Const.PROFIL_MIXTE.equals(session(SESSION_PROFIL)) ) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            JsonNode json = request().body().asJson();

            String nom = json.findPath("nom").textValue();
            String adresse = json.findPath("adresse").textValue();
            String partner = session(Const.SESSION_CONSUMER_ID);
            String device = session(Const.SESSION_DEVICE_ID);

            if (!Utils.checkData(nom, partner, device)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                node.put("countries", Json.toJson(Country.getCountries()));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
            ProcedureResult result = Guichet.addGuichet(nom.toUpperCase(), adresse, partner, device);

            if (result.getResult() == 0) {
                node.put("code", 201);
                node.put("message", result.getMessage());
                return ok(node);
            } else {
                node.put("code", 200);
                node.put("message", result.getMessage());
            }
            Log.logActionOutput(node.toString());
            return ok(node);

        });
    }

    public F.Promise<Result> verrouGuichet() {
        return F.Promise.promise(() -> {
            ObjectNode node = Json.newObject();
            String session = session(SESSION_CONNECTED);
            if (session == null) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.session"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            String user = session(Const.SESSION_USER_NAME);
            Log.logActionHeader(user, "verrouGuichet");
            Log.logActionInputJson(request().body().asJson().toString());

            if (!Const.PROFIL_ADMIN.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_PAYER.equals(session(SESSION_PROFIL))  &&
                    !Const.PROFIL_MIXTE.equals(session(SESSION_PROFIL)) ) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            JsonNode json = request().body().asJson();
            try {

                String code = json.findPath("code").textValue();
                String action = json.findPath("valueAction").textValue();
                boolean userUpdated = Guichet.verrou(code, action);

                if (!userUpdated) {
                    node.put("code", 201);
                    node.put("message", Messages.get("error.parameter"));
                    Log.logActionOutput(node.toString());
                    return ok(node);
                }

                node.put("code", 200);
                node.put("message", Messages.get("success.show"));
                Log.logActionOutput(node.toString());
                return ok(node);

            } catch (NullPointerException e) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
        });
    }

    public F.Promise<Result> depotGuichet() {
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

            if (!Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) && !Const.PROFIL_MIXTE.equals(session(SESSION_PROFIL))) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            JsonNode json = request().body().asJson();

            String partner = session(Const.SESSION_CONSUMER_ID);
            String guichet = json.findPath("guichet").textValue();
            String montant = json.findPath("montant").textValue();
            String idUser = session(Const.SESSION_USER_ID);

            if (!Utils.checkData(partner, guichet, montant, idUser)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
            DepotResult result = Guichet.depotExecute(partner, guichet, Double.parseDouble(montant), Integer.parseInt(idUser));

            if (result.getResult() == 0) {
                node.put("code", 201);
                node.put("message", result.getMessage());
                return ok(node);
            } else {
                node.put("code", 200);
                node.put("message", result.getMessage());
            }
            Log.logActionOutput(node.toString());
            return ok(node);

        });
    }

}
