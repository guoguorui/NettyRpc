package org.gary.nettyrpc.client;

import org.gary.nettyrpc.zookeeper.ServiceDiscover;

public class Client {

    public static <T> T getImpl(Class<T> clazz, String zkAddress) {
        ServiceDiscover sd = new ServiceDiscover(zkAddress);
        String serverAddress = sd.discover(clazz.getSimpleName());
        ProxyHandler pf = new ProxyHandler();
        return pf.getImplObj(clazz, serverAddress);
    }
}
