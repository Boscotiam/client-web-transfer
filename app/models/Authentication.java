package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by mac on 18/02/2021.
 */
public class Authentication {

    private int code;

    private String message;

    private String token;

    public Authentication() {
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public ObjectNode toObjectNode() {
        ObjectNode objectNode = Json.newObject();
        objectNode.put("code", code);
        objectNode.put("message", message);
        objectNode.put("token", token);
        return objectNode;

    }

    public String toString(){
        return toObjectNode().toString();
    }

}
