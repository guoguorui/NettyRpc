package org.gary.nettyrpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.common.FastJsonSerializer;

import java.lang.reflect.Method;

public class RpcServerHandler extends ChannelInboundHandlerAdapter {

	FastJsonSerializer serializer = new FastJsonSerializer();
	String implPackage;

	public RpcServerHandler(String implPackage) {
		super();
		this.implPackage = implPackage;
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		// 接收
		ByteBuf bytebuf = (ByteBuf) msg;
		byte[] req = new byte[bytebuf.readableBytes()];
		bytebuf.readBytes(req);
		RpcRequest cm = serializer.deserialize(req, RpcRequest.class);
		System.out.println(cm.getMessage());
		// class类成员序列化失败,很可能是class本身包含了全局的命名，而在不同项目中的路径不同而序列失败
		// 将接口共享即可

		// 回应
		RpcResponse rpcResponse = new RpcResponse();
		Class<?> interfaceClass = cm.getInterfaceClass();
		Object implObject = ScanImpl.scanType(implPackage, interfaceClass);
		Method[] methods = implObject.getClass().getDeclaredMethods();
		for (Method method : methods) {
			if (method.getName().equals(cm.getMethodName())) {
				rpcResponse.setResult(invokeMethod(implObject, method.getName(), cm.getArgs()));
			}
		}
		byte[] reback = serializer.serialize(rpcResponse);
		bytebuf.clear();
		bytebuf.writeBytes(reback);
		ctx.writeAndFlush(bytebuf);
	}

	public static Object invokeMethod(Object newObj, String methodName, Object[] args) throws Exception {
		Class<?> ownerClass = newObj.getClass();
		Class<?>[] argsClass = null;
		if (args != null) {
			argsClass = new Class[args.length];
			for (int i = 0, j = args.length; i < j; i++) {
				argsClass[i] = args[i].getClass();
			}
		}
		Method method = ownerClass.getMethod(methodName, argsClass);
		return method.invoke(newObj, args);
	}

}
