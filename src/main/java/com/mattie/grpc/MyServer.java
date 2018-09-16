package com.mattie.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;

import java.io.IOException;

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
