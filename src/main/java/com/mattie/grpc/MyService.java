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

    @Override
    public StreamObserver<HelloStreamRequest> biStream(StreamObserver<HelloStreamResponse> responseObserver) {
        return new StreamObserver<HelloStreamRequest>() {
            @Override
            public void onNext(HelloStreamRequest value) {
                System.out.println("client says: " + value.getRequestInfo());
                responseObserver.onNext(HelloStreamResponse.newBuilder().setResponseInfo("hello client").build());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onCompleted() {
                responseObserver.onCompleted();
            }
        };
    }
}
