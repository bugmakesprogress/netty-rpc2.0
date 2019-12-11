package client;

import core.RpcFuture;
import core.RpcRequest;
import core.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;

public class RpcHandler<T> implements IRpcProxy, InvocationHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(RpcHandler.class);
    private Class<T> clz;

    @Override
    public RpcFuture call(String funcName, Object... args) {
        RpcRequest request = createRequest(this.clz.getName(), funcName, args);
        RpcClient client = new RpcClient("127.0.0.1", 4437); // 初始化 RPC 客户端
        RpcFuture future = client.sendRequest(request);
        return future;
    }

    public RpcHandler(Class<T> clz){
        this.clz = clz;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcRequest request = new RpcRequest(); // 创建并初始化 RPC 请求
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParameters(args);

        RpcClient client = new RpcClient("127.0.0.1", 4437); // 初始化 RPC 客户端
        RpcFuture future = client.sendRequest(request); // 通过 RPC 客户端发送 RPC 请求并获取 RPC 响应
        RpcResponse response = future.get();
        if (response.getError() != null) {
            throw response.getError();
        } else {
            return response.getResult();
        }
    }

    private RpcRequest createRequest(String className, String methodName, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);

        Class[] parameterTypes = new Class[args.length];
        // Get the right class type
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);

        logger.debug(className);
        logger.debug(methodName);
        for (int i = 0; i < parameterTypes.length; ++i) {
            logger.debug(parameterTypes[i].getName());
        }
        for (int i = 0; i < args.length; ++i) {
            logger.debug(args[i].toString());
        }

        return request;
    }

    private Class<?> getClassType(Object obj) {
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName) {
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }

        return classType;
    }
}
