package org.gary.nettyrpc.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.gary.nettyrpc.common.ReflectionUtil;
import org.gary.nettyrpc.common.ServerDecoder;
import org.gary.nettyrpc.common.ServerEncoder;
import org.gary.nettyrpc.zookeeper.ServiceRegister;

import java.net.InetSocketAddress;
import java.util.List;

//一个端口服务一个实现包
class NettyServer {

    static void processRequest(String implPackage, String zkAddress, int nettyPort) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    .addLast(new ServerDecoder(1024 * 1024, 0, 4))
                                    .addLast(new ServerEncoder())
                                    .addLast(new ServerChannelHandler(implPackage));
                        }
                    }).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(new InetSocketAddress(nettyPort)).sync();
            List<String> serviceNames= ReflectionUtil.getInterfaceNames(implPackage);
            if(serviceNames!=null){
                ServiceRegister serviceRegister = new ServiceRegister(zkAddress);
                for(String serviceName:serviceNames)
                    serviceRegister.register(serviceName, "127.0.0.1:" + String.valueOf(nettyPort));
            }
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
