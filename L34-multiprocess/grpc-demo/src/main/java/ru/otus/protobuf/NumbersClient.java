package ru.otus.protobuf;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.CountDownLatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.client.ClientStreamObserver;

@SuppressWarnings({"squid:S106", "squid:S2142"})
public class NumbersClient {

    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8190;

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(SERVER_HOST, SERVER_PORT)
                .usePlaintext()
                .build();
        log.info("numbers Client is starting...");

        CountDownLatch latch = new CountDownLatch(1);
        NumbersServiceGrpc.NumbersServiceStub asyncStub = NumbersServiceGrpc.newStub(channel);

        NumbersRange range =
                NumbersRange.newBuilder().setFirstValue(0).setLastValue(30).build();

        ClientStreamObserver observer = new ClientStreamObserver(latch);
        asyncStub.generate(range, observer);

        int currentValue = 0;

        for (int i = 0; i <= 50; i++) {
            int serverVal = observer.getAndResetLastValue();
            currentValue = currentValue + serverVal + 1;
            log.info("currentValue:{}", currentValue);
            Thread.sleep(1000);
        }

        latch.await();
        channel.shutdown();
    }
}
