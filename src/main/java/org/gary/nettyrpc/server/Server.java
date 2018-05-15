package org.gary.nettyrpc.server;

public class Server {

    public static void provideService(String implPackage, String zkAddress,int nettyPort) {
        String packageName = RpcServer.class.getPackage().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        RpcServer.processRequest(packageName + "." + implPackage, zkAddress, nettyPort);
    }

}
