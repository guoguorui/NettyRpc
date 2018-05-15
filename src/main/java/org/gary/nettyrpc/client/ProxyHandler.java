package org.gary.nettyrpc.client;

import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.zookeeper.ServiceDiscover;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

//解决zk延时问题，服务器实际上已经断开，但仍被发现
public class ProxyHandler implements InvocationHandler {

    private static AtomicInteger atomicInteger = new AtomicInteger(0);
    private RpcClient rpcClient;
    private ServiceDiscover sd;
    private String serviceName;
    private String serverAddress;

    ProxyHandler(String zkAddress){
        sd = new ServiceDiscover(zkAddress);
    }

    @SuppressWarnings("unchecked")
    <T> T getImplObj(Class<T> serviceClass) {
        serviceName=serviceClass.getSimpleName();
        serverAddress = sd.discover(serviceName,null);
        rpcClient = new RpcClient(serviceClass, serverAddress);
        rpcClient.connect();
        return  (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        int id = atomicInteger.addAndGet(1);
        RpcResponse rpcResponse = rpcClient.call(method, args, id);
        System.out.println("收到回应了：" + id);
        if (rpcResponse.getStatus() == -1) {
            atomicInteger.decrementAndGet();
            serverAddress=sd.discover(serviceName,serverAddress);
            rpcClient = new RpcClient(rpcClient.getServiceClass(), serverAddress);
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