package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by mac on 18/02/2021.
 */
public class Connection {

    private int code;

    private String message;

    private int userId;

    private String name;

    private String surname;

    private String consumerId;

    private String consumerSecret;

    private String profil;

    public Connection() {
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getConsumerSecret() {
        return consumerSecret;
    }

    public void setConsumerSecret(String consumerSecret) {
        this.consumerSecret = consumerSecret;
    }

    public String getProfil() {
        return profil;
    }

    public void setProfil(String profil) {
        this.profil = profil;
    }

    public ObjectNode toObjectNode() {
        ObjectNode objectNode = Json.newObject();
        objectNode.put("code", code);
        objectNode.put("message", message);
        objectNode.put("userId", userId);
        objectNode.put("name", name);
        objectNode.put("surname", surname);
        objectNode.put("consumerId", consumerId);
        objectNode.put("consumerSecret", consumerSecret);
        objectNode.put("profil", profil);

        return objectNode;

    }

    public String toString(){
        return toObjectNode().toString();
    }

}
