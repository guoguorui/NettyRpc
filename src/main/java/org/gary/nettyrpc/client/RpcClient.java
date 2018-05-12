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

public class RpcClient {

	String serverAddress;
	Class<?> serviceClass;

	public RpcClient(String serverAddress) {
		this.serverAddress = serverAddress;
	}

	public Object call(Method method, Object[] args) {
		final RpcRequest rpcRequest = new RpcRequest();
		rpcRequest.setInterfaceClass(serviceClass);
		rpcRequest.setMessage("hello nico from client");
		rpcRequest.setMethodName(method.getName());
		rpcRequest.setArgs(args);
		rpcRequest.setReturnType(String.class);
		EventLoopGroup group = new NioEventLoopGroup();
		final RpcResponse response = new RpcResponse();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(group).channel(NioSocketChannel.class).handler(new ChannelInitializer<SocketChannel>() {
				@Override
				protected void initChannel(SocketChannel channel) throws Exception {
					// 在connect之后就发送
					channel.pipeline().addLast(new RpcClientHandler(rpcRequest));
				}
			}).option(ChannelOption.SO_KEEPALIVE, true);

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

		return response;
	}

	public void setServiceClass(Class<?> serviceClass) {
		this.serviceClass = serviceClass;
	}
}
