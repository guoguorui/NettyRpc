package org.gary.nettyrpc.client;

import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.zookeeper.ServiceDiscover;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyHandler implements InvocationHandler {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private NettyClient nettyClient;
    private ServiceDiscover sd;
    private String serviceName;
    private String serverAddress;
    private Class serviceClass;

    ProxyHandler(String zkAddress){
        sd = new ServiceDiscover(zkAddress);
    }

    @SuppressWarnings("unchecked")
    <T> T getImplObj(Class<T> serviceClass) {
        this.serviceClass=serviceClass;
        serviceName=serviceClass.getSimpleName();
        serverAddress = sd.discover(serviceName,null);
        connect();
        return  (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        int id = atomicInteger.addAndGet(1);
        RpcResponse rpcResponse = nettyClient.call(method, args, id);
        System.out.println(Thread.currentThread()+"收到回应了  ：" + rpcResponse.getId());
        if (rpcResponse.getStatus() == -1) {
            atomicInteger.decrementAndGet();
            serverAddress=sd.discover(serviceName,serverAddress);
            connect();
            try {
                return method.invoke(proxy, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rpcResponse.getResult();
    }

    private void connect(){
        nettyClient = new NettyClient(serviceClass, serverAddress);
        Thread thread=new Thread(){
            @Override
            public void run() {
                nettyClient.connect();
            }
        };
        thread.start();
    }
}