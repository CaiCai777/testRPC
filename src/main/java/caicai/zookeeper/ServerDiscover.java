package caicai.zookeeper;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;


public class ServerDiscover {
    private static final Logger logger= LoggerFactory.getLogger(ServerDiscover.class);
    private String registryAddress;
    public List<String> dataList=new ArrayList<String>();
    private ZooKeeper zooKeeper;
    private final CountDownLatch countDownLatch=new CountDownLatch(1);
    public ServerDiscover(String registryAddress){
        this.registryAddress=registryAddress;

    }
    //随机获取一个服务器地址
    public String dicover(){
        String data=null;
        if (dataList.size()>0){
            if (dataList.size()==1){
                data=dataList.get(0);
                logger.info("get the only data:{}",data);
            }
            if (dataList.size()>1){
                data=dataList.get(ThreadLocalRandom.current().nextInt(dataList.size()));
                logger.info("get random data:{}",data);
            }
        }
        return  data;


    }
    private ZooKeeper connectZookeeper(){
        ZooKeeper zk=null;
        try {
            zk=new ZooKeeper(registryAddress, ZK_Constant.SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {

                    if(event.getState()==Event.KeeperState.SyncConnected)
                    countDownLatch.countDown();
                }
            });
            countDownLatch.await();
        }catch (InterruptedException e){
            logger.debug("",e);

        }catch (IOException e){
            logger.debug("",e);
        }
        return zk;
    }
    private  void getChildData(){
        try {
//获得children 路径
            /*
            * 大概过程：
            * getChildr这个过程是zookeeper向客户端传送子节点地址的过程
            * 这个过程有注册的EventWach来watch root中子节点的变化
            * 一旦发生变化则向客户端传送变化结果
            * 此时客户端注册的watcher callBack就会被执行*/
            List<String> dList=new ArrayList<>();
            if (zooKeeper != null) {
                List<String> pathList=zooKeeper.getChildren(ZK_Constant.REGISTRY_PATH, new Watcher() {
                    @Override
                    public void process(WatchedEvent event) {
                        if (event.getType()==Event.EventType.NodeChildrenChanged){
                            getChildData();
                        }

                    }
                });
                String childPath;
                for (String nodePath:pathList){
                    childPath=ZK_Constant.REGISTRY_PATH+"/"+nodePath;
                   String childData =new String(zooKeeper.getData(childPath,false,null));
                   dList.add(childData);
                }

            }
            this.dataList=dList;
        }catch (InterruptedException e){
             logger.error("",e);

        }catch (KeeperException e){
             logger.error("",e);
        }
    }

}
