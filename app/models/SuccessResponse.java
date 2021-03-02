package models;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by mac on 14/10/2020.
 */
public class SuccessResponse {

    private int code;

    private String message;

    ArrayList<PairValue> recap;

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

    public ArrayList<PairValue> getRecap() {
        return recap;
    }

    public void setRecap(ArrayList<PairValue> recap) {
        this.recap = recap;
    }

    public ObjectNode toObjectNode() {
        ObjectNode objectNode = Json.newObject();
        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        objectNode.put("code", code);
        objectNode.put("message", message);
        for(PairValue e:recap){
            arrayNode.add(e.toObjectNode());
        }
        objectNode.set("recap", arrayNode);
        return objectNode;

    }

    public String toString(){
        return toObjectNode().toString();
    }

    public static List<PairValue> checkPairValues(Iterator<JsonNode> values) throws NumberFormatException {
        List<PairValue> recap = new ArrayList<>();

        while (values.hasNext()) {
            JsonNode next = values.next();
            PairValue pairValue = new PairValue();
            pairValue.setLabel(next.findPath("label").textValue());
            pairValue.setValue(next.findPath("value").textValue());
            recap.add(pairValue);
        }

        return recap;
    }


}
