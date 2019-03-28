package caicai.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BootServer {
    public static void main(String[] args){
        new ClassPathXmlApplicationContext("server_config_context.xml");
    }
}
