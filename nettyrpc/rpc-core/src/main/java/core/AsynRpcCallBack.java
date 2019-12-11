package core;

public interface AsynRpcCallBack {

    void success(Object result);

    void fail(Exception e);
}
