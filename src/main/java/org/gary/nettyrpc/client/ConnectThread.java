package org.gary.nettyrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

class ConnectThread extends Thread{

    private RpcClientHandler rpcClientHandler;
    private String serverAddress;
    private RpcClient rpcClient;

    ConnectThread(RpcClientHandler rpcClientHandler,String serverAddress,RpcClient rpcClient){
        this.rpcClientHandler=rpcClientHandler;
        this.serverAddress=serverAddress;
        this.rpcClient=rpcClient;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        //这里的handler仍然属于外部类，不会被匿名内部类copy
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).
                    channel(NioSocketChannel.class).
                    handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(rpcClientHandler);
                        }
                    }).
                    option(ChannelOption.SO_KEEPALIVE, true);
            String[] addresses = serverAddress.split(":");
            String host = addresses[0];
            int port = Integer.parseInt(addresses[1]);
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
            rpcClient.setConnected(1);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            System.out.println("与服务器断开连接："+serverAddress);
            rpcClient.setConnected(-1);
            group.shutdownGracefully();
        }
    }
}
