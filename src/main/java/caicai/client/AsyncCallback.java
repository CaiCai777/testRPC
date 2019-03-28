package caicai.client;

import caicai.model.RpcResponse;

public interface AsyncCallback {
    public void success(Object result);
    public void fail(Exception e);
}
