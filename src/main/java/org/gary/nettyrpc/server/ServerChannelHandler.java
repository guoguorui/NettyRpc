package org.gary.nettyrpc.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.common.SerializeUtils;

import java.lang.reflect.Method;

public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private String implPackage;

    public ServerChannelHandler(String implPackage) {
        this.implPackage = implPackage;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        /*ByteBuf bytebuf = (ByteBuf) msg;
        byte[] request = new byte[bytebuf.readableBytes()];
        bytebuf.readBytes(request);
        for (int i = 0; i < request.length; i++) {
            System.out.print(request[i]+" ");
        }
        System.out.printf("");
        RpcRequest rpcRequest = SerializeUtils.deserialize(request, RpcRequest.class);*/
        RpcRequest rpcRequest=(RpcRequest) msg;
        Class<?> interfaceClass = rpcRequest.getInterfaceClass();
        Object implObject = ScanImpl.scanType(implPackage, interfaceClass);
        Method[] methods = implObject.getClass().getDeclaredMethods();
        RpcResponse rpcResponse = new RpcResponse();
        for (Method method : methods) {
            if (method.getName().equals(rpcRequest.getMethodName())) {
                rpcResponse.setResult(invokeMethod(implObject, method.getName(), rpcRequest.getArgs()));
                break;
            }
        }
        int id=rpcRequest.getId();
        rpcResponse.setId(id);
        System.out.println("处理完请求："+id);
        //byte[] response = SerializeUtils.serialize(rpcResponse, RpcResponse.class);
        //bytebuf.clear();
        //bytebuf.writeBytes(response);
        //ctx.writeAndFlush(bytebuf);
        ctx.writeAndFlush(rpcResponse);
    }

    private static Object invokeMethod(Object implObj, String methodName, Object[] args) throws Exception {
        Class<?> implObjClass = implObj.getClass();
        Class<?>[] argsClass = null;
        if (args != null) {
            argsClass = new Class[args.length];
            for (int i = 0, j = args.length; i < j; i++) {
                argsClass[i] = args[i].getClass();
            }
        }
        Method method = implObjClass.getMethod(methodName, argsClass);
        return method.invoke(implObj, args);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        System.out.println("客户端断开了连接");
        ctx.close();
    }
}
