package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by mac on 31/01/2021.
 */
public class PartnerResponse {

    private int code;

    private String message;

    private Partner partner;

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

    public Partner getPartner() {
        return partner;
    }

    public void setPartner(Partner partner) {
        this.partner = partner;
    }

    public ObjectNode toObjectNode() {
        ObjectNode objectNode = Json.newObject();
        objectNode.put("code", code);
        objectNode.put("message", message);
        if(partner != null) objectNode.put("partner", partner.toObjectNode());
        return objectNode;

    }

    public String toString(){
        return toObjectNode().toString();
    }

}
