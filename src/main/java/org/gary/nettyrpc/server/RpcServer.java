package org.gary.nettyrpc.server;

public class RpcServer {

    private String zkAddress;

    public RpcServer(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public void provideService(String implPackage,int nettyPort) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                NettyServer.processRequest(implPackage, zkAddress, nettyPort);
            }
        };
        thread.start();
    }

}
