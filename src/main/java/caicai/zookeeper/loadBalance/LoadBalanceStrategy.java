package caicai.zookeeper.loadBalance;

import java.util.List;

public interface LoadBalanceStrategy {
    public String selectAddress(List<String > addresses);
}
