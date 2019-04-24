package caicai.zookeeper.curator;

import caicai.client.ClientConnector;

import java.util.List;


public class ListenerImp implements CuratorListener {
    @Override
    public void notify(List<String> serverAddresses) {
        ClientConnector connector=ClientConnector.getInstance();
        connector.connect(serverAddresses);

    }
}
