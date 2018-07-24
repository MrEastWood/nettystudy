## 为什么要使用netty
1. 解决了自己写NIO和AIO的各种问题，如半包，粘包等问题
2. 优化API，能够简单的实现I/O操作
3. 实现各种类型的encoder，decoder，定义了一套API，能够方便的实现自己的编码器，解码器
4. 对各种序列化，反序列化协议有较好的支持，如protobuf，msgpack
6. 对常用的协议有较好的支持，如http，WebSocket  

## netty服务端的创建
1. 创建两个NioEventLoopGroup，一个负责调度，一个负责实际工作(使用new创建)
2. 创建一个服务启动器ServerBootstrap(使用new创建)
3. 将NioEventLoopGroup配置给ServerBootstrap  
	b.group(bossGroup, workerGroup);
4. 配置服务端channel - NioServerSocketChannel
	b.channel(NioServerSocketChannel.class);
5. 设置backlog大小(即是服务端最大全连接的数量，同时受系统配置的限制，取系统值和配置值中最小的)  
	b.option(ChannelOption.SO_BACKLOG, 1024);
6. 配置ChildHandler，ChildHandler 继承 ChannelInitializer<SocketChannel>接口
	b.childHandler(new ChildChannelHandler());
7. ChildHandler实现代码，重写 initChannel()方法，加载各种编码器及解码器，如:  
	* ch.pipeline().addLast(new LineBasedFrameDecoder(1024)); //按行的解码器
	* ch.pipeline().addLast(new StringDecoder()); //将byte转成string的解码器
	* 配置一个ChannelHandlerAdapter()，对各种事件进行处理：  
		ch.pipeline().addLast(new ChannelHandlerAdapter()
	* ChannelHandlerAdapter实现代码，重写需要处理的方法，如: 
		* channelRead() :  
			由于有string解码器的方法，可以直接获取string  
			可以在这里处理返回，在这里使用参数中的ChannelHandlerContext，不需要和channel打交道
		* channelReadComplete()
8. 启动服务器ChannelFuture f = b.bind(port).sync();  
9. 服务器退出后的处理 f.channel().closeFuture().sync();  

## 客户端的创建
1. 创建一个NioEventLoopGroup，一个负责调度，一个负责实际工作(使用new创建)
2. 创建客户端启动器Bootstrap(使用new创建)
3. 将NioEventLoopGroup配置给Bootstrap，并配置channel  
	b.group(group).channel(NioSocketChannel.class);
4. 配置通道参数，TCP_NODELAY，使请求快速响应  
	b.option(ChannelOption.TCP_NODELAY, true);
5. 配置ChildHandler，Handler 继承 ChannelInitializer<SocketChannel>接口
	b.handler(new ChannelHandler());  
6. Handler实现代码，重写 initChannel()方法，加载各种编码器及解码器，同Service实现
7. 配置一个ChannelHandlerAdapter()，对各种事件进行处理:  
	ch.pipeline().addLast(new ChannelHandlerAdapter()  
8. ChannelHandlerAdapter实现代码，重写需要处理的方法，如: 
	* channelActive方法，在连接建立的时候调用
		* 可以向服务器发送请求数据
	* channelRead方法，在可以读取的时候会调用
		* 读取服务器的返回数据
9. 建立连接 ChannelFuture f = b.connect(host, port).sync();
10. 连接关闭后的处理f.channel().closeFuture().sync();

## Netty如何写入和读取数据
* 写入数据 ，需要使用到ByteBuf对象
	* 可以先给ByteBuf分配空间，再写入数据:  
		ByteBuf buf = Unpooled.buffer(length);  
		buf.writeBytes(bytes);
	* 可以直接写入数据:  
		ByteBuf buf = Unpooled.copiedBuffer(str.getBytes());  
	* 通过参数中的ChannelHandlerContext发送数据 :  
		ctx.writeAndFlush(buf);
* 读取数据，可以通过方法中的第二个参数，直接获取
	* 如果没有配置解码器，则接收到的是一个byte数组
	* 如果有配置解码器，则接收到的是解码后的数据，如String，MsgPack对象，protoBuf对象等等