package HCBplugins.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.Logger;


/******************************************************************************
 * The core class that does following:
 *  when constructed:
 *  - initialises logger,
 *  - acquires jira object managers,
 *  afterwards, every time when receives get request:
 *      - invokes constructor of MutableOptionsList object
 *      - invokes that object addNewOption method,
 *      - provides the object to constructor of XML response
 *      - returns the response.
 * the full path to API endpoint(?) looks like:
 * http://{hostname}/jira/rest/cfoptchange/1.0/options?field_key={jira field key}&proj_key={jira project key}&new_opt={new option}
 *****************************************************************************/
@Path("/options")
public class CustomFieldOptionChange {

    private final Logger logger;
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final FieldConfigSchemeManager fieldConfigSchemeManager;
    private final OptionsManager optionsManger;

    /**************************************************************************
     * constructor initialises logger and receives Jira objects manager trough
     * component accessor
     * is it necessary to use "this." if constructor does not receives any
     * parameters?
     *************************************************************************/
    public CustomFieldOptionChange() {
        logger = LoggerUtils
                .createLogger(CustomFieldOptionChange.class.getName());
        fieldManager = ComponentAccessor.getFieldManager();
        getLogger().info("FieldManager acquired - " + getFieldManager());
        projectManager = ComponentAccessor.getProjectManager();
        getLogger().info("ProjectManager acquired - " + getProjectManager());
        fieldConfigSchemeManager
                = ComponentAccessor.getFieldConfigSchemeManager();
        getLogger().info("FieldConfigSchemeManager acquired - "
                + getFieldConfigSchemeManager());
        optionsManger = ComponentAccessor.getOptionsManager();
        getLogger().info("OptionsManager acquired - " + getOptionsManger());
        // is it necessary to check if all the managers acquired properly,
        // to prevent null pointer exception in MutableOptionsList object?
    }


    // http://localhost:2990/jira/rest/cfoptchange/1.0/options?field_id=customfield_10000&proj_id=TES&new_opt=new3

    /**************************************************************************
     * the core method which receives the GET parameters,
     * initialises the logger, creates new instance of MutableOptionsList
     * nested class, invokes the .addNew() method and constructs the response
     * with CfOptChangeModel class constructor
     * @param field_key the <em>key</em> of the <em>customfield</em> from GET parameter
     * @param proj_key the <em>key</em> of the <em>project</em> from GET parameter
     * @param new_opt  string - the <em>new option</em> from GET parameter
     * @return response in XML format
     *************************************************************************/
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage(@QueryParam("field_key") String field_key
            , @QueryParam("proj_key") String proj_key
            , @QueryParam("new_opt") String new_opt) {
        getLogger().info("starting getMessage method...");
        MutableOptionsList mutableOptionsList
                = new MutableOptionsList(field_key, proj_key, new_opt, getLogger());
        mutableOptionsList.addNew(getLogger(), getFieldManager(), getProjectManager()
                , getFieldConfigSchemeManager(), getOptionsManger());
        Response response = Response.ok(new PackingResponseToXML(mutableOptionsList)).build();
        getLogger().info("constructed response, returning...");
        // is it necessary to reset logger for closing the log file properly?
        // LogManager.getLogManager().reset();
        return response;
    }

    public Logger getLogger() {
        return logger;
    }

    public FieldManager getFieldManager() {
        return fieldManager;
    }

    public ProjectManager getProjectManager() {
        return projectManager;
    }

    public FieldConfigSchemeManager getFieldConfigSchemeManager() {
        return fieldConfigSchemeManager;
    }

    public OptionsManager getOptionsManger() {
        return optionsManger;
    }
}
