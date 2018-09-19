# grpc_demo
simple grpc demo with server and client


本文是gRPC的一个简单例子，以protocol buffers 3作为契约类型，使用gRPC自动生成服务端和客户端代码，实现服务的远程调用。


![gRPC](https://upload-images.jianshu.io/upload_images/420187-7a286fddf39d7a56.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


- 创建gradle类型工程，`build.gradle`文件如下，包含了根据`.proto`自动生成代码的插件.

      apply plugin: 'java'
      apply plugin: 'com.google.protobuf'

      buildscript {
          repositories {
              maven {
                  url "http://maven.aliyun.com/nexus/content/groups/public/" }
          }
          dependencies {
              classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.5'
          }
      }

      repositories {
          maven {
              url "http://maven.aliyun.com/nexus/content/groups/public/" }
          mavenLocal()
      }

      def grpcVersion = '1.14.0'
      def nettyTcNativeVersion = '2.0.7.Final'
      def protobufVersion = '3.5.1'

      dependencies {
          compile "com.google.api.grpc:proto-google-common-protos:1.0.0"
          compile "io.grpc:grpc-alts:${grpcVersion}"
          compile "io.grpc:grpc-netty:${grpcVersion}"
          compile "io.grpc:grpc-protobuf:${grpcVersion}"
          compile "io.grpc:grpc-stub:${grpcVersion}"
          compileOnly "javax.annotation:javax.annotation-api:1.2"
          compile "io.netty:netty-tcnative-boringssl-static:${nettyTcNativeVersion}"
          compile "com.google.protobuf:protobuf-java-util:${protobufVersion}"
      }

      protobuf {
          protoc {
              artifact = "com.google.protobuf:protoc:3.5.1-1"
          }
          plugins {
              grpc {
                  artifact = 'io.grpc:protoc-gen-grpc-java:1.15.0'
              }
          }
          generateProtoTasks {
              all()*.plugins {
                  grpc {}
              }
          }
      }

- 创建目录`src/main/proto`，并在其中新建契约文件`helloworld.proto`。这里定义一个请求类型`HelloRequest`、一个响应类型`HelloReply`，和一个简单的服务（`service`）`Greeter`。`Greeter`只提供一个简单的**RPC**服务（**simple RPC**）`sayHello`

      syntax = "proto3";

      package com.mattie.grpc;

      option java_package = "com.mattie.grpc";
      option java_outer_classname = "HelloWorldProtos";

      service Greeter {
        rpc SayHello (HelloRequest) returns (HelloReply) {}
      }

      message HelloRequest {
        string message = 1;
      }

      message HelloReply {
        string message = 1;
      }



- 运行命令`gradle clean build`，在工程目录下会生成 `build`文件夹，其中`generated`目录下包含由插件`io.grpc:protoc-gen-grpc-java`所生成的RPC代码。

![生成的代码目录](https://upload-images.jianshu.io/upload_images/420187-c403ca338e0bf3b7.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 生成的`GreeterGrpc.java`中包含**自定义服务需要继承的抽象类`GreeterImplBase`**，以及**`stub`**。
`GreeterStub`（异步返回response）和`GreeterBlockingStub`（同步等待response）。客户端通过`stub`调用服务端。
![image.png](https://upload-images.jianshu.io/upload_images/420187-9253fa25663837a2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

- 定义自己的服务，继承`GreeterImplBase`并重写契约中定义的服务`sayHello`，实现真正的业务逻辑。`onNext`方法用来返回 `helloReply`对象给客户端，`onCompleted`方法用来标明此次RPC调用已结束。

      public class MyService extends GreeterImplBase {
          @Override
          public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
              HelloReply helloReply = HelloReply.newBuilder().setMessage("hello client.").build();
              responseObserver.onNext(helloReply);
              responseObserver.onCompleted();
          }
      }

- 创建好自定义服务后，就可以新建和启动一个服务器，用来接收客户端的连接。

      1. 使用`ServerBuilder`创建服务器，`forPort`方法监听端口
      
      2. 创建`MyService `的一个实例，并传递给`ServerBuilder`的`addService`方法
      
      3. 调用`build`和`start`启动服务器

      public class MyServer {

          public static void main(String[] args) {
              ServerBuilder<?> serverBuilder = ServerBuilder.forPort(8899);
              serverBuilder.addService(new MyService());
              Server server = serverBuilder.build();
              try {
                  server.start();
                  server.awaitTermination();
              } catch (IOException | InterruptedException e) {
                  e.printStackTrace();
              }
          }
      }

- 创建客户端:

  1.创建`gRPC channel`，将`stub`与服务器连接：这里使用`ManagedChannelBuilder`来指定主机和端口
  
 2. 将创建的`channel`作为参数，创建`stub`
  
 3. 使用`stub`像调用本地方法一样调用远程服务

            public class MyClient {

          public static void main(String[] args) {
              //使用usePlaintext，否则使用加密连接
              ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("localhost", 8899).usePlaintext();
              ManagedChannel channel = channelBuilder.build();

              GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
              HelloWorldProtos.HelloReply helloReply = blockingStub.sayHello(HelloWorldProtos.HelloRequest.newBuilder().setMessage("hello wolrd").build());
              System.out.println(helloReply.getMessage());
            }
          }

> 注：clone仓库后，运行`gradle clean build`生成build文件夹，并将`main`目录设置为`source root`。
![main目录设置为Sources](https://upload-images.jianshu.io/upload_images/420187-71fdbbfc9d3c29c4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)





  
