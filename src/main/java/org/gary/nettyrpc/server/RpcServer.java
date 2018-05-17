package org.gary.nettyrpc.server;

public class RpcServer {

    private String zkAddress;
    private int nettyPort;

    public RpcServer(String zkAddress, int nettyPort) {
        this.zkAddress = zkAddress;
        this.nettyPort = nettyPort;
    }

    public void provideService(String implPackage, String serviceName) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                NettyServer.processRequest(implPackage, serviceName, zkAddress, nettyPort);
            }
        };
        thread.start();
    }

}
