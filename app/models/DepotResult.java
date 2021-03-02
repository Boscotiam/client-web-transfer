package models;

/**
 * Created by mac on 01/03/2021.
 */
public class DepotResult extends ProcedureResult {

    private String transaction;

    private double balanceCHARGE;

    public DepotResult(int result, String message, String transaction, double balanceCHARGE) {
        super(result, message);
        this.transaction = transaction;
        this.balanceCHARGE = balanceCHARGE;
    }

    public String getTransaction() {
        return transaction;
    }

    public void setTransaction(String transaction) {
        this.transaction = transaction;
    }

    public double getBalanceCHARGE() {
        return balanceCHARGE;
    }

    public void setBalanceCHARGE(double balanceCHARGE) {
        this.balanceCHARGE = balanceCHARGE;
    }
}
