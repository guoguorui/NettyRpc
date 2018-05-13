package org.gary.nettyrpc.server;

import org.gary.nettyrpc.service.HelloService;

import java.io.File;
import java.net.URL;

public class ScanImpl {
    static Object scanType(String basePackage,Class<?> interfaceClass){
			String path=basePackage.replace(".", "/");
			ClassLoader cl=ScanImpl.class.getClassLoader();
			URL url = cl.getResource(path);
			String fileUrl = url.getFile().substring(1);
			File file=new File(fileUrl);
			String[] names=file.list();
			Object destObject=null;
			for(String name:names) {
				//兄弟，包名不能少啊
				name=basePackage+"."+name.split("\\.")[0];
				Class<?> tempClass=null;
				try {
					tempClass = Class.forName(name);
					if(interfaceClass.isAssignableFrom(tempClass)) {
						try {
							destObject=tempClass.newInstance();
						} catch (Exception e) {
							
						}
					}
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
			return destObject;
		}
	
	public static void main(String[] args) {
		/*
		HelloService hello=(HelloService) ScanImpl.scanType("com.gary.nettyrpc_server.service",HelloService.class);
		System.out.println(hello.hello());
		*/
		
		String basePackage="org.gary.nettyrpc.server.serviceimpl";
		String path=basePackage.replace(".", "/");
		ClassLoader cl=ScanImpl.class.getClassLoader();
		URL url = cl.getResource(path);
		String fileUrl = url.getFile().substring(1);
		//System.out.println(fileUrl);
		File file=new File(fileUrl);
		String[] names=file.list();
		for(String name:names) {
			name=basePackage+"."+name.split("\\.")[0];
			//System.out.println(name);
			Class<?> tempClass=null;
			try {
				tempClass = Class.forName(name);
				System.out.println(tempClass);
				Class<?> interfaceClass=HelloService.class;
				if(interfaceClass.isAssignableFrom(tempClass)) {
					System.out.println("had");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		
	}
}

