package ru.otus.protobuf.client;

import io.grpc.stub.StreamObserver;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.protobuf.NumberMessage;

public class ClientStreamObserver implements StreamObserver<NumberMessage> {

    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private final CountDownLatch latch;
    private final AtomicInteger lastValueFromServer = new AtomicInteger(0);

    public ClientStreamObserver(CountDownLatch latch) {
        this.latch = latch;
    }

    public int getAndResetLastValue() {
        return lastValueFromServer.getAndSet(0);
    }

    @Override
    public void onNext(NumberMessage value) {
        int num = value.getValue();
        log.info("new value:{}", num);
        lastValueFromServer.set(num);
    }

    @Override
    public void onError(Throwable t) {
        log.error("stream error", t);
        latch.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("request completed");
        latch.countDown();
    }
}
