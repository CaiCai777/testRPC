/**
 * Copyright (C) 2016 Newland Group Holding Limited
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package caicai.spring;

import caicai.client.ClientConnector;
import caicai.client.RpcClient;

import caicai.zookeeper.curator.CuratorRegistry;
import com.google.common.eventbus.EventBus;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

/**原作者
 * @author tangjie<https://github.com/tang-jie>
 *
 */
//工厂bean，通过配置它的属性，实例化的是它产生的对象，而不是它的对象
public class TestRpcReference implements FactoryBean, InitializingBean, DisposableBean {
    private String interfaceName;
    private String ipAddr;
    private String protocol;
    private EventBus eventBus = new EventBus();

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddr) {
        this.ipAddr = ipAddr;
    }

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }


    @Override
    public void destroy() throws Exception {
       // eventBus.post(new ClientStopEvent(0));
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //在这里连接服务器
        CuratorRegistry registry=new CuratorRegistry("127.0.0.1:2181");
        registry.subcribe();
       // ClientStopEventListener listener = new ClientStopEventListener();
       // eventBus.register(listener);
    }

    @Override
    public Object getObject() throws Exception {
        //连接服务器以后获取对象实例

        return RpcClient.createProxy(getObjectType());

    }

    /***
     * 这里interface只是一个名字，并不能之间用名字来获取它的class文件
     * 例如 Class<?> class=interfaceName.class(错误)
     * 因此需要用到以下的方法
     * getObjectType()
     *
     */

    @Override
    public Class<?> getObjectType() {

        try {
            return this.getClass().getClassLoader().loadClass(interfaceName);
        } catch (ClassNotFoundException e) {
            System.err.println("spring analyze fail!");
        }
        return null;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}
