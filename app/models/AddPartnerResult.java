package models;

/**
 * Created by mac on 28/02/2021.
 */
public class AddPartnerResult extends ProcedureResult {

    private String consumerId, compteCharge;

    public AddPartnerResult(int result, String message, String consumerId, String compteCharge) {
        super(result, message);
        this.consumerId = consumerId;
        this.compteCharge = compteCharge;
    }

    public String getConsumerId() {
        return consumerId;
    }

    public void setConsumerId(String consumerId) {
        this.consumerId = consumerId;
    }

    public String getCompteCharge() {
        return compteCharge;
    }

    public void setCompteCharge(String compteCharge) {
        this.compteCharge = compteCharge;
    }
}
