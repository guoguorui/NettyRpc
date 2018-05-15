package org.gary.nettyrpc.test;

import org.gary.nettyrpc.server.RpcServer;


//考虑zk集群
public class ServerTest {

    public static void main(String[] args) {
        RpcServer.provideService("serviceimpl","127.0.0.1:2181",8888);
    }
}
