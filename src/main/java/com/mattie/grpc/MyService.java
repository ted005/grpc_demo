package com.mattie.grpc;

import com.mattie.grpc.GreeterGrpc.GreeterImplBase;
import io.grpc.stub.StreamObserver;

import static com.mattie.grpc.HelloWorldProtos.*;

public class MyService extends GreeterImplBase {
    @Override
    public void sayHello(HelloRequest request, StreamObserver<HelloReply> responseObserver) {
        HelloReply helloReply = HelloReply.newBuilder().setMessage("hello client.").build();
        responseObserver.onNext(helloReply);
        responseObserver.onCompleted();
    }
}
