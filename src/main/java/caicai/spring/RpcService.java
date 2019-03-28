package caicai.spring;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义一个annotation RpCService
 */

@Target({ElementType.TYPE})//注解作用的类型，这里是接口或者Class

@Retention(RetentionPolicy.RUNTIME)//
@Component
public @interface RpcService {
    Class<?> value();//注解的值
}
