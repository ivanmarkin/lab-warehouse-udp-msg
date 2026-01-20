package net.ivm.lab.warehouse.in;

import net.ivm.lab.warehouse.TestUtil;
import net.ivm.lab.warehouse.out.NatsPublisher;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class UdpListenerTest {
    private static final int SLEEP_TO_LET_START = 200;

    private AutoCloseable closeable;

    @Mock
    private NatsPublisher natsPublisher;

    @BeforeEach
    public void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    public void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    public void testUdpMessageReceivedAndPublished() throws Exception {
        String testMessage = "sensor_id=t501; value=56";
        doRunTest(testMessage, 10998, 10998);

        verify(natsPublisher, timeout(1000)).publish(testMessage);
    }

    @Test
    public void testUdpMessageSentToWrongPort() throws Exception {
        String testMessage = "sensor_id=t501; value=56";
        doRunTest(testMessage, 10998, 10999);

        verify(natsPublisher, never()).publish(anyString());
    }

    private void doRunTest(String testMessage, int openUdpOnPort, int testMessagePort) throws InterruptedException, IOException {
        UdpListener udpListener = new UdpListener(openUdpOnPort, natsPublisher);
        udpListener.start();

        Thread.sleep(SLEEP_TO_LET_START);
        TestUtil.sendUdpMessage("localhost", testMessagePort, testMessage);
        Thread.sleep(SLEEP_TO_LET_START);
    }
}