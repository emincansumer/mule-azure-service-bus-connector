package org.mule.modules.azureservicebuspoc.connection.strategy;

import com.microsoft.azure.servicebus.*;
import com.microsoft.azure.servicebus.primitives.ConnectionStringBuilder;
import com.microsoft.azure.servicebus.primitives.ServiceBusException;
import org.mule.api.ConnectionException;
import org.mule.api.MuleContext;
import org.mule.api.annotations.*;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.param.ConnectionKey;
import org.mule.api.callback.SourceCallback;
import org.mule.api.context.MuleContextAware;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author emin
 * @since 28/08/2019
 */
@ConnectionManagement(friendlyName = "Azure Service Bus Connection Configuration")
public class AzureConnectionManagement implements MuleContextAware {

    private static final Logger LOG = Logger.getLogger(AzureConnectionManagement.class.getName());

    @Configurable
    private String serviceBusName;

    private QueueClient receiveClient;
    private QueueClient addClient;
    private String connectionId;
    private String accessKeyName;
    private String accessKey;
    private MuleContext muleContext;

    @Connect
    @TestConnectivity
    public void connect(@ConnectionKey String accessKeyName, @ConnectionKey String accessKey) throws ConnectionException {
        LOG.info("AzureConnectionManagement: Connecting...");
        this.accessKeyName = accessKeyName;
        this.accessKey = accessKey;
        connectionId = serviceBusName + "-" + accessKeyName;
        LOG.info("AzureConnectionManagement: Connected...");
    }

    @Disconnect
    public void disconnect() {
        if (receiveClient != null) {
            try {
                receiveClient.close();
            } catch (ServiceBusException e) {
                e.printStackTrace();
            }
        }
        if (addClient != null) {
            try {
                addClient.close();
            } catch (ServiceBusException e) {
                e.printStackTrace();
            }
        }
        receiveClient = null;
        addClient = null;
    }

    @ValidateConnection
    public boolean validateConnection() {
        return  receiveClient != null;
    }

    @ConnectionIdentifier
    public String getConnectionId() {
        return connectionId;
    }

    public void runQueueListener(String queueName, SourceCallback callback) {
        ConnectionStringBuilder csb = new ConnectionStringBuilder(serviceBusName, queueName, accessKeyName, accessKey);
        try {
            receiveClient = new QueueClient(csb, ReceiveMode.PEEKLOCK);
            registerReceiver(receiveClient, callback);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addMessageToQueue(String queueName, String message) {
        ConnectionStringBuilder csb = new ConnectionStringBuilder(serviceBusName, queueName, accessKeyName, accessKey);
        try {
            addClient = new QueueClient(csb, ReceiveMode.PEEKLOCK);
            LOG.info("Adding new message to the queue: " + queueName + " - message: " + message);
            addClient.send(new Message(message));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerReceiver(QueueClient receiveClient,
                                  final SourceCallback callback) throws ServiceBusException, InterruptedException {
        receiveClient.registerMessageHandler(new IMessageHandler() {
            @Override
            public CompletableFuture<Void> onMessageAsync(IMessage message) {
                try {
                    callback.process(new String(message.getBody(), UTF_8));
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return CompletableFuture.completedFuture(null);
            }

            @Override
            public void notifyException(Throwable exception, ExceptionPhase phase) {

            }
        },
        new MessageHandlerOptions(1, true, Duration.ofMinutes(1)));
    }

    @Override
    public void setMuleContext(MuleContext muleContext) {
        this.muleContext = muleContext;
    }

    public MuleContext getMuleContext() {
        return muleContext;
    }

    public String getServiceBusName() {
        return serviceBusName;
    }

    public void setServiceBusName(String serviceBusName) {
        this.serviceBusName = serviceBusName;
    }
}
