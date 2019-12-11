package user;

public interface UserAction {

    public void greet(User user);

    public String greet2(User user);

    public User copy(User user);

    public User asynTest(User user) throws InterruptedException;
}
