package caicai.test;

import caicai.client.ClientConnector;
import caicai.client.RpcClient;
import caicai.serviceInterfice.Caculate;
import caicai.services.CaculateImp;

public class SecondTest {
    public static void main(String[] args){
        ClientConnector.getInstance().connectServer();
        Caculate aa= RpcClient.createProxy(Caculate.class);
        //同步获得结果
        int b=aa.add(2,3);
        System.out.println(b);
    }
}
