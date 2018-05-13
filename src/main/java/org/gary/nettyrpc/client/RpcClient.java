package org.gary.nettyrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

class RpcClient {

	private Class<?> serviceClass;
	private String serverAddress;

    RpcClient(Class<?> serviceClass, String serverAddress) {
        this.serviceClass = serviceClass;
        this.serverAddress = serverAddress;
    }

    RpcResponse call(Method method, Object[] args) {
		RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setInterfaceClass(serviceClass);
		rpcRequest.setMessage("hello nico from client");
		rpcRequest.setMethodName(method.getName());
		rpcRequest.setArgs(args);
		EventLoopGroup group = new NioEventLoopGroup();
		RpcClientHandler rpcClientHandler=new RpcClientHandler(rpcRequest);
        MyChannelInitializer myChannelInitializer=new MyChannelInitializer(rpcClientHandler);
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).
                    channel(NioSocketChannel.class).
                    handler(myChannelInitializer).
                    option(ChannelOption.SO_KEEPALIVE, true);
			String[] addresses = serverAddress.split(":");
			String host = addresses[0];
			int port = Integer.parseInt(addresses[1]);
			ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			group.shutdownGracefully();
		}
		return rpcClientHandler.rpcResponse;
	}
}
