package com.mattie.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

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
