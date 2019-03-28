package caicai.model;

public class Request {
    private String requestId;
    private String className;
    private  String methedName;
    private  Object[] parameters;
    private  Class<?>[] parametersTypes;

    public String getClassName() {
        return className;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethedName() {
        return methedName;
    }

    public void setMethedName(String methedName) {
        this.methedName = methedName;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public void setParameters(Object[] parameters) {
        this.parameters = parameters;
    }

    public Class<?>[] getParametersTypes() {
        return parametersTypes;
    }

    public void setParametersTypes(Class<?> parametersTypes[]) {
        this.parametersTypes = parametersTypes;
    }
}
