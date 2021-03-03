package controllers;

import models.UserSession;
import play.Logger;
import play.Routes;
import play.mvc.Controller;
import play.mvc.Result;
import tools.Const;
import tools.Log;
import tools.Utils;
import views.html.*;

import java.util.UUID;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result changeLangue(String lang) {
        Log.logActionHeader("changeLangue");
        Logger.info("New lang :" + lang);
        Const.DEFAULT_LANG = lang;
        Controller.changeLang(Const.DEFAULT_LANG);
        return redirect(controllers.routes.HomeController.home());
    }

    public Result index() {
        Controller.changeLang(Const.DEFAULT_LANG);
        session().clear();
        UUID uuid = UUID.randomUUID();
        session(Const.SESSION_DEVICE_ID, uuid.toString());
        Logger.info("SESSION DEVICE ID: " + session(Const.SESSION_DEVICE_ID));
        return ok(index.render());
    }

    public Result activation() {
        //Http.Context.current().changeLang(Const.DEFAULT_LANG);
        if (session(Const.SESSION_CONNECTED).equals("activation")) {
            return ok(activation.render());
        }else{
            return ok(index.render());
        }
    }

    public Result changepass() {
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) session = "false";
        if (!session.equals("true")) {return ok(index.render());}
        else{
            return ok(changepass.render());
        }
    }

    public Result home() {
        //Logger.info("SESSION TOKEN: " + session(Const.SESSION_TOKEN_APP));
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) session = "false";
        if (!session.equals("true")) {return ok(index.render());}
        UserSession userSession = new UserSession(session(Const.SESSION_USER_NAME),
                session(Const.SESSION_PROFIL), session(Const.SESSION_PARTNER_NAME));
        return ok(home.render(userSession));
    }

    public Result transactions() {
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) session = "false";
        if (!session.equals("true")) {return ok(index.render());}
        UserSession userSession = new UserSession(session(Const.SESSION_USER_NAME),
                session(Const.SESSION_PROFIL), session(Const.SESSION_PARTNER_NAME));
        return ok(transactions.render(userSession));
    }

    public Result compenses() {
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) session = "false";
        if (!session.equals("true")) {return ok(index.render());}
        UserSession userSession = new UserSession(session(Const.SESSION_USER_NAME),
                session(Const.SESSION_PROFIL), session(Const.SESSION_PARTNER_NAME));
        return ok(compenses.render(userSession));
    }

    public Result depots() {
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) session = "false";
        if (!session.equals("true")) {return ok(index.render());}
        UserSession userSession = new UserSession(session(Const.SESSION_USER_NAME),
                session(Const.SESSION_PROFIL), session(Const.SESSION_PARTNER_NAME));
        return ok(depots.render(userSession));
    }

    public Result partners() {
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) session = "false";
        if (!session.equals("true")) {return ok(index.render());}
        UserSession userSession = new UserSession(session(Const.SESSION_USER_NAME),
                session(Const.SESSION_PROFIL), session(Const.SESSION_PARTNER_NAME));
        return ok(partners.render(userSession));
    }

    public Result users() {
        String session = session(Const.SESSION_CONNECTED);
        if (session == null) session = "false";
        if (!session.equals("true")) {return ok(index.render());}
        UserSession userSession = new UserSession(session(Const.SESSION_USER_NAME),
                session(Const.SESSION_PROFIL), session(Const.SESSION_PARTNER_NAME));
        return ok(users.render(userSession));
    }

    // Methode contenant les URL Ajax pour ce controller
    public Result jsRoutes() {
        response().setContentType("text/javascript");
        return ok(Routes.javascriptRouter("appRoutes",
                routes.javascript.LoginTransfer.connectInTransfer(),
                routes.javascript.LoginTransfer.activationConnection(),
                routes.javascript.Enquiries.getPartnerData(),
                routes.javascript.Enquiries.getInfosPayTransfer(),
                routes.javascript.Enquiries.getJournal(),
                routes.javascript.Enquiries.getTransactions(),
                routes.javascript.Enquiries.getCompenses(),
                routes.javascript.Enquiries.getDepots(),
                routes.javascript.Operations.checkFees(),
                routes.javascript.Operations.sendTransfer(),
                routes.javascript.Operations.payTransfer(),
                routes.javascript.Operations.initCompense(),
                routes.javascript.PartnerManager.getPartners(),
                routes.javascript.PartnerManager.addPartner(),
                routes.javascript.PartnerManager.depotSender(),
                routes.javascript.UserController.getUsers(),
                routes.javascript.UserController.addUser(),
                routes.javascript.UserController.updateUser(),
                routes.javascript.UserController.changePassword(),
                routes.javascript.UserController.reinitPassword(),
                routes.javascript.UserController.lockOrUnlockUser()
        ));
    }


}
