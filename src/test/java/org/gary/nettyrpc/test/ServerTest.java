package org.gary.nettyrpc.test;

import org.gary.nettyrpc.server.RpcServer;


//考虑zk集群
//考虑缓存，享元设计模式，克隆
//考虑计时自旋获取返回结果
public class ServerTest {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("127.0.0.1:2181", 8888);
        rpcServer.provideService("org.gary.nettyrpc.serviceimpl", "UserService");
    }
}
