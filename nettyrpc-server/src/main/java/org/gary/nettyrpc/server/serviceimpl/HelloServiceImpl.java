package org.gary.nettyrpc.server.serviceimpl;

import org.gary.nettyrpc.service.HelloService;

public class HelloServiceImpl implements HelloService {

	public String hello() {
		return "hello nico from server in HelloServiceImpl hello()";
	}

	public String helloWithName(String name) {
		return "hello " + name;
	}

	public String hello(String name) {
		return "orverride hello " + name;
	}

}
