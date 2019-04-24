package caicai.client;

import caicai.model.RpcRequest;
import caicai.model.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import org.apache.log4j.xml.Log4jEntityResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
//test Handler

public class ClientChannelHandler extends SimpleChannelInboundHandler<RpcResponse> {
    private volatile Channel channel;
    private static final Logger logger= LoggerFactory.getLogger(ClientChannelHandler.class);
    @Override
    public void channelRead0(ChannelHandlerContext ctx, RpcResponse msg) throws Exception {
     System.out.println(msg.getRequestId());
    }
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }
    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("client caught exception", cause);
        ctx.close();
    }
    public void sendReques(RpcRequest request) {

        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ChannelFuture channelFuture = channel.writeAndFlush(request);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                countDownLatch.countDown();
                System.out.println("发送成功");
            }


        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
