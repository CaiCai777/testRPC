package caicai.zookeeper.curator;

import caicai.client.ClientConnector;

import java.util.List;

public interface CuratorListener {
    public void notify(List<String> serverAddresses);
}
