package client;

import java.lang.reflect.Proxy;

public class RpcProxy {

    public <T> T create(Class<T> interfaceCLass){
        return (T) Proxy.newProxyInstance(interfaceCLass.getClassLoader(),
                new Class<?>[]{interfaceCLass},
                new RpcHandler<T>(interfaceCLass));
    }

    public static <T> IRpcProxy createAsync(Class<T> interfaceClass){
        return new RpcHandler<T>(interfaceClass);
    }
}
