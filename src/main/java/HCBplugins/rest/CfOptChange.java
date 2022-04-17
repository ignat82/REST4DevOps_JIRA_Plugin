package HCBplugins.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.logging.*;


/**
 * A resource of message.
 * the full path to API endpoint(?) looks like:
 * http://{hostname}/jira/rest/cfoptchange/1.0/options
 */
@Path("/options")
public class CfOptChange {

    /**************************************************************************
     * method initialises new logger, adds new console and file handlers to it
     * sets the log level to all and changes the system property of log format
     * log string will look like
     * Apr 14, 2022 5:08:49 PM HCBplugins.rest.CfOptChange settingLogger
     * INFO: starting log....
     * @return logger instance with console and file handler added to it
     *************************************************************************/
    private static Logger settingLogger() {
        System.setProperty(
                "java.util.logging.SimpleFormatter.format",
                "%4$s %2$s: %5$s%6$s%n");
        Logger newLogger = Logger.getLogger(CfOptChange.class.getName());
        newLogger.setUseParentHandlers(false);
        Handler conHandler = new ConsoleHandler();
        conHandler.setLevel(Level.ALL);
        newLogger.addHandler(conHandler);
        try {
            Handler fileHandler = new FileHandler("C:\\Users\\digit\\Documents" +
                    "\\JAVA\\Plugin\\REST4DevOps\\log.log", true);
            fileHandler.setLevel(Level.ALL);
            fileHandler.setFormatter(new SimpleFormatter());
            newLogger.addHandler(fileHandler);
            newLogger.info("logger fileHandler creation success");
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
            newLogger.warning("logger fileHandler creation failure");
        }

        newLogger.info("starting log....");
        return newLogger;
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
        Logger logger = settingLogger();
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
