package org.gary.nettyrpc.test;

import org.gary.nettyrpc.server.RpcServer;

//考虑zk集群
public class ServerTest {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("127.0.0.1:2181");
        rpcServer.provideService("org.gary.nettyrpc.serviceimpl",8888);
    }
}
