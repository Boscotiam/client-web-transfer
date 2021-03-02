package models;

import com.fasterxml.jackson.databind.node.ObjectNode;
import play.libs.Json;

/**
 * Created by mac on 14/10/2020.
 */
public class PairValue {

    private String label;

    private String value;

    public PairValue() {
    }

    public PairValue(String label, String value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public ObjectNode toObjectNode(){
        ObjectNode node = Json.newObject();
        node.put("label",label);
        node.put("value",value);
        return node;
    }

}
