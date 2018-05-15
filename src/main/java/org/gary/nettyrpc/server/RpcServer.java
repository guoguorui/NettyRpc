package org.gary.nettyrpc.server;

public class RpcServer {

    public static void provideService(String implPackage, String zkAddress,int nettyPort) {
        String packageName = NettyServer.class.getPackage().getName();
        packageName = packageName.substring(0, packageName.lastIndexOf('.'));
        NettyServer.processRequest(packageName + "." + implPackage, zkAddress, nettyPort);
    }

}
