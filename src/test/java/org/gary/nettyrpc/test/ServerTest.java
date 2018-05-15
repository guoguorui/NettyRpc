package org.gary.nettyrpc.test;

import org.gary.nettyrpc.server.RpcServer;
import org.gary.nettyrpc.server.Server;
import org.gary.nettyrpc.zookeeper.ServiceRegister;


//考虑zk集群
public class ServerTest {

    public static void main(String[] args) {
        Server.provideService("serviceimpl","127.0.0.1:2181",8888);
    }
}
