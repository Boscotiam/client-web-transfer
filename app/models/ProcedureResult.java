package models;

/**
 * Created by bosco on 07/05/2019.
 */

public class ProcedureResult {

    private int result;

    private String message;

    public ProcedureResult(int result, String message) {
        this.result = result;
        this.message = message;
    }

    public int getResult() {
        return result;
    }

    public void setResult(int result) {
        this.result = result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "ProcedureResult{" +
                "result=" + result +
                ", message='" + message + '\'' +
                '}';
    }
}
