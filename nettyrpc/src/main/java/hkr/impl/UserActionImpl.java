package hkr.impl;

import server.RpcService;
import user.User;
import user.UserAction;

@RpcService(UserAction.class)
public class UserActionImpl implements UserAction {
    @Override
    public void greet(User user) {
        System.out.println(user.getUserName());
    }

    @Override
    public String greet2(User user) {
        return "hello ~ "+ user.getUserName();
    }

    @Override
    public User copy(User user) {
        User user1 = new User();
        user1.setUserId(user.getUserId());
        user1.setUserName(user.getUserName()+"copy");
        return user1;
    }

    @Override
    public User asynTest(User user) throws InterruptedException {
        Thread.sleep(30000);
        user.setUserId(user.getUserId());
        user.setUserName(user.getUserName()+"async");
        return user;
    }
}
