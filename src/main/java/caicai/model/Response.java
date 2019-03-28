package caicai.model;

public class Response {
    private String requesId;
    private String erro;
    private Object result;
    boolean iserro(){
       return erro!=null;
    }

    public String getRequesId() {
        return requesId;
    }

    public void setRequesId(String requesId) {
        this.requesId = requesId;
    }

    public String getErro() {
        return erro;
    }

    public void setErro(String erro) {
        this.erro = erro;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
