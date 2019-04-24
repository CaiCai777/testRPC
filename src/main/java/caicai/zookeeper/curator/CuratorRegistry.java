package caicai.zookeeper.curator;

import caicai.zookeeper.ZK_Constant;
import com.google.common.collect.ImmutableClassToInstanceMap;
import io.netty.util.internal.ConcurrentSet;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.CuratorWatcher;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.framework.state.ConnectionStateListener;
import org.apache.curator.retry.RetryNTimes;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.WatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CuratorRegistry {
    Logger logger=LoggerFactory.getLogger(CuratorRegistry.class);

    private final CuratorFramework zkClient;
    private String zkAddress;
    private final Set<String>registerd=new ConcurrentSet<>();
    private final Set<String>subcribed=new ConcurrentSet<>();
    private List<String> pathlist,contentlist;
    private List<CuratorListener> listeners=new ArrayList<>();

    public List<String> getContentlist() {
        return contentlist;
    }

    public  CuratorRegistry(String zkAddress){
        this.zkAddress=zkAddress;
        //这种类的设计有什么好处？？
        CuratorFrameworkFactory.Builder builder=CuratorFrameworkFactory.builder()//初始化一些参数
                .connectString(zkAddress)//这后面需要自己设置的
                .retryPolicy(new RetryNTimes(1,1000))
                .connectionTimeoutMs(ZK_Constant.SESSION_TIMEOUT);
        zkClient=builder.build();
        //当重连时，重新注册。
        zkClient.getConnectionStateListenable().addListener(new ConnectionStateListener() {
            @Override
            public void stateChanged(CuratorFramework curatorFramework, ConnectionState connectionState) {
                if (connectionState==ConnectionState.RECONNECTED){
                    recover();
                }


            }
        });
        zkClient.start();
        addListener();
        logger.info("成功连接zookeeper");

    }/*
      *当服务客户端重连时，重新将自己的地址注册到zookeeper
      */
    private void recover(){
        for (String data:registerd){
            try {
                register(data);
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
    public void register(String data)throws Exception{
        registerd.add(data);
        byte[] bytes=data.getBytes();
        zkClient.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).forPath(ZK_Constant.DATA_PATH,bytes);
        logger.info("注册服务器地址到zookeeper:{}",data);

    }
    //testCode
    public void registerTest(String data)throws Exception{
        registerd.add(data);
        byte[] bytes=data.getBytes();
        zkClient.create().creatingParentsIfNeeded().forPath(ZK_Constant.REGISTRY_PATH+"/test6",bytes);
        logger.info("注册服务器地址到zookeeper");

    }
    public void subcribe()throws  Exception{

        pathlist=zkClient.getChildren().usingWatcher(new CuratorWatcher() {
            @Override
            public void process(WatchedEvent watchedEvent) throws Exception {
              String path=watchedEvent.getPath()==null?"":watchedEvent.getPath();
             if (!"".equals(path)){
                 subcribe();
             }

            }
        }).forPath(ZK_Constant.REGISTRY_PATH);
        for (String a:pathlist){
            logger.info("获得了子节点地址:{}",a);
        }
         List<String> addresses=getContent();
        notifyClient(addresses);





    }
    private void notifyClient(List<String> addresses){
        for (CuratorListener listener:listeners){
            listener.notify(addresses);
        }

    }
    public void addListener(){
        listeners.add(new ListenerImp());
    }


    private List<String> getContent()throws Exception{
         byte[] content;
         List list=new ArrayList();
        for (String aa:pathlist){
            content=zkClient.getData().forPath(ZK_Constant.REGISTRY_PATH+"/"+aa);
            String data=new String(content);
            logger.info("获得服务地址：{}",data);
            list.add(data);

        }
          contentlist=list;
        return contentlist;
    }


}
