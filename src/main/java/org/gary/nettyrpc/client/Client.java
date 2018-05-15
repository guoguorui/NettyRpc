package org.gary.nettyrpc.client;

import org.gary.nettyrpc.zookeeper.ServiceDiscover;

public class Client {

    public static <T> T getImpl(Class<T> clazz, String zkAddress) {
        ProxyHandler pf = new ProxyHandler(zkAddress);
        return (T)pf.getImplObj(clazz);
    }
}
