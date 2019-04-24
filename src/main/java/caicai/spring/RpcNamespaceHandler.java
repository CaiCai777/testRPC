package caicai.spring;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class RpcNamespaceHandler extends NamespaceHandlerSupport {
    @Override
    public void init() {
         registerBeanDefinitionParser("reference",new TestRpcReferenceParser());
    }
}
