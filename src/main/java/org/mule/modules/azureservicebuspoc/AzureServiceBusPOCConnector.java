package org.mule.modules.azureservicebuspoc;

import org.mule.api.annotations.*;
import org.mule.api.annotations.lifecycle.Start;
import org.mule.api.annotations.lifecycle.Stop;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.RefOnly;
import org.mule.api.callback.SourceCallback;

import org.mule.modules.azureservicebuspoc.connection.strategy.AzureConnectionManagement;

import java.util.Map;
import java.util.logging.Logger;

@Connector(name="azure-service-bus-poc", friendlyName="AzureServiceBusPOC")
public class AzureServiceBusPOCConnector {

    private static final Logger LOG = Logger.getLogger(AzureServiceBusPOCConnector.class.getName());

    @Config
    private AzureConnectionManagement connectionManagement;

    @Source
    public void listenQueue(String queue, SourceCallback callback) throws Exception {
        connectionManagement.runQueueListener(queue, callback);
    }

    @Processor
    public void addMessage(String queue, @Default("#[payload]") @RefOnly final String message) throws Exception {
        LOG.info("Sending new message to the queue: " + queue);
        connectionManagement.addMessageToQueue(queue, message);
    }

    @Processor
    public void acknowledgeMessage(@Default("#[payload]") @RefOnly final String message) throws Exception {
        // Method template for acknowledgeMessage operation
    }

    @Start
    public void onStart() {
        LOG.info("AzureServiceBusPOCConnector onStart called.");
    }

    @Stop
    public void onStop() {
        LOG.info("AzureServiceBusPOCConnector onStop called.");
    }

    public AzureConnectionManagement getConnectionManagement() {
        return connectionManagement;
    }

    public void setConnectionManagement(
            AzureConnectionManagement connectionManagement) {
        this.connectionManagement = connectionManagement;
    }
}