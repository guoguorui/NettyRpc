package org.gary.nettyrpc.test;

import org.gary.nettyrpc.server.RpcServer;
import org.gary.nettyrpc.zookeeper.ServiceRegister;

public class ServerTest {

    public static void main(String[] args) {
        ServiceRegister serviceRegister = new ServiceRegister("127.0.0.1:2181");
        serviceRegister.register("UserService", "127.0.0.1:8888");
        String packageName = RpcServer.class.getPackage().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        RpcServer.processRequest(packageName + ".serviceimpl");
    }
}
