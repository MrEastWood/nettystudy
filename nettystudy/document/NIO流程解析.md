## 基本原理
	使用IO的多路复用技术，实现伪异步操作，其中，selecter是一直在后台运行的。  
	每次建立一个channel，或channel状态有改变，则在selecter注册监听事件，如接收连接，读等  
	在selector中，轮休注册的channel和事件是否已发生(每次可以筛选出active的channel)  

## 概念
1. Channel，通道，跟之前的stream不同，channel既可以读，又可以写，是双向的  
2. Buffer，缓冲区，通常是一个字节数组。程序直接和缓冲区交互  
3. Selector，多路复用器，多路复用器可以监听到channel的状态  

## 流程  
### 服务端
1. 创建一个selector  
	Selector.open()  
2. 创建一个serverSocketChannel，并设置为异步模式  
	ServerSocketChannel.open()  
	channel.configureBlocking(false)  
3. 启动服务，监听端口，可以设置最大长度  
	channel.socket().bind(socketAddress,maxReqLength);  
4. 在Selector中注册channel的accept事件  
	channel.register(selector,SelectionKey.OP_ACCEPT);  
5. 轮询处理，selector是否接收到请求  
	* 设置阻塞时间，直到有请求，或超过设置时间，防止不停的空循环导致占用CPU   
		selector.select(time)  
	* 获取active的key集合  
		selector.selectedKeys()  
	* 迭代处理每一个key，并且获取后，就从set中删除(所以要使用Iterator)  
	* 判断key是处于哪种状态(即获取到哪种事件)，前提:key.isValid()  
		* key.isAcceptable()，此时是收到一个连接请求:  
			获取channel，此时是一个ServerSocketChannel : key.channel()  
			获取一个socketChannel: serverSocketChannel.accept()  
			socketChannel设置为异步模式: socketChannel.configureBlocking(false);  
			socketChannel注册读取的事件 : socketChannel.register(selector, SelectionKey.OP_READ)  
		* key.isReadable()  
			开始进行读取，及后续处理，同样先通过key获取到channel  

### 客户端 
1. 创建一个selector
	Selector.open()
2. 创建一个SocketChannel，并设置为异步模式
	SocketChannel.open()
	channel.configureBlocking(false)
3. 启动服务，监听端口，可以设置最大长度
	channel.socket().bind(socketAddress,maxReqLength);
4. 在Selector中注册channel的connect事件
	channel.register(selector, SelectionKey.OP_CONNECT);
5. 连接服务器
	channel.connect(new InetSocketAddress(host, port));
6. 轮询处理，selector是否接收到请求，同服务器，但是监听的事件为key.isConnectable()
				
					
## buffer的读取和写入步骤:
* 读取:  
	* 先为ByteBuffer分配内存空间 : ByteBuffer.allocate(size)
	* 从channel中读取数据: size = channel.read(buffer)，如果size = -1，表示客户端关闭了连接
	* 重置buffer的指针，指向开头 : buffer.flip()
	* 创建一个byte[]，用于从buffer读取字节 : bytes = new byte[buffer.remaining()]
	* 从buffer读取字节 : buffer.get(bytes)
	* 将字节数组转换为字符串  : new String(bytes,charSet) 
* 写入:
	* 将字符串转换为字节数组 : bytes = msg.getBytes()
	* 为缓冲区分配空间，大小为字节数组的大小 :  ByteBuffer.allocate(bytes.length)
	* 写入缓冲区 : buffer.put(bytes)
	* 重置buffer的指针，指向开头 : buffer.flip()
	* 将缓冲区数据写入channel : channel.write(buffer);


## 问题  
* 在客户端也使用NIO是否有点浪费？  
* 个人猜想可以在客户端使用传统的BIO方式
			
	
	