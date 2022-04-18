package HCBplugins.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.logging.LogManager;
import java.util.logging.Logger;


/**
 * A resource of message.
 * the full path to API endpoint(?) looks like:
 * http://{hostname}/jira/rest/cfoptchange/1.0/options
 */
@Path("/options")
public class CfOptChange {

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
        Logger logger = LoggerUtils.createLogger(CfOptChange.class.getName());
        logger.info("starting getMessage method...");
        MutableOptionsList mol
                = new MutableOptionsList(field_key, proj_key, new_opt, logger);
        mol.addNew(logger);
        Response response = Response.ok(new CfOptChangeModel(mol.fKey
                , mol.fName, mol.pKey, mol.pName, mol.fConfName, mol.newOpt
                , mol.optList)).build();
        logger.info("constructed response, returning...");
        LogManager.getLogManager().reset();
        return response;
    }
}
