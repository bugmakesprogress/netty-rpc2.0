package hkr.com;

import client.IRpcProxy;
import client.RpcProxy;
import core.AsynRpcCallBack;
import core.RpcFuture;
import core.RpcResponse;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import user.User;
import user.UserAction;

import java.util.concurrent.TimeUnit;

@RestController
public class TestController {

    @RequestMapping(value = "/test/1", method = RequestMethod.GET)
    public String test(){
        RpcProxy rpcProxy = new RpcProxy();
        UserAction userAction = rpcProxy.create(UserAction.class);
        User user = new User();
        user.setUserName("hkr");
        user.setUserId(123456L);
        userAction.greet(user);
        String returnValue = userAction.greet2(user);
        User user1 = userAction.copy(user);
        System.out.println("exe");
        return "exe";
    }

    @RequestMapping(value = "/test/2", method = RequestMethod.GET)
    public String test2(){
        IRpcProxy rpcClient = RpcProxy.createAsync(UserAction.class);
        User user = new User();
        user.setUserName("hkr");
        user.setUserId(123456L);
        RpcFuture rpcFuture = rpcClient.call("greet", user);
        return "exe";
    }

    @RequestMapping(value = "/test/3", method = RequestMethod.GET)
    public String asynTest(){
        IRpcProxy rpcClient = RpcProxy.createAsync(UserAction.class);
        User user = new User();
        user.setUserName("hkr");
        user.setUserId(123456L);
        RpcFuture rpcFuture = rpcClient.call("asynTest", user);
        return "exe";
    }

    @RequestMapping(value = "/test/4", method = RequestMethod.GET)
    public String timeoutTest() throws InterruptedException {
        IRpcProxy rpcClient = RpcProxy.createAsync(UserAction.class);
        User user = new User();
        user.setUserName("hkr");
        user.setUserId(123456L);
        RpcFuture rpcFuture = rpcClient.call("asynTest", user);
        RpcResponse response = rpcFuture.get(3, TimeUnit.SECONDS);
        return "exe";
    }

    @RequestMapping(value = "/test/5", method = RequestMethod.GET)
    public String asyncTest() throws InterruptedException {
        IRpcProxy rpcClient = RpcProxy.createAsync(UserAction.class);
        User user = new User();
        user.setUserName("hkr");
        user.setUserId(123456L);
        RpcFuture rpcFuture = rpcClient.call("asynTest", user);
        RpcResponse response = rpcFuture.get(40, TimeUnit.SECONDS);
        return "exe";
    }

    @RequestMapping(value = "/test/6", method = RequestMethod.GET)
    public String callBackTest() throws InterruptedException {
        IRpcProxy rpcClient = RpcProxy.createAsync(UserAction.class);
        User user = new User();
        user.setUserName("hkr");
        user.setUserId(123456L);
        RpcFuture rpcFuture = rpcClient.call("asynTest", user);
        rpcFuture.addCallBack(new AsynRpcCallBack() {
            @Override
            public void success(Object result) {
                System.out.println("test 6 success");
            }

            @Override
            public void fail(Exception e) {

            }
        });
        RpcResponse response = rpcFuture.get(40, TimeUnit.SECONDS);
        return "exe";
    }
}
