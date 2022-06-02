package it.HCBplugins.rest;

import HCBplugins.rest.PackingResponseToXML;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class OptionsChangeControllerFuncTest {

    @Before
    public void setup() {

    }

    @After
    public void tearDown() {

    }

    @Test
    public void messageIsValid() {

        String baseUrl = System.getProperty("baseurl");
        String resourceUrl = baseUrl + "/rest/cfoptchange/1.0/message";

        RestClient client = new RestClient();
        Resource resource = client.resource(resourceUrl);

        PackingResponseToXML message = resource.get(PackingResponseToXML.class);

        assertEquals("wrong message", "customfield_10000", message.getFieldKey());
    }
}
