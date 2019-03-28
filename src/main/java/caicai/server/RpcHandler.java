package caicai.server;

import caicai.model.Request;
import caicai.model.Response;
import caicai.model.RpcRequest;
import caicai.model.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import net.sf.cglib.reflect.FastClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

public class RpcHandler extends SimpleChannelInboundHandler<RpcRequest> {
    private final Logger logger= LoggerFactory.getLogger(RpcHandler.class);
    private final Map<String,Object> handlerMap;
    public RpcHandler(Map<String,Object> handlerMap){
        this.handlerMap=handlerMap;
    }



    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        logger.info("服务器已经接收到了消息");
        RpcServer.submit(new Runnable() {
            @Override
            public void run() {
                RpcResponse response=new RpcResponse();
               response.setRequestId(request.getRequestId());
                try {
                    Object o=handleRequst(request);
                    response.setResult(o);

                }catch (Throwable e){
                    e.printStackTrace();
                }
                ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        System.out.println("已经发送成功"+response.getRequestId());

                    }
                });
            }
        });

    }
    private Object handleRequst(RpcRequest request) throws Throwable{
        //利用反射获得对象的实例并执行对象方法

        String className=request.getClassName();
        System.out.println(className);

        Object object=handlerMap.get(className);

        System.out.println(object.toString());
        Class<?> serviceClass=object.getClass();
        System.out.println(serviceClass.getName());
        String methodName=request.getMethodName();
        Object[] parameters=request.getParameters();
        Class<?>[] parameterTypes=request.getParameterTypes();

        FastClass fastClass=FastClass.create(serviceClass);
        int methodIndex=fastClass.getIndex(methodName,parameterTypes);
        System.out.println(methodIndex);
         return fastClass.invoke(methodIndex,object,parameters);





    }
}
