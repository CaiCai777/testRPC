package caicai.test;

import caicai.client.ClientConnector;
import caicai.client.RpcClient;
import caicai.serviceInterfice.Caculate;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ThirdTest {
    public static void main(String[] args){
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("classpath:rpc-client.xml");

        Caculate aa= (Caculate) context.getBean("Caculate");
        //同步获得结果
        int b=aa.add(2,3);
        System.out.println(b);
    }
}
