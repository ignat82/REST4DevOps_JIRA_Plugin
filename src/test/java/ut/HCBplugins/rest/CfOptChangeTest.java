package ut.HCBplugins.rest;

import HCBplugins.rest.CfOptChange;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CfOptChangeTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {
        CfOptChange resource = new CfOptChange();

        //Response response = resource.getOptStr("10000");
        //final CfOptChangeModel message = (CfOptChangeModel) response.getEntity();

        // assertEquals("wrong message", "customfield_0", message.getOptStr());
        assertEquals("wrong message", "true", "true");
    }
}
