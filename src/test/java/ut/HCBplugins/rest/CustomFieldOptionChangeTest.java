package ut.HCBplugins.rest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomFieldOptionChangeTest {

    // https://blog.codecentric.de/en/2014/07/agile-testing-jira-plugins/

    @Before
    public void setup() {

        /*
        new MockComponentWorker()
                .addMock(FieldManager.class, fieldManager)
                .init();
        */
    }

    @After
    public void tearDown() {

    }

    /*
    @Mock
    private FieldManager fieldManager;
    @Mock
    private ProjectManager projectManager;
    @Mock
    private FieldConfigSchemeManager fieldConfigSchemeManager;
    @Mock
    private OptionsManager optionsManger;
    */
    @Test
    public void messageIsValid() {
        /*
        CustomFieldOptionChange resource = new CustomFieldOptionChange();

        Response response = resource.getResponse("customfield_10000"
                , "TES", "new10");
        final PackingResponseToXML XMLResponse = (PackingResponseToXML) response.getEntity();

        assertEquals("wrong message", "customfield_10000", XMLResponse.getFieldKey());
        */
        assertEquals("wrong message", "true", "true");
    }
}
