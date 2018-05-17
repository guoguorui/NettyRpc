package org.gary.nettyrpc.test;

import org.gary.nettyrpc.server.RpcServer;

//考虑zk集群
//考虑缓存，享元设计模式，克隆
//考虑计时自旋获取返回结果
//处理注册服务未真正实现的错误
//获取实现类对象异常时如何返回给用户
//每一个服务器连接负责多少接口实现服务？
public class ServerTest {

    public static void main(String[] args) {
        RpcServer rpcServer = new RpcServer("127.0.0.1:2181");
        rpcServer.provideService("org.gary.nettyrpc.serviceimpl",8888);
    }
}
