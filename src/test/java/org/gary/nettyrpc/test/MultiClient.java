package org.gary.nettyrpc.test;

import org.gary.nettyrpc.client.RpcClient;
import org.gary.nettyrpc.pojo.User;
import org.gary.nettyrpc.service.UserService;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MultiClient {

    public static void main(String[] args) {
        RpcClient rpcClient=new RpcClient("127.0.0.1:2181");
        UserService userService=rpcClient.getImpl(UserService.class);
        for(int i=0;i<5;i++){
            ClientTask clientTask=new ClientTask(userService);
            clientTask.start();
        }
    }

}

class ClientTask extends Thread{

    private UserService userService;

    ClientTask(UserService service){
        userService=service;
    }

    @Override
    public void run() {
        for(int j=0;j<20;j++){
            User user = userService.getUser();
            System.out.println("泪流满面: " + user.getName());
            System.out.println("泪流满面: " + user.getPassword());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}