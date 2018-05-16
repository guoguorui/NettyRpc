package org.gary.nettyrpc.test;

import org.gary.nettyrpc.client.RpcClient;
import org.gary.nettyrpc.pojo.User;
import org.gary.nettyrpc.service.UserService;

public class MultiClient {

    public static void main(String[] args) {
        RpcClient rpcClient=new RpcClient("127.0.0.1:2181");
        UserService userService=rpcClient.getImpl(UserService.class);
        for(int i=0;i<3;i++){
            /*ClientThread clientThread=new ClientThread(userService);
            clientThread.start();*/
            Thread thread=new Thread(){
                @Override
                public void run() {
                    for(int j=0;j<3;j++){
                        User user = userService.getUser();
                        System.out.println("泪流满面: " + user.getName());
                        System.out.println("泪流满面: " + user.getPassword());
                    }
                }
            };
            thread.start();
        }
    }

}

class ClientThread extends Thread{

    private UserService userService;

    ClientThread(UserService service){
        userService=service;
    }

    @Override
    public void run() {
        for(int j=0;j<3;j++){
            User user = userService.getUser();
            System.out.println("泪流满面: " + user.getName());
            System.out.println("泪流满面: " + user.getPassword());
        }
    }
}