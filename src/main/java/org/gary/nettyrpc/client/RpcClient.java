package org.gary.nettyrpc.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.CountDownLatch;

class RpcClient {

    private Class<?> serviceClass;
    private String serverAddress;
    //handler是读写线程和连接线程的枢纽
    private RpcClientHandler rpcClientHandler;
    //0代表连接还未建立，1代表连接已建立，-1代表连接中断
    private volatile int connected;


    RpcClient(Class<?> serviceClass, String serverAddress) {
        this.serviceClass = serviceClass;
        this.serverAddress = serverAddress;
    }

    void connect(){
        rpcClientHandler = new RpcClientHandler();
        System.out.println("尝试与服务器相连:"+serverAddress);
        ConnectThread connectThread=new ConnectThread(rpcClientHandler,serverAddress,this);
        connectThread.start();
    }

    RpcResponse call(Method method,Object[] args,int requestId){
        while (connected<1){
            if(connected==-1)
                return getErrorResponse();
        }
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setInterfaceClass(serviceClass);
        rpcRequest.setMessage("hello nico from client");
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setArgs(args);
        rpcRequest.setId(requestId);
        CountDownLatch countDownLatch=new CountDownLatch(1);
        rpcClientHandler.sendRpcRequest(rpcRequest,countDownLatch);
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HashMap<Integer,RpcResponse> map=rpcClientHandler.idToResult;
        return map.get(requestId);
    }

    private RpcResponse getErrorResponse(){
        RpcResponse rpcResponse=new RpcResponse();
        rpcResponse.setStatus(-1);;
        return rpcResponse;
    }

    private RpcClientHandler getRpcClientHandler() {
        return rpcClientHandler;
    }

    void setConnected(int connected) {
        this.connected = connected;
    }

    int getConnected() {
        return connected;
    }

    Class<?> getServiceClass() {
        return serviceClass;
    }

}

class ConnectThread extends Thread{

    private RpcClientHandler rpcClientHandler;
    private String serverAddress;
    private RpcClient rpcClient;

    ConnectThread(RpcClientHandler rpcClientHandler,String serverAddress,RpcClient rpcClient){
        this.rpcClientHandler=rpcClientHandler;
        this.serverAddress=serverAddress;
        this.rpcClient=rpcClient;
    }

    @Override
    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        //这里的handler仍然属于外部类，不会被匿名内部类copy
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group).
                    channel(NioSocketChannel.class).
                    handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(rpcClientHandler);
                        }
                    }).
                    option(ChannelOption.SO_KEEPALIVE, true);
            String[] addresses = serverAddress.split(":");
            String host = addresses[0];
            int port = Integer.parseInt(addresses[1]);
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).sync();
            rpcClient.setConnected(1);
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            System.out.println("与服务器断开连接："+serverAddress);
            rpcClient.setConnected(-1);
            group.shutdownGracefully();
        }
    }
}
