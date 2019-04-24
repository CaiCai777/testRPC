package caicai.zookeeper.loadBalance;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSelectStrategy implements LoadBalanceStrategy{
    Logger logger= LoggerFactory.getLogger(RandomSelectStrategy.class);
    @Override
    public String selectAddress(List<String > dataList) {
        String data = null;
        int size = dataList.size();
        if (size > 0) {
            if (size == 1) {
                data = dataList.get(0);
                logger.debug("using only data: {}", data);
            } else {
                data = dataList.get(ThreadLocalRandom.current().nextInt(size));
                logger.debug("using random data: {}", data);
            }
        }
        return data;


    }
}
