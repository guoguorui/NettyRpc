package org.gary.nettyrpc.client;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class RpcClient {
	
	Class<?> serviceClass;
	
	public Object call(Method method,Object[] args) {
		final RpcRequest rpcRequest=new RpcRequest();
		rpcRequest.setInterfaceClass(serviceClass);
		rpcRequest.setMessage("hello nico from client");
		rpcRequest.setMethodName(method.getName());
		rpcRequest.setArgs(args);
		rpcRequest.setReturnType(String.class);
		EventLoopGroup group = new NioEventLoopGroup();
		final RpcResponse response = new RpcResponse();
		try {
	        Bootstrap bootstrap = new Bootstrap();
	        bootstrap.group(group).channel(NioSocketChannel.class)
	        .handler(new ChannelInitializer<SocketChannel>() {
	        	@Override
	            protected void initChannel(SocketChannel channel) throws Exception {
	        		//在connect之后就发送
	        		channel.pipeline().addLast(new RpcClientHandler(rpcRequest));
	        	}
	        }).option(ChannelOption.SO_KEEPALIVE, true);
	        
			ChannelFuture future = bootstrap.connect(new InetSocketAddress("127.0.0.1",8888)).sync();
            future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}finally {
            group.shutdownGracefully();
        }
        
		return response;
	}
	
	public void setServiceClass(Class<?> serviceClass) {
		this.serviceClass=serviceClass;
	}
}
