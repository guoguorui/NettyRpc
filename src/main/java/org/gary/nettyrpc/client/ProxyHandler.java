package org.gary.nettyrpc.client;

import org.gary.nettyrpc.carrier.RpcResponse;
import org.gary.nettyrpc.common.UnsafeUtil;
import org.gary.nettyrpc.zookeeper.ServiceDiscover;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

public class ProxyHandler implements InvocationHandler {

    private static AtomicInteger atomicId = new AtomicInteger(0);
    private NettyClient nettyClient;
    private ServiceDiscover sd;
    private String serviceName;
    private String serverAddress;
    private Class serviceClass;
    private volatile int connectFlag;
    private AtomicInteger waitNum = new AtomicInteger(0);;

    ProxyHandler(String zkAddress){
        sd = new ServiceDiscover(zkAddress);
    }

    @SuppressWarnings("unchecked")
    <T> T getImplObj(Class<T> serviceClass) {
        this.serviceClass=serviceClass;
        serviceName=serviceClass.getSimpleName();
        connect();
        return  (T) Proxy.newProxyInstance(serviceClass.getClassLoader(), new Class[]{serviceClass}, this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) {
        int id = atomicId.addAndGet(1);
        RpcResponse rpcResponse = nettyClient.call(method, args, id);
        System.out.println(Thread.currentThread()+"收到回应了  ：" + rpcResponse.getId());
        if (rpcResponse.getStatus() == -1) {
            try {
                atomicId.decrementAndGet();
                waitNum.incrementAndGet();
                synchronized (this){
                    if(UnsafeUtil.cas(this,0,1)){
                        System.out.println(Thread.currentThread()+"我是领头线程，带头连接");
                        connect();
                        waitNum.decrementAndGet();
                        System.out.println("服务可用了");
                    }else{
                        System.out.println(Thread.currentThread()+"我是小弟线程，已经被唤醒");
                        if(waitNum.decrementAndGet()==0)
                            UnsafeUtil.cas(this,1,0);
                    }
                }
                return method.invoke(proxy, args);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return rpcResponse.getResult();
    }

    private void connect(){
        serverAddress=sd.discover(serviceName,serverAddress);
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