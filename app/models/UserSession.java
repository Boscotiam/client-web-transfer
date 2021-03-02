package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by mac on 21/02/2021.
 */
public class UserSession {

    private String name;

    private String profil;

    private String partner;

    public UserSession() {
    }

    public UserSession(String name, String profil, String partner) {
        this.name = name;
        this.profil = profil;
        this.partner = partner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfil() {
        return profil;
    }

    public void setProfil(String profil) {
        this.profil = profil;
    }

    public String getPartner() {
        return partner;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public ObjectNode toObjectNode(){
        ObjectNode objectNode = Json.newObject();
        objectNode.put("name", name);
        objectNode.put("profil", profil);
        objectNode.put("partner", partner);
        return objectNode;
    }

    @Override
    public String toString() {
        return toObjectNode().toString();
    }
}
