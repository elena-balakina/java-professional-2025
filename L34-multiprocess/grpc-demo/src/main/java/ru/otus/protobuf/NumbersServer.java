package ru.otus.protobuf;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.service.NumbersServiceImpl;

@SuppressWarnings("squid:S106")
public class NumbersServer {

    private static final Logger log = LoggerFactory.getLogger(NumbersServer.class);
    public static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws IOException, InterruptedException {
        Server server = ServerBuilder
                .forPort(SERVER_PORT)
                .addService(new NumbersServiceImpl())
                .build();

        server.start();
        log.info("Numbers gRPC server started on port {}", SERVER_PORT);
        log.info("waiting for client connections...");
        server.awaitTermination();
    }
}
