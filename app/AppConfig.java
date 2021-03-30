import play.Configuration;
import tools.Const;

import javax.inject.Inject;

/**
 * Created by mac on 07/05/2019.
 */
public class AppConfig {

    @Inject
    public AppConfig(Configuration conf) {

        Const.DEFAULT_LANG = conf.getString("default.lang");

        Const.OS = conf.getString("os.app");
        Const.VERSION = conf.getString("version.app");

        Const.SMTP_NO_REPLY = conf.getString("mail.default.sender");
        Const.SMTP_USERNAME = conf.getString("mail.default.username");
        Const.SMTP_PASSWORD = conf.getString("mail.default.password");
        Const.SMTP_HOST = conf.getString("mail.default.host");
        Const.SMTP_PORT = conf.getString("mail.smtp.port");
        Const.SMTP_AUTH = conf.getString("mail.smtp.auth");
        Const.SMTP_STARTTLS_ENABLE = conf.getString("mail.smtp.starttls.enable");

        Const.HTTP_SUCCESS = conf.getInt("http.success");

        Const.PROFIL_ADMIN_ID = conf.getInt("profil.id.admin");
        Const.PROFIL_SENDER_ID = conf.getInt("profil.id.sender");
        Const.PROFIL_PAYER_ID = conf.getInt("profil.id.payer");
        Const.PROFIL_MIXTE_ID = conf.getInt("profil.id.mixte");
        Const.PROFIL_AGENT_SENDER_ID = conf.getInt("profil.id.agent.sender");
        Const.PROFIL_AGENT_PAYER_ID = conf.getInt("profil.id.agent.payer");
        Const.PROFIL_AGENT_MIXTE_ID = conf.getInt("profil.id.agent.mixte");

        Const.DEFAULT_GUICHET = conf.getString("default.guichet");

        Const.PROFIL_ADMIN = conf.getString("profil.admin");
        Const.PROFIL_SENDER = conf.getString("profil.sender");
        Const.PROFIL_PAYER = conf.getString("profil.payer");
        Const.PROFIL_MIXTE = conf.getString("profil.mixte");
        Const.PROFIl_AGENT_SENDER = conf.getString("profil.agent.sender");
        Const.PROFIl_AGENT_PAYER = conf.getString("profil.agent.payer");
        Const.PROFIl_AGENT_MIXTE = conf.getString("profil.agent.mixte");

        Const.SERVICE_TRANSFER = conf.getString("service.transfer");
        Const.CPT_SERVICE_PRINC = conf.getString("cpt.service.princ");
        Const.CPT_SERVICE_COMM = conf.getString("cpt.service.comm");


    }

}
