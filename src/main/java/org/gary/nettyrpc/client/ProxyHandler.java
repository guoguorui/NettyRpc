package org.gary.nettyrpc.client;

import org.gary.nettyrpc.carrier.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyHandler implements InvocationHandler {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private RpcClient rpcClient;


    <T> T getImplObj(Class<T> serviceClass, String serverAddress) {
        rpcClient = new RpcClient(serviceClass, serverAddress);
        rpcClient.connect();
        @SuppressWarnings("unchecked")
        T implObj = (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, this);
        return implObj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        int id = atomicInteger.addAndGet(1);
        RpcResponse rpcResponse = rpcClient.call(method, args, id);
        System.out.println("收到回应了：" + id);
        if (rpcResponse.getStatus() == -1) {
            rpcClient = new RpcClient(rpcClient.getServiceClass(), "127.0.0.1:9999");
            rpcClient.connect();
            try {
                return method.invoke(proxy, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rpcResponse.getResult();
    }
}