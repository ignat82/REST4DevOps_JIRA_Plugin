package it.ru.homecredit.rest;

import ru.homecredit.rest.FieldOptionsXML;
import org.apache.wink.client.Resource;
import org.apache.wink.client.RestClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FieldOptionsControllerFuncTest {

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

        FieldOptionsXML message = resource.get(FieldOptionsXML.class);

        assertEquals("wrong message", "customfield_10000", message.getFieldKey());
    }
}
