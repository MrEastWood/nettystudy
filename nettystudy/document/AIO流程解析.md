## 基本原理
1. 使用异步的服务器channel AsynchronousServerSocketChannel
2. 使用异步的客户端channel AsynchronousSocketChannel
3. 调用channel的各个方法时，传递一个回调对象(也可以称为策略模式)，此对象实现接口 CompletionHandler  
	实际上，当channel的方法完成时，会调用回调对象的completed()方法  

## 服务端流程
1. 创建一个AsynchronousServerSocketChannel对象(使用类的open方法)  
2. 使用channel的bind方法，启动服务器
3. 调用AsynchronousServerSocketChannel对象的accept方法，并实现接收成功的completed()
4. 在accept成功的completed()方法中，再次调用accept方法，接收新的请求，并通过参数中的AsynchronousSocketChannel，调用read方法
5. 在read()方法中，传入实现read成功的CompletionHandler
6. 在read成功的CompletionHandler的方法中，从ByteBuffer中获取请求数据，并调用channel的write方法返回数据
7. 在write成功的completed()方法中，判断是否已经写完(buffer.hasRemaining())，如果没有写完，再次调用write方法  

## 客户端流程
1. 创建一个AsynchronousSocketChannel对象(使用类的open方法) 
2. 调用channel的connect方法，传入connect完成的CompletionHandler
3. 在connect完成的CompletionHandler中，调用channel的write方法，并传入write完成的CompletionHandler
4. 在write完成的CompletionHandler中，判断是否已经写完，如果没有，再次调用write方法
5. 如果已经写完，调用channel的read方法，并传入read完成的CompletionHandler
6. 在read完成的CompletionHandler中，读取返回数据