package caicai.client;

import caicai.model.Request;
import caicai.model.Response;
import caicai.model.RpcRequest;
import caicai.model.RpcResponse;
import caicai.protocol.*;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class SendChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    public void initChannel(SocketChannel ch) throws Exception {

        ch.pipeline().addLast(new RpcEncoder(RpcRequest.class))
                     .addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 0))
                     .addLast(new RpcDecoder(RpcResponse.class))
                     .addLast(new ChannelHandler());
    }
}
