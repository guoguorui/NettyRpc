package org.gary.nettyrpc.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.common.ReflectionUtil;

public class ServerChannelHandler extends ChannelInboundHandlerAdapter {

    private String implPackage;

    ServerChannelHandler(String implPackage) {
        this.implPackage = implPackage;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcRequest rpcRequest = (RpcRequest) msg;
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult(ReflectionUtil.getResult(rpcRequest,implPackage));
        int id = rpcRequest.getId();
        rpcResponse.setId(id);
        System.out.println("处理完请求：" + id);
        ctx.writeAndFlush(rpcResponse);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        //cause.printStackTrace();
        System.out.println("客户端断开了连接");
        ctx.close();
    }
}
