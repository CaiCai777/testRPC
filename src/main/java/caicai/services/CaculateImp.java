package caicai.services;

import caicai.serviceInterfice.Caculate;
import caicai.spring.RpcService;

@RpcService(Caculate.class)
public class CaculateImp implements Caculate {
    @Override
    public int add(int a, int b) {
        return a+b;
    }
}
