package caicai.test;

import caicai.client.*;
import caicai.client.ChannelHandler;
import caicai.model.Request;
import caicai.model.RpcRequest;
import caicai.server.RpcServer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class FirstTest {
    public static void main(String args[]){
        RpcRequest request=new RpcRequest();
        //new ClassPathXmlApplicationContext("server_config_context.xml")
        request.setClassName("caicai.serviceInterfice.Caculate");
        Class[] classes={int.class,int.class};
        request.setParameterTypes(classes);
        Object[] objects={2,3};
        request.setParameters(objects);
        request.setMethodName("add");
        request.setRequestId("1111");

        ClientConnector.getInstance().connectServer();
      try {
          ChannelHandler channelHandler=ClientConnector.getInstance().getChannelHandler();
         TestFuture future= channelHandler.sendReques(request);
         future.addAsyncCallback(new AsyncCallback() {
             @Override
             public void success(Object result) {
                 System.out.println("啦啦啦啦啦啦成功获得了结果:"+result);
             }

             @Override
             public void fail(Exception e) {
                 System.out.println("....对方拒绝了你的请求还扔了你一脸粑粑");
                 e.printStackTrace();

             }
         });
      }catch (InterruptedException e){
          e.printStackTrace();
      }


    }
}
