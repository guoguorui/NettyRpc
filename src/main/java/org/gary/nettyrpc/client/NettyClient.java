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
import org.gary.nettyrpc.common.ClientDecoder;
import org.gary.nettyrpc.common.ClientEncoder;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

class NettyClient {

    private Class<?> serviceClass;
    private String serverAddress;
    //handler是读写线程和连接线程的枢纽
    private ClientChannelHandler clientChannelHandler;
    //0代表连接还未建立，1代表连接已建立，-1代表连接中断
    private volatile int connected;


    NettyClient(Class<?> serviceClass, String serverAddress) {
        this.serviceClass = serviceClass;
        this.serverAddress = serverAddress;
    }

    void connect(){
        EventLoopGroup group = new NioEventLoopGroup();
        //这里的handler仍然属于外部类，不会被匿名内部类copy
        clientChannelHandler=new ClientChannelHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).
                    channel(NioSocketChannel.class).
                    handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientDecoder(1024*1024,0,4))
                                    .addLast(new ClientEncoder()).addLast(clientChannelHandler);
                        }
                    }).
                    option(ChannelOption.SO_KEEPALIVE, true);
            String[] addresses = serverAddress.split(":");
            String host = addresses[0];
            int port = Integer.parseInt(addresses[1]);
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
            System.out.println("与服务器建立连接："+serverAddress);
            connected=1;
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            System.out.println("与服务器断开连接："+serverAddress);
            connected=-1;
            group.shutdownGracefully();
        }
    }

    RpcResponse call(Method method,Object[] args,int requestId){
        while (connected<1){
            if(connected==-1)
                return getErrorResponse();
        }
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceClass(serviceClass);
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setArgs(args);
        rpcRequest.setId(requestId);
        CountDownLatch countDownLatch=new CountDownLatch(1);
        clientChannelHandler.sendRpcRequest(rpcRequest,countDownLatch);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Integer,RpcResponse> map= clientChannelHandler.idToResult;
        return map.get(requestId);
    }

    private RpcResponse getErrorResponse(){
        RpcResponse rpcResponse=new RpcResponse();
        rpcResponse.setStatus(-1);
        rpcResponse.setId(-1);
        return rpcResponse;
    }

}

