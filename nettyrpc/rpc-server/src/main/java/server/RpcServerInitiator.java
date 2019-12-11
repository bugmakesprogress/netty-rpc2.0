package server;

import core.RpcDecoder;
import core.RpcEncoder;
import core.RpcRequest;
import core.RpcResponse;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

public class RpcServerInitiator implements EnvironmentAware, InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(RpcServerInitiator.class);

    private Environment environment;

    private Map<String, Object> handlerMap = new HashMap<>(); // 存放接口名与服务对象之间的映射关系

    private int port = 4437;

    private ChannelFuture future;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workderGroup;

    @Autowired
    private ApplicationContext context;

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        judgeParam();
        setHandlerMap(context);
        bossGroup = new NioEventLoopGroup();
        workderGroup = new NioEventLoopGroup();
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workderGroup).channel(NioServerSocketChannel.class);
        bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new RpcDecoder(RpcRequest.class));
                ch.pipeline().addLast(new RpcEncoder(RpcResponse.class));
                ch.pipeline().addLast(new RpcHandler(handlerMap));
            }
        }).option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

        future = bootstrap.bind(port).sync();
        logger.info("rpc server started on port {}", port);
    }

    public void judgeParam() throws Exception {
        if(environment.getProperty("spring.application.name") == null){
            ((ConfigurableApplicationContext) context).close();
            throw new Exception("No spring.application.name defined, required by RpcServerInitiator");
        }
        try {
            if(environment.getProperty("hkr.rpc.port") != null){
                port = Integer.parseInt(environment.getProperty("hkr.rpc.port"));
            }
        }catch (Exception e){
            logger.warn("init rpc server port failed, please check your settings hkr.rpc.port = {}",
                    environment.getProperty("hkr.rpc.port"));
        }
    }

    public void setHandlerMap(ApplicationContext ctx) {
        // 获取所有带有 RpcService 注解的 Spring Bean
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)) {
            for (Object serviceBean : serviceBeanMap.values()) {
                String interfaceName = serviceBean.getClass().getAnnotation(RpcService.class).value().getName();
                handlerMap.put(interfaceName, serviceBean);
            }
        }
    }

    //注意 这里如果不把关闭相关代码拆分出来的话,会造成线程阻塞 导致后续bean无法初始化
    @Override
    public void destroy() throws Exception {
        future.channel().closeFuture().sync();
        if(bossGroup != null){
            bossGroup.shutdownGracefully();
        }
        if(workderGroup != null){
            workderGroup.shutdownGracefully();
        }
    }
}
