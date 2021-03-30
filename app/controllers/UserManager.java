package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import models.*;
import play.Logger;
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
public class UserManager extends Controller {

    public F.Promise<Result> getUsers(int partner) {
        return promise(() -> {
            String session = session(SESSION_CONNECTED);
            ObjectNode node = Json.newObject();
            if (session == null) {
                node = Utils.getObjectNode(301, "Session Error");
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            String user = session(Const.SESSION_USER_NAME);
            Log.logActionHeader(user, "getUsers");
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

            ArrayList<User> list = null;

            list = User.getUsers(partner);
            if (list.size() != 0) {
                node.put("code", 200);
                node.put("name", DBUtils.getPartnerName(partner));
                node.put("users", Json.toJson(list));
                //node.put("profils", Json.toJson(Profil.getProfils()));
            } else {
                node.put("code", 201);
                node.put("name", DBUtils.getPartnerName(partner));
                node.put("message", Messages.get("label.message.empty.user"));
                //node.put("profils", Json.toJson(Profil.getProfils()));
            }

            Log.logActionOutput(node.toString());
            return ok(node);
        });

    }

    public F.Promise<Result> addUser() {
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
            Log.logActionHeader(user, "addUser");
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
            String prenom = json.findPath("prenom").textValue();
            String telephone = json.findPath("telephone").textValue();
            String email = json.findPath("email").textValue();
            String login = json.findPath("login").textValue();
            String pass = Utils.generateUserPassword();
            //String profil = json.findPath("profil").textValue();
            String partner = json.findPath("partner").textValue();

            if (!Utils.checkData(nom, prenom, telephone, email, login, pass, partner)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                node.put("countries", Json.toJson(Country.getCountries()));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
            ProcedureResult result = User.addUser(nom.toUpperCase(), prenom.toUpperCase(), telephone, email, login, pass,
                                Utils.getProfilAgent(session(Const.SESSION_PARTNER_TYPE)), Integer.parseInt(partner));

            if (result.getResult() == 0) {
                node.put("code", 201);
                node.put("message", result.getMessage());
                return ok(node);
            } else {
                node.put("code", 200);
                node.put("message", result.getMessage());
                String message = Messages.get("message.hi") + " " + prenom .toUpperCase() + " " + nom.toUpperCase() + ", " +
                        Messages.get("message.create.user") + " ; " +
                        Messages.get("message.create.user.login")  + " : " + login + ", " +
                        Messages.get("message.create.user.pass")  + " : " + pass + " . " ;
                DBUtils.saveNotification(telephone, "SMS", Messages.get("message.header"), message);
                DBUtils.saveNotification(email, "MAIL", Messages.get("message.header"), message);
            }
            Log.logActionOutput(node.toString());
            return ok(node);

        });
    }

    public F.Promise<Result> updateUser() {
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
            Log.logActionHeader(user, "updateUser");
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
            String prenom = json.findPath("prenom").textValue();
            String telephone = json.findPath("telephone").textValue();
            String email = json.findPath("email").textValue();
            //String profil = json.findPath("profil").textValue();
            String idUser = json.findPath("user").textValue();

            if (!Utils.checkData(nom, prenom, telephone, email, idUser)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                Log.logActionOutput(node.toString());
                return ok(node);
            }

            ProcedureResult result = User.updateUser(nom, prenom, telephone, email, Integer.parseInt(idUser));

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

    public F.Promise<Result> changePassword() {
        return F.Promise.promise(() -> {
            ObjectNode node = Json.newObject();
            String session = session(Const.SESSION_CONNECTED);
            if (session == null) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.session"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
            String user = session(Const.SESSION_USER_NAME);
            Log.logActionHeader(user, "changePassword");
            Log.logActionInputJson(request().body().asJson().toString());
            JsonNode json = request().body().asJson();
            try {
                String idUser = session(Const.SESSION_USER_ID);
                String mdp = json.findPath("mdp").textValue();
                String oldMdp = json.findPath("oldmdp").textValue();

                if (mdp.equals(oldMdp)) {
                    node.put("result", "nok");
                    node.put("message", Messages.get("error.bad.profil"));
                    Log.logActionOutput(node.toString());
                    return unauthorized(node);
                }

                boolean userUpdated = User.updatePwdUser(idUser, mdp, oldMdp);

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
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
        });
    }

    public F.Promise<Result> reinitPassword() {
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
            Log.logActionHeader(user, "reinitPassword");
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
                String idUser = json.findPath("user").textValue();
                String nom = json.findPath("nom").textValue();
                String prenom = json.findPath("prenom").textValue();
                String email = json.findPath("email").textValue();
                String telephone = json.findPath("telephone").textValue();
                String pass = Utils.generateUserPassword();

                boolean userUpdated = User.reinitPassUser(idUser, pass);

                if (!userUpdated) {
                    node.put("code", 201);
                    node.put("message", Messages.get("error.parameter"));
                    Log.logActionOutput(node.toString());
                    return ok(node);
                }

                node.put("code", 200);
                node.put("message", Messages.get("success.show"));
                Log.logActionOutput(node.toString());
                String message = Messages.get("message.hi") + " " + prenom + " " + nom + " " +
                        Messages.get("message.update.user.pass") + ": " + pass + " .";

                DBUtils.saveNotification(telephone, "SMS", Messages.get("message.header"), message);
                DBUtils.saveNotification(email, "MAIL", Messages.get("message.header"), message);
                return ok(node);

            } catch (NullPointerException e) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }
        });
    }

    public F.Promise<Result> lockOrUnlockUser() {
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
            Log.logActionHeader(user, "lockOrUnlockUser");
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

                String idUser = json.findPath("idUser").textValue();
                String action = json.findPath("valueAction").textValue();
                boolean userUpdated = User.verrouUser(idUser, action);

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

    public F.Promise<Result> openGuichet() {
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
            Log.logActionHeader(user, "openGuichet");
            Log.logActionInputJson(request().body().asJson().toString());

            //Logger.info("SESSION_GUICHET BEGIN OPEN: " + session(Const.SESSION_GUICHET));

            if (!Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_MIXTE.equals(session(SESSION_PROFIL))) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            JsonNode json = request().body().asJson();

            String device = session(Const.SESSION_DEVICE_ID);
            String guichet = json.findPath("guichet").textValue();
            String idUser = session(Const.SESSION_USER_ID);

            if (!Utils.checkData(device, guichet, idUser)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
            ProcedureResult result = Guichet.openGuichet(device, guichet, Integer.parseInt(idUser));

            if (result.getResult() == 0) {
                node.put("code", 201);
                node.put("message", result.getMessage());
                return ok(node);
            } else {
                node.put("code", 200);
                node.put("message", result.getMessage());
                session(Const.SESSION_GUICHET, guichet);
                Logger.info("SESSION_GUICHET OPEN: " + session(Const.SESSION_GUICHET));
            }
            Log.logActionOutput(node.toString());
            return ok(node);

        });
    }

    public F.Promise<Result> updateTransaction() {
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
            Log.logActionHeader(user, "updateTransaction");
            Log.logActionInputJson(request().body().asJson().toString());

            if (!Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_SENDER.equals(session(SESSION_PROFIL)) &&
                    !Const.PROFIL_MIXTE.equals(session(SESSION_PROFIL))) {
                node.put("result", "nok");
                node.put("message", Messages.get("error.bad.profil"));
                Log.logActionOutput(node.toString());
                return unauthorized(node);
            }

            JsonNode json = request().body().asJson();

            String transaction = json.findPath("transaction").textValue();
            String transmetter = json.findPath("transmetter").textValue();
            String transmetterTel = json.findPath("transmetterTel").textValue();
            String beneficiaire = json.findPath("beneficiaire").textValue();
            String beneficiaireTel = json.findPath("beneficiaireTel").textValue();

            if (!Utils.checkData(transaction, transmetter, transmetterTel, beneficiaire, beneficiaireTel)) {
                node.put("code", 201);
                node.put("message", Messages.get("error.parameter"));
                Log.logActionOutput(node.toString());
                return ok(node);
            }
            ProcedureResult result = Transaction.updateTransaction(transaction, transmetter, transmetterTel, beneficiaire, beneficiaireTel);

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
