package org.mule.modules.azureservicebuspoc;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mule.tools.devkit.ctf.junit.AbstractTestCase;

/**
 * @author emin
 * @since 02/09/2019
 */
public class AbstractConnectorTestCase extends AbstractTestCase<AzureServiceBusPOCConnector> {

    public AbstractConnectorTestCase() {
        super(AzureServiceBusPOCConnector.class);
    }

}
