package org.gary.nettyrpc.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;
import org.jboss.netty.util.internal.ConcurrentHashMap;

import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

public class ClientChannelHandler extends ChannelInboundHandlerAdapter {

    private ConcurrentHashMap<Integer,CountDownLatch> idToSignal =new ConcurrentHashMap<>();
    ConcurrentHashMap<Integer,RpcResponse> idToResult =new ConcurrentHashMap<>();
    private ChannelHandlerContext ctx;

    void sendRpcRequest(RpcRequest rpcRequest,CountDownLatch countDownLatch){
        idToSignal.put(rpcRequest.getId(),countDownLatch);
        for (int id:idToSignal.keySet()) {
            System.out.print(id+" ");
        }
        System.out.println();
        ctx.writeAndFlush(rpcRequest);
        System.out.println(Thread.currentThread()+"请求发出去了："+rpcRequest.getId());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx=ctx;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        RpcResponse rpcResponse=(RpcResponse) msg;
        idToResult.put(rpcResponse.getId(),rpcResponse);
        CountDownLatch countDownLatch= idToSignal.get(rpcResponse.getId());
        if(countDownLatch!=null){
            idToSignal.remove(rpcResponse.getId());
            countDownLatch.countDown();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("与服务器断开"+ctx.channel().remoteAddress());
        cause.printStackTrace();
        ctx.close();
    }

}
