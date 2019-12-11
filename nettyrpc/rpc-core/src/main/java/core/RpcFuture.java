package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;

public class RpcFuture implements Future<Object> {

    private static final Logger logger = LoggerFactory.getLogger(RpcFuture.class);

    private RpcRequest rpcRequest;
    private RpcResponse rpcResponse;
    private long startTime;
    private long responseTimeThreshold = 30000L;
    private List<AsynRpcCallBack> callBacks = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();
    private CountDownLatch countDownLatch = new CountDownLatch(1);

    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(16, 16,
            600L, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(65536));

    public RpcFuture(RpcRequest rpcRequest){
        this.rpcRequest = rpcRequest;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return countDownLatch.getCount() == 0;
    }

    public void done(RpcResponse reponse) {
        this.rpcResponse = reponse;
        countDownLatch.countDown();
        invokeCallbacks();
        // Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold) {
            logger.warn("Service response time is too slow. Request id = " + reponse.getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    @Override
    public RpcResponse get() throws InterruptedException {
        countDownLatch.await();
        return this.rpcResponse;
    }

    @Override
    public RpcResponse get(long timeout, TimeUnit unit) throws InterruptedException {
        boolean isSuccess = countDownLatch.await(timeout, unit);
        if(isSuccess){
            return this.rpcResponse;
        }else {
            throw new RuntimeException("Timeout exception. Request id: " + this.rpcRequest.getRequestId()
                    + ". Request class name: " + this.rpcRequest.getClassName()
                    + ". Request method: " + this.rpcRequest.getMethodName());
        }
    }

    public void invokeCallbacks(){
        lock.lock();
        try {
            for (AsynRpcCallBack callBack: callBacks){
                runCallback(callBack);
            }
        }finally {
            lock.unlock();
        }
    }

    public RpcFuture addCallBack(AsynRpcCallBack callBack){
        lock.lock();
        try {
            if(isDone()){
                runCallback(callBack);
            }else {
                this.callBacks.add(callBack);
            }
        }finally {
            lock.unlock();
        }
        return this;
    }

    private void runCallback(final AsynRpcCallBack rpcCallBack){
        final RpcResponse response = this.rpcResponse;
        threadPoolExecutor.submit(() -> {
            if(response.getError() == null){
                rpcCallBack.success(response.getResult());
            }else {
                rpcCallBack.fail(new RuntimeException("Response error", new Throwable(response.getError())));
            }
        });
    }
}
