package caicai.client;

import caicai.model.Request;
import caicai.model.Response;
import caicai.model.RpcRequest;
import caicai.model.RpcResponse;
import io.netty.channel.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ChannelHandler extends ChannelInboundHandlerAdapter {
    private volatile Channel channel;
    private static Map<String,TestFuture> futureMap=new ConcurrentHashMap<String,TestFuture>();
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel=ctx.channel();
    }
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse=(RpcResponse)msg;
        String requestId=((RpcResponse) msg).getRequestId();
        if(futureMap.containsKey(requestId))
            futureMap.get(requestId).done(rpcResponse);


    }




    public TestFuture sendReques(RpcRequest request){
        //创建一个TestFuture来监听客户端sendRequest后服务器返回的结果
        TestFuture tfuture=new TestFuture(request);
        //将这个TestFuture放入map中，待客户端收到消息时寻找对应的TestFuture
        futureMap.put(request.getRequestId(),tfuture);

        final CountDownLatch countDownLatch=new CountDownLatch(1);
        ChannelFuture channelFuture= channel.writeAndFlush(request);
        channelFuture.addListener(new ChannelFutureListener() {
           @Override
           public void operationComplete(ChannelFuture future) throws Exception {
               countDownLatch.countDown();
               System.out.println("发送成功");
           }


       });
       try {
           countDownLatch.await();
       }catch (InterruptedException e){
           e.printStackTrace();
       }

       return tfuture;


    }
}
