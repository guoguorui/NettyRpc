package org.gary.nettyrpc.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;

public class MyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private RpcClientHandler rpcClientHandler;

    MyChannelInitializer(RpcClientHandler rpcClientHandler) {
        this.rpcClientHandler = rpcClientHandler;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        socketChannel.pipeline().addLast(rpcClientHandler);
    }
}
