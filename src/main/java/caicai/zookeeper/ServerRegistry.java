package caicai.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ServerRegistry {
    //服务器注册节点的时候是不用监听的，客户端发现服务的时候才要监听
    private Logger logger= LoggerFactory.getLogger(ServerRegistry.class);
    public ZooKeeper zooKeeper;
    private String registryAddress;
    private final CountDownLatch countDownLatch=new CountDownLatch(1);
    public ServerRegistry(String registryAddress){
        this.registryAddress=registryAddress;
    }
    public void registry(String data){
        if(data!=null){
            zooKeeper=ConnectServer();
            if (zooKeeper!=null) {
                addRootNode(zooKeeper);
                addDataNode(zooKeeper, data);
            }
        }

    }
    private ZooKeeper ConnectServer() {
        ZooKeeper zk=null;
        try {


            zk = new ZooKeeper(registryAddress, ZK_Constant.SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        countDownLatch.countDown();
                    }
                }
            });
            countDownLatch.await();
        }catch (InterruptedException e){
            logger.error("",e);

        }catch (IOException e){
            logger.error("",e);
        }
            return zk;


    }
    private void addRootNode(ZooKeeper zooKeeper){
        try {


            Stat stat = zooKeeper.exists(ZK_Constant.REGISTRY_PATH, false);
            if (stat==null){

                zooKeeper.create(ZK_Constant.REGISTRY_PATH,new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.PERSISTENT);
            }
        }catch (InterruptedException e){
            logger.error(e.toString());

        }catch (KeeperException e){
            logger.error(e.toString());

        }

    }
    private void addDataNode(ZooKeeper zooKeeper,String data){
        byte[] bytes=data.getBytes();
        try {
           String path= zooKeeper.create(ZK_Constant.DATA_PATH,bytes,ZooDefs.Ids.OPEN_ACL_UNSAFE,CreateMode.EPHEMERAL_SEQUENTIAL);
           logger.debug("creat zookeeper node ({}=>{})",path,data);
        }catch (KeeperException e){
            logger.error(e.toString());
        }catch (InterruptedException e){
            logger.error(e.toString());
        }


    }
}
