package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by mac on 31/01/2021.
 */
public class SimpleResponse {

    private int code;

    private String message;

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

    public ObjectNode toObjectNode() {
        ObjectNode objectNode = Json.newObject();
        objectNode.put("code", code);
        objectNode.put("message", message);
        return objectNode;

    }

    public String toString(){
        return toObjectNode().toString();
    }

}
