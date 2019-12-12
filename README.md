# netty-rpc2.0
nettp rpc learning project 2.0

## structure
  * contract: for test purpose, including interfaces and beans that client and server will use
  * rpc-core: core bean definition and serialize
  * rpc-client
  * rpc-server
  * src: springboot test project

## design
  * no service discovery, keep the learning project as simple as possible

## key point compare with former project
  * change request from sync to async by [RpcFuture](https://github.com/bugmakesprogress/netty-rpc2.0/blob/master/nettyrpc/rpc-core/src/main/java/core/RpcFuture.java). Implements `Future<T>` interface
  * send async method through method definitions instead of `InvocationHandler`
  * achieve callback function with [AsyncRpcCallback](https://github.com/bugmakesprogress/netty-rpc2.0/blob/master/nettyrpc/rpc-core/src/main/java/core/AsynRpcCallBack.java)
  
## reference:
  * [轻量级分布式 RPC 框架](https://my.oschina.net/huangyong/blog/361751)
  * [一个轻量级分布式RPC框架--NettyRpc](https://www.cnblogs.com/luxiaoxun/p/5272384.html)
  * [原始代码](https://github.com/luxiaoxun/NettyRpc)
## degraded project
  * [netty-rpc1.0](https://github.com/bugmakesprogress/netty-rpc)
