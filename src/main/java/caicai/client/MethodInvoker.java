package caicai.client;

import caicai.model.RpcRequest;
import com.google.common.reflect.AbstractInvocationHandler;

import java.lang.reflect.Method;
import java.util.UUID;

public class MethodInvoker extends AbstractInvocationHandler {
    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request=new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameters(args);
        request.setParameterTypes(method.getParameterTypes());
        return ClientConnector.getInstance().getChannelHandler().sendReques(request).get();


    }
}
