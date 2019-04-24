package caicai.client;


import caicai.zookeeper.loadBalance.LoadBalanceStrategy;
import caicai.zookeeper.loadBalance.RandomSelectStrategy;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;

import io.netty.channel.socket.nio.NioSocketChannel;
import jdk.internal.dynalink.beans.StaticClass;
import org.apache.commons.collections4.bag.SynchronizedBag;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ClientConnector {
   // private ServerBootstrap serverBootstrap;
    private LoadBalanceStrategy loadBalanceStrategy;
    private Bootstrap bootstrap;
    private String serverAddress;
    private EventLoopGroup worker;
    private ReentrantLock lock=new ReentrantLock();
    private Condition condition=lock.newCondition();
    private ChannelHandler channelHandler=null;

    public ChannelHandler getChannelHandler()throws InterruptedException {
        try {
            lock.lock();//获得锁
            if (channelHandler == null) {
                //等待连接线程连接成功将此线程唤醒。
                condition.await();//在condition这个队列排队等着
            }
            return channelHandler;
        } finally {
            lock.unlock();
            System.out.println("get完成");
        }
    }

    public void setChannelHandler(ChannelHandler channelHandler) {

        try {
            lock.lock();
            this.channelHandler=channelHandler;
            condition.signal();
        }finally {
            lock.unlock();
            System.out.println("设置完毕");
        }
    }

    //private  ChannelHandler channelHandler;
   // public static volatile Map<String,Object> handlerMap=new ConcurrentHashMap<~>();
    private  static ClientConnector instance;
    private ClientConnector(){
        loadBalanceStrategy=new RandomSelectStrategy();

    }
    public static  ClientConnector getInstance(){
        if (instance==null){
            synchronized (ClientConnector.class){
                if (instance==null){
                    instance= new ClientConnector();
                }
            }
        }
        return instance;
    }
    public void connect(List<String> addresses){
        String address=loadBalanceStrategy.selectAddress(addresses);
        connectServer(address);
    }

    public void connectServer(String serverAddress) {
        bootstrap=new Bootstrap();
        worker=new NioEventLoopGroup();
        bootstrap.group(worker)
                .channel(NioSocketChannel.class).handler(new SendChannelInitializer());
        String data[]=serverAddress.split(":");
        String ip=data[0];
        int port=Integer.parseInt(data[1]);
        ChannelFuture future = bootstrap.connect(ip,port);
        //future.channel().closeFuture().sync();
        future.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
             ChannelHandler ch= future.channel().pipeline().get(ChannelHandler.class);
              setChannelHandler(ch);
               System.out.println("连接服务器成功");
            }
        });

    }
    public void closeClient(){
        if(worker!=null){
            worker.shutdownGracefully();
        }
    }
}
