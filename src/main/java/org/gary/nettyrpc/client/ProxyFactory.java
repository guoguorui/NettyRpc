package org.gary.nettyrpc.client;

import org.gary.nettyrpc.carrier.RpcResponse;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyFactory implements InvocationHandler {

    private RpcClient rpcClient;

    <T> T getImplObj(Class<T> serviceClass, String serverAddress) {
        rpcClient = new RpcClient(serviceClass, serverAddress);
        @SuppressWarnings("unchecked")
        T implObj = (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, this);
        return implObj;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        RpcResponse rpcResponse = (RpcResponse) rpcClient.call(method, args);
        return rpcResponse.getResult();
    }
}