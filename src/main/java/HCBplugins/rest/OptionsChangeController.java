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
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

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
public class OptionsChangeController {

    private static final Logger logger = LoggerFactory.
            getLogger(OptionsChangeController.class);
    private final MutableOptionsService mos;
    private final SettingsService settingsService;

    /**
     * constructor initialises logger and receives Jira objects managers trough
     * component accessor
     */
    @Inject
    public OptionsChangeController(FieldManager fieldManager,
                                   ProjectManager projectManager,
                                   FieldConfigSchemeManager fieldConfigSchemeManager,
                                   OptionsManager optionsManger,
                                   PluginSettingsFactory pluginSettingsFactory) {
        logger.info("starting OptionsChangeController instance construction");
        mos = new MutableOptionsService(fieldManager,
                                        projectManager,
                                        fieldConfigSchemeManager,
                                        optionsManger);
        settingsService = new SettingsService(pluginSettingsFactory);
    }

    /**
     * the core method which receives the GET parameters,
     * creates new instance of MutableOptionsList nested class, invokes the
     * .addNewOption() method of it and constructs the response with
     * PackingResponseToXML class constructor
     * @param fieldKey the <em>key</em> of the <em>customfield</em> from GET parameter
     * @param projKey the <em>key</em> of the <em>project</em> from GET parameter
     * @param newOpt  string - the <em>new option</em> from GET parameter
     * @return response in XML format
     */
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // rename to addOption ??
    public Response getResponse(@QueryParam("field_key") String fieldKey,
                                @QueryParam("proj_key") String projKey,
                                @QueryParam("new_opt") String newOpt) {
        MutableOptionsObject moo;
        logger.info("starting getResponse method...");
        moo = mos.initializeMoo(fieldKey, projKey);
        List<String> editableFields = settingsService.getSettings().getEditableFields();
        logger.info("editable fields are:");
        for (String field : editableFields) {
            logger.info(field);
        }
        if (editableFields.contains(fieldKey)) {
            logger.info("field {} permitted for accessing trough REST service", newOpt);
            moo = mos.addNewOption(moo, newOpt);
        } else {
            logger.warn("field {} NOT permitted for accessing trough REST service", newOpt);
        }
        Response response = Response.ok(
                new PackingResponseToXML(moo)).build();
        logger.info("constructed response, returning...");
        return response;
    }
}
