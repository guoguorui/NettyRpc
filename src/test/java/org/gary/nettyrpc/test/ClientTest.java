package org.gary.nettyrpc.test;

import org.gary.nettyrpc.client.Client;
import org.gary.nettyrpc.pojo.User;
import org.gary.nettyrpc.service.HelloService;
import org.gary.nettyrpc.service.UserService;

public class ClientTest {
	public static void main(String[] args) {
		/*HelloService helloService = Client.getImpl(HelloService.class);
		if (helloService != null) {
			System.out.println(helloService.hello());
		}*/
        UserService userInterface=Client.getImpl(UserService.class);
        User user=userInterface.getUser();
        System.out.println("泪流满面: "+user.getName());
        System.out.println("泪流满面: "+user.getPassword());
	}
}
