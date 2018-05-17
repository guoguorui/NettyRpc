# NettyRpc
新建一个工程将公共的接口定义包装起来<br><br>
如果接口方法的返回值不是JDK提供的类，则新建一个工程来包装接口的返回值类型<br><br>
例子中客户端和服务端都依赖这两个工程
```
<dependency>
    <groupId>org.gary</groupId>
    <artifactId>nettyrpc-pojo</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
<dependency>
    <groupId>org.gary</groupId>
    <artifactId>nettyrpc-service</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```
由于项目中使用到了ZooKeeper，所以要求用户部署ZooKeeper服务器并将其地址传递给RpcClient或RpcServer<br><br>
客户端的用法：<br>
此例中的接口UserService就是公共的接口，User就是公共的POJO，客户端通过getImpl方法从服务端获取到实现接口的类的对象，接下来客户端对该对象的操作就像在操作本地的对象，当然这只是对客户端的表面现象，实际实现并非如此
```
RpcClient rpcClient = new RpcClient("127.0.0.1:2181");
UserService userService = rpcClient.getImpl(UserService.class);
User user=userService.getUser();
System.out.println(user.getName());
System.out.println(user.getPassword());
System.out.println(userService.getPasswordByName("hello"));
```
服务端的用法：<br>
用户除了提供ZooKeeper地址外，还需要提供实现类的包名和Netty服务的端口，该端口暴露给用户的原因是避免与用户其他应用上的端口冲突
```
RpcServer rpcServer = new RpcServer("127.0.0.1:2181");
rpcServer.provideService("org.gary.nettyrpc.serviceimpl",8888);
```
