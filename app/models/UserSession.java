package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by mac on 21/02/2021.
 */
public class UserSession {

    private int idPartner;

    private String name;

    private String profil;

    private String partner;

    private String consumerId;

    public UserSession() {
    }

    public UserSession(String name, String profil, String partner) {
        this.name = name;
        this.profil = profil;
        this.partner = partner;
    }

    public UserSession(int idPartner, String name, String profil, String partner) {
        this.idPartner = idPartner;
        this.name = name;
        this.profil = profil;
        this.partner = partner;
    }

    public UserSession(int idPartner, String name, String profil, String partner, String consumerId) {
        this.idPartner = idPartner;
        this.name = name;
        this.profil = profil;
        this.partner = partner;
        this.consumerId = consumerId;
    }

    public int getIdPartner() {
        return idPartner;
    }

    public void setIdPartner(int idPartner) {
        this.idPartner = idPartner;
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

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public void setPartner(String partner) {
        this.partner = partner;
    }

    public ObjectNode toObjectNode(){
        ObjectNode objectNode = Json.newObject();
        objectNode.put("idPartner", idPartner);
        objectNode.put("name", name);
        objectNode.put("profil", profil);
        objectNode.put("partner", partner);
        objectNode.put("consumerId", consumerId);
        return objectNode;
    }

    @Override
    public String toString() {
        return toObjectNode().toString();
    }
}
