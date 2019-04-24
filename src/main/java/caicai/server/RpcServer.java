package caicai.server;



import caicai.model.RpcRequest;
import caicai.model.RpcResponse;

import caicai.protocol.RpcDecoder;
import caicai.protocol.RpcEncoder;
import caicai.spring.RpcService;
import caicai.zookeeper.curator.CuratorRegistry;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcServer implements InitializingBean, ApplicationContextAware {
    private static final Logger logger= LoggerFactory.getLogger(RpcServer.class);
    private  String serverAddress;
    public static ThreadPoolExecutor threadPoolExecutor=null;
    private Map<String,Object> serviceMap=new HashMap<>();//存放Service
    private EventLoopGroup boss=null;
    private EventLoopGroup worker=null;

    public  RpcServer(String serverAddress){
        this.serverAddress=serverAddress;
    }

    public static void submit(Runnable task) {
        if(threadPoolExecutor==null){
            synchronized (RpcServer.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(16, 16, 600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));
                }
            }
        }
        threadPoolExecutor.submit(task);
    }

    //
    @Override
   public void afterPropertiesSet() throws Exception{
        start();


   }
   //获得service对象，放入serviceMap中

   @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
         Map<String,Object> beanMap=ctx.getBeansWithAnnotation(RpcService.class);
          if (MapUtils.isNotEmpty(beanMap)){
              for (Object object:beanMap.values()){
                  //通过对象获得类，并获得类上的注解中的值
                  //getAnnotation(RpcService.class)获得的是一个RpcService的对象
                  //可以写成 RpcService annotation=getAnnotation(RpcService.class)
                  String interfaceName=object.getClass().getAnnotation(RpcService.class).value().getName();
                  logger.info("loading Service:{}",interfaceName);
                  System.out.println("loading service:"+interfaceName);
                  System.out.println("ObjectName"+object.getClass());

                  this.serviceMap.put(interfaceName,object);
                  if(serviceMap.containsKey("Caculate")){
                      System.out.println("yes");
                  }
                  for (String a:serviceMap.keySet()){
                      System.out.println(a);
                  }
              }

          }
    }
    public void start()throws Exception {
        CuratorRegistry curatorRegistry1=new CuratorRegistry("127.0.0.1:2181");
        curatorRegistry1.register(serverAddress);

        if (boss == null && worker == null) {


            boss = new NioEventLoopGroup();
            worker = new NioEventLoopGroup();
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        public void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(65526,0,4,0,0))
                                    .addLast(new RpcDecoder(RpcRequest.class))
                                    .addLast(new RpcEncoder(RpcResponse.class))
                                    .addLast(new RpcHandler(serviceMap));

                        }
                    })
                    .option(ChannelOption.SO_BACKLOG,128)
                    .childOption(ChannelOption.SO_KEEPALIVE,true);
            String[] array=serverAddress.split(":");
            int port=Integer.parseInt(array[1]);
            String host=array[0];
            ChannelFuture future=serverBootstrap.bind(host,port).sync();
            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {

                   logger.info("服务器开启成功");
                }
            });
            future.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    System.out.println("已经关闭");
                }
            })
;

        }

    }

}
