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

