package client;

import core.RpcFuture;

public interface IRpcProxy {
    public RpcFuture call(String funcName, Object... args);
}
