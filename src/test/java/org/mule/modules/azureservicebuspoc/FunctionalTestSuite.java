package org.mule.modules.azureservicebuspoc;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.mule.tools.devkit.ctf.mockup.ConnectorTestContext;

/**
 * @author emin
 * @since 02/09/2019
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
        AcknowledgeTestCases.class
                    })
public class FunctionalTestSuite {
    @BeforeClass
    public static void initialiseSuite() {
        ConnectorTestContext.initialize(AzureServiceBusPOCConnector.class);
    }

    @AfterClass
    public static void shutdownSuite() {
        ConnectorTestContext.shutDown();
    }
}
