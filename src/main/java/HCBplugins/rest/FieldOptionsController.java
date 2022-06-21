package HCBplugins.rest;

import HCBplugins.DTO.FieldOptions;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * The core class for handling GET and POST requests to /options endpoint
 */
@Path("/options")
@Named
public class FieldOptionsController {

    private static final Logger logger = LoggerFactory.
            getLogger(FieldOptionsController.class);
    private final FieldOptionsService fieldOptionsService;

    /**
     * constructor initialises logger and receives Jira objects managers trough
     * component accessor
     */
    @Inject
    public FieldOptionsController(FieldManager fieldManager,
                                  ProjectManager projectManager,
                                  OptionsManager optionsManager,
                                  PluginSettingsFactory pluginSettingsFactory) {
        logger.info("starting FieldOptionsController instance construction");
        fieldOptionsService = new FieldOptionsService(fieldManager,
                                                      projectManager,
                                                      optionsManager,
                                                      pluginSettingsFactory);
    }

    /**
     * GET request is used to receive the list of options for customfield in
     * given context. Context is defined by project key and issue type id
     * the request url for GET request looks like:
     * http://{hostname}/jira/rest/cfoptchange/1.0/options?fieldKey={jira field key}&proj_key={
     * jira project key}&new_opt={new option}&issueTypeId={issue type id}
     * http://localhost:2990/jira/rest/cfoptchange/1.0/options?fieldKey=customfield_10000&projKey=test&issueTypeId=10000
     *
     * @param fieldKey - jira customfield key like - cf_10000
     * @param projKey - jira project key like TES
     * @param issueTypeId - jira issue type id like 10000
     * @return xml response in format, defined in FieldOptionToXML class
     */
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // rename to addOption ??
    public Response doGet(@QueryParam("fieldKey") String fieldKey,
                          @QueryParam("projKey") String projKey,
                          @QueryParam("issueTypeId") String issueTypeId) {
        logger.info("************* starting doGet method... ************");
        return Response.ok(new FieldOptionsXML(
                fieldOptionsService.initializeFieldOptions(
                        fieldKey, projKey, issueTypeId))).build();

    }

    /**
     * method for handling POST request to /options endpoint (adding and
     * enabling/disabling given field option
     * @param requestBody - string in Json format with request parameters,
     *  same as for GET request with an extra parameter newOption (option value)
     * @return xml response in format, defined in FieldOptionToXML class
     */
    @POST
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // rename to addOption ??
    public Response doPost(String requestBody) {
        logger.info("************ starting doPost method... **************");
        FieldOptions fieldOptions = fieldOptionsService.postOption(requestBody);
        return (fieldOptions == null)
            ? Response.ok("something goes wrong. Check log file").build()
            : Response.ok(new FieldOptionsXML(fieldOptions)).build();
    }
}
