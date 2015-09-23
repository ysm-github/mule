import org.mule.tck.junit4.FunctionalTestCase;

import org.junit.Test;

/**
 *
 */
public class ConnectionMetadataTestCase extends FunctionalTestCase
{

    @Test
    public void test() throws InterruptedException
    {
        while (true)
        {
            Thread.sleep(1000);
        }
    }

    @Override
    protected String getConfigFile()
    {
        return "connection-metadata-config.xml";
    }

    @Override
    public int getTestTimeoutSecs()
    {
        return 9999999;
    }
}
