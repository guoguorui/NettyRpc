package org.gary.nettyrpc.client;

import org.gary.nettyrpc.carrier.RpcRequest;
import org.gary.nettyrpc.carrier.RpcResponse;

import java.lang.reflect.Method;
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
        clientChannelHandler = new ClientChannelHandler();
        System.out.println("与服务器建立连接:"+serverAddress);
        NettyChannel nettyChannel =new NettyChannel(this, clientChannelHandler,serverAddress);
        nettyChannel.start();
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

    private ClientChannelHandler getClientChannelHandler() {
        return clientChannelHandler;
    }

    void setConnected(int connected) {
        this.connected = connected;
    }

    Class<?> getServiceClass() {
        return serviceClass;
    }

}

