package org.gary.nettyrpc.client;

public class RpcClient {

    private String zkAddress;

    public RpcClient(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public <T> T getImpl(Class<T> serviceClass) {
        ProxyHandler pf = new ProxyHandler(zkAddress);
        return (T) pf.getImplObj(serviceClass);
    }


}
