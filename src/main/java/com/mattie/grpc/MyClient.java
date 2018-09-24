package com.mattie.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

public class MyClient {

    public static void main(String[] args) {
        //使用usePlaintext，否则使用加密连接
        ManagedChannelBuilder<?> channelBuilder = ManagedChannelBuilder.forAddress("localhost", 8899).usePlaintext();
        ManagedChannel channel = channelBuilder.build();

//        GreeterGrpc.GreeterBlockingStub blockingStub = GreeterGrpc.newBlockingStub(channel);
//        HelloWorldProtos.HelloReply helloReply = blockingStub.sayHello(HelloWorldProtos.HelloRequest.newBuilder().setMessage("hello wolrd").build());
//        System.out.println(helloReply.getMessage());

        //双向流式通信
        StreamObserver<HelloWorldProtos.HelloStreamRequest> requestObserver = GreeterGrpc.newStub(channel).biStream(new StreamObserver<HelloWorldProtos.HelloStreamResponse>() {
            @Override
            public void onNext(HelloWorldProtos.HelloStreamResponse value) {
                System.out.println(value.getResponseInfo());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {

            }
        });

        requestObserver.onNext(HelloWorldProtos.HelloStreamRequest.newBuilder().setRequestInfo("hello server1").build());
        requestObserver.onNext(HelloWorldProtos.HelloStreamRequest.newBuilder().setRequestInfo("hello server2").build());
        requestObserver.onNext(HelloWorldProtos.HelloStreamRequest.newBuilder().setRequestInfo("hello server3").build());
        requestObserver.onNext(HelloWorldProtos.HelloStreamRequest.newBuilder().setRequestInfo("hello server4").build());
        requestObserver.onCompleted();


        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
