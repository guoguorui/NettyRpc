package org.gary.nettyrpc.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.common.SerializeUtils;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class RpcClientHandler extends ChannelInboundHandlerAdapter {

    private HashMap<Integer,CountDownLatch> idToSignal =new HashMap<>();
    HashMap<Integer,RpcResponse> idToResult =new HashMap<>();
    private ChannelHandlerContext ctx;

    void sendRpcRequest(RpcRequest rpcRequest,CountDownLatch countDownLatch){
        idToSignal.put(rpcRequest.getId(),countDownLatch);
        byte[] request = SerializeUtils.serialize(rpcRequest, RpcRequest.class);
        ByteBuf clientMessage = Unpooled.buffer(request.length);
        clientMessage.writeBytes(request);
        ctx.writeAndFlush(clientMessage);
        System.out.println("请求发出去了："+rpcRequest.getId());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] response = new byte[buf.readableBytes()];
        buf.readBytes(response);
        RpcResponse rpcResponse = SerializeUtils.deserialize(response, RpcResponse.class);
        idToResult.put(rpcResponse.getId(),rpcResponse);
        CountDownLatch countDownLatch= idToSignal.get(rpcResponse.getId());
        countDownLatch.countDown();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }

}
