package tools;

import play.Logger;

import java.util.Map;

/**
 * Created by bosco on 07/05/2019
 */
public class Log {

    public static void logActionHeader(String company, String actionName) {
        Logger.info("####################################################################################################################################");
        Logger.info("####################################################################################################################################");
        Logger.info("############################## USER: " + company.toUpperCase());
        Logger.info("############################## ACTION: " + actionName.toUpperCase());
    }

    public static void logActionHeader(String actionName) {
        Logger.info("####################################################################################################################################");
        Logger.info("####################################################################################################################################");
        Logger.info("############################## ACTION: " + actionName.toUpperCase());
    }

    public static void logActionOutput(String result) {
        Logger.info("####################################################################################################################################");
        Logger.info("############################## OUTPUT: " + result);
        Logger.info("####################################################################################################################################");

    }

    public static void logActionInput(Map<String, String[]> stringMap) {
        StringBuilder log = new StringBuilder();
        for (String s : stringMap.keySet()) {
            log.append(s).append("=").append(stringMap.get(s)[0] + " ");
        }
        Logger.info("############################## INPUT: " + log.toString());
    }

    public static void logActionInputJson(String json) {
        Logger.info("############################## INPUT: " + json);
    }
}
