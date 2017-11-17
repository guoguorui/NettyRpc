# NettyRpc

the usage of client:</br>
```
	TestService testService=Client.getImpl(TestService.class);
	if(testService!=null) {
		System.out.println(testService.test());
	}
```
client dependencies:</br>
```
    <dependency>
      <groupId>org.gary</groupId>
      <artifactId>nettyrpc-client</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
    
    <dependency>
      <groupId>org.gary</groupId>
      <artifactId>testservice</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
```

the usage of server:</br>
```
	ServiceRegister.register("TestService", "127.0.0.1:8888");
	RpcServer.processRequest(ServerTest.class.getPackage().getName()+".serviceimpl");
```
server dependencies:</br>
```
    <dependency>
      <groupId>org.gary</groupId>
      <artifactId>nettyrpc-server</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
    
    <dependency>
      <groupId>org.gary</groupId>
      <artifactId>testservice</artifactId>
      <version>0.0.1-SNAPSHOT</version>
    </dependency>
```

the project org.gary.testservice contains common service interface.
