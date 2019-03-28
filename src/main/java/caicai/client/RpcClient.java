package caicai.client;

import com.google.common.reflect.Reflection;

public class RpcClient {
    public static  <T> T createProxy(Class<T> interficeType){
        return (T)Reflection.newProxy(interficeType,new MethodInvoker());

    }

}
