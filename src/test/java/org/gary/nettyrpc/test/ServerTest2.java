package org.gary.nettyrpc.test;

import org.gary.nettyrpc.server.RpcServer;
import org.gary.nettyrpc.zookeeper.ServiceRegister;

public class ServerTest2 {

    public static void main(String[] args) {
        String packageName = RpcServer.class.getPackage().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        RpcServer.processRequest(packageName + ".serviceimpl",9999);
    }
}
