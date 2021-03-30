package models;

/**
 * Created by mac on 28/02/2021.
 */
public class AddGuichetResult extends ProcedureResult {

    private String account, code;

    public AddGuichetResult(int result, String message, String account, String code) {
        super(result, message);
        this.account = account;
        this.code = code;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
