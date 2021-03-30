package tools;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.Logger;
import play.libs.Json;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Random;
import java.util.regex.Pattern;


public class Utils
{
    public Utils() {}

    public static String formatNumber(int number) {
        DecimalFormat df = new DecimalFormat("#,###,###,##0");

        DecimalFormatSymbols s = df.getDecimalFormatSymbols();
        s.setGroupingSeparator(' ');
        df.setDecimalFormatSymbols(s);
        Logger.info(df.format(number));

        return df.format(number);
    }

    public static String formatAmount(Double number) {
        DecimalFormat df = new DecimalFormat("#,###,###,##0");

        DecimalFormatSymbols s = df.getDecimalFormatSymbols();
        s.setGroupingSeparator(' ');
        df.setDecimalFormatSymbols(s);
        Logger.info(df.format(number));
        return df.format(number);
    }

    public static ObjectNode getObjectNode(int  code, String message) {
        ObjectNode result = Json.newObject();
        result.put("code", code);
        result.put("message", message);

        return result;
    }

    public static String getSHA1(String chaine) {
        MessageDigest mDigest;
        try {
            mDigest = MessageDigest.getInstance("SHA1");
            byte[] result = mDigest.digest(chaine.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aResult : result) {
                sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String getMD5(String chaine) {
        MessageDigest mDigest;
        try {
            mDigest = MessageDigest.getInstance("mD5");
            byte[] result = mDigest.digest(chaine.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte aResult : result) {
                sb.append(Integer.toString((aResult & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static String generateUserPassword() {
        return generatePass(8);
    }

    public static String generatePass(int numchars) {
        Random r = new Random();

        StringBuilder builder = new StringBuilder();
        String alphabet = "1234567890qwertyuioplkjhgfdsazxcvbnm";
        for (int i = 0; i < numchars; i++) {
            builder.append(alphabet.charAt(r.nextInt(alphabet.length())));
        }
        return builder.toString();
    }

    public static boolean checkData(String... data) {
        Pattern regex = Pattern.compile("[$&=\\\\?|/<>^()%!]");
        for (String s : data) {
            if (s == null) {
                return false;
            } else if (s.isEmpty()) {
                return false;
            }

            if (regex.matcher(s).find()) {
                Logger.info(s + "Contains special caracter !!!");
                return false;
            }
        }
        return true;

    }

    public static int getProfilID(String partnerType){
        if (partnerType.equals("SENDER")) return Const.PROFIL_SENDER_ID;
        if (partnerType.equals("PAYER")) return Const.PROFIL_PAYER_ID;
        if (partnerType.equals("MIXTE")) return Const.PROFIL_MIXTE_ID;
        return 0;
    }
    public static int getProfilAgent(String partnerType){
        if (partnerType.equals("SENDER")) return Const.PROFIL_AGENT_SENDER_ID;
        if (partnerType.equals("PAYER")) return Const.PROFIL_AGENT_PAYER_ID;
        if (partnerType.equals("MIXTE")) return Const.PROFIL_AGENT_MIXTE_ID;
        return 0;
    }




}
