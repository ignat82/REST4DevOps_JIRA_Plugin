package HCBplugins.rest;

import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
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

/******************************************************************************
 * The core class that does following:
 *  when constructed:
 *  - initialises LoggerUtilsObject ang gets logger from it,
 *  - acquires jira object managers,
 *  afterwards, every time when receives get request:
 *      - invokes constructor of MutableOptionsList object
 *      - invokes that object addNewOption method,
 *      - provides the object to constructor of XML response
 *      - returns the response.
 * the full path to API endpoint(?) looks like:
 * http://{hostname}/jira/rest/cfoptchange/1.0/options?field_key={jira field key}&proj_key={jira project key}&new_opt={new option}
 * http://localhost:2990/jira/rest/cfoptchange/1.0/options&?field_key=customfield_10000&proj_key=TES&new_opt=new3
 *****************************************************************************/
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
                                  FieldConfigSchemeManager fieldConfigSchemeManager,
                                  OptionsManager optionsManger,
                                  PluginSettingsFactory pluginSettingsFactory) {
        logger.info("starting FieldOptionsController instance construction");
        fieldOptionsService = new FieldOptionsService(fieldManager,
                                                      projectManager,
                                                      fieldConfigSchemeManager,
                                                      optionsManger,
                                                      pluginSettingsFactory);
    }

    /**
     *
     */
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // rename to addOption ??
    public Response getOptions(@QueryParam("field_key") String fieldKey,
                                @QueryParam("proj_key") String projKey) {
        logger.info("************* starting getOptions method... ************");
        logger.info("constructed response, returning...");
        return Response.ok(new FieldOptionsXML(
                fieldOptionsService.initializeFieldOptions(fieldKey, projKey))).build();
    }

    @POST
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // rename to addOption ??
    public Response postOption(String requestBody) {
        logger.info("************ starting postOption method... **************");
        return Response.ok(new FieldOptionsXML(
                fieldOptionsService.addNewOption(requestBody))).build();
    }
}
