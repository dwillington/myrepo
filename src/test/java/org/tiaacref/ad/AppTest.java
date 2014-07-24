package org.tiaacref.ad;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import com.buildforge.services.client.api.APIClientConnection; 
import com.buildforge.services.client.dbo.*; 
import com.buildforge.services.common.api.APIException; 

/**
 * Unit test for simple App.
 */
public class AppTest 
    extends TestCase
{
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public AppTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( AppTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }
}
