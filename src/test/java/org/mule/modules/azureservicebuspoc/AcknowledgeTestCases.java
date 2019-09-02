package org.mule.modules.azureservicebuspoc;

import org.junit.Test;

/**
 * @author emin
 * @since 02/09/2019
 */
public class AcknowledgeTestCases extends AbstractConnectorTestCase {

    @Test
    public void acknowledgeMessageTest() throws Exception {
        getConnector().acknowledgeMessage("test");
    }
}
