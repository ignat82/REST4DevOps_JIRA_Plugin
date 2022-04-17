package HCBplugins.rest;

import com.atlassian.jira.component.ComponentAccessor;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.ConfigurableField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.issue.fields.config.FieldConfigScheme;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
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

    /***************************************************************************
     * Class that represents Jira entity "Options list" which by Jira logic
     * exists only in specific "context", another Jira entity, defined by
     * specific combination of "field", "project" and "issue type"
     **************************************************************************/
    private static class MutableOptionsList {
        private String fieldKey;
        private ConfigurableField field;
        private String projectKey;
        private Project project;
        private FieldConfigScheme fieldConfSch;
        private FieldConfig fieldConf;
        private OptionsManager optMgr;
        boolean builtSuccessfully = false;

        /**********************************************************************
         * @param fKey the key of the <em>customfield</em> from GET parameter
         * @param pKey the key of the <em>project</em> from GET parameter
         * @param logger just the logger, initialized before invoking the
         *               constructor to keep track of what's going on
         * potential problem - field can have more than one and only
         *             configuration in in a given project
         *********************************************************************/
        MutableOptionsList(String fKey, String pKey, Logger logger) {
            logger.info("field and project keys acquired are " + fKey + "; " + pKey);
            fieldKey = fKey;
            projectKey = pKey;
            logger.info("starting to construct MutableOptionsList object");
            FieldManager fMgr = ComponentAccessor.getFieldManager();
            logger.info("field manager acquired - " + fMgr);
            field = fMgr.getConfigurableField(fieldKey);
            if (field != null) {
                logger.info("field " + fieldKey + " acquired as " + field);
            } else {
                logger.log(Level.WARNING, "FieldManager fails to get field " + fieldKey);
                return;
            }
            ProjectManager pMgr = ComponentAccessor.getProjectManager();
            logger.info("ProjectManager acquired - " + pMgr);
            project = pMgr.getProjectByCurrentKeyIgnoreCase(projectKey);
            if (project != null) {
                logger.info("project " + projectKey + " acquired as " + project);
            } else {
                logger.log(Level.WARNING
                        , "ProjectManager fails to get project " + projectKey);
                return;
            }
            FieldConfigSchemeManager fcsMgr = ComponentAccessor.getFieldConfigSchemeManager();
            logger.info("FieldConfigSchemeManager acquired - " + fcsMgr);
            fieldConfSch = fcsMgr.getRelevantConfigScheme(project, field);
            if (fieldConfSch != null) {
                logger.info("field Configuration Schema acquired as " + fieldConfSch);
            } else {
                logger.log(Level.WARNING
                        , "FieldConfigSchemeManager fails to get Field Config Schema");
                return;
            }
            /* potential problem here. field can have more than one and only
            configuration in in a given project
            */
            fieldConf = fieldConfSch.getOneAndOnlyConfig();
            if (fieldConf != null) {
                logger.info("field Configuration acquired as " + fieldConf);
            } else {
                logger.log(Level.WARNING
                        , "FieldConfig fails to get acquired");
                return;
            }
            optMgr = ComponentAccessor.getOptionsManager();
            logger.info("OptionsManager acquired - " + optMgr);
            Options fieldOptions = optMgr.getOptions(fieldConf);
            if (fieldOptions != null) {
                logger.info("field options acquired");
                logger.info("field options are " + this.getOptionsString());
            } else {
                logger.log(Level.WARNING, "field options fails to get acquired");
            }
            builtSuccessfully = true;
            logger.info("MutableOptionsList object constructed successfully");
        }

        /*********************************************************************
         * method returns the list of options of customfield in single string
         * @return <em>{"option_1", ... , "option_n"}</em> the string of options
         * of invoking the method field instance
         ********************************************************************/
        private String getOptionsString() {
            StringBuilder optStr = new StringBuilder("{");
            Options fieldOptions = optMgr.getOptions(fieldConf);
            for (Option option : fieldOptions) {
                optStr.append("\"").append(option.getValue()).append("\", ");
            }
            optStr.setLength(optStr.length() - 2);
            return optStr.append("}").toString();
        }

        /**********************************************************************
         * @param newOption string - the <em>new option</em> from GET parameter
         * @param logger just the logger, initialized before invoking the
         *          constructor to keep track of what's going on
         * @return <em>true</em> if the option added <em>false</em> if not on some reason
         *********************************************************************/
        private boolean addNew(String newOption, Logger logger) {
            if (newOption == null || !builtSuccessfully) {
                logger.warning("something goes wrong. newOption = " + newOption
                        + "; MutableOptionsList built successfully - "
                        + builtSuccessfully);
                return false;
            }
            logger.info("trying to add new option \"" + newOption + "\"");
            logger.info("checking if option already exists");
            Options fieldOptions = optMgr.getOptions(fieldConf);
            for (Option option : fieldOptions) {
                if (option.getValue().equals(newOption)) {
                    logger.warning("new option \"" + newOption + "\" already exist");
                    return false;
                }
            }
            logger.info("... it doesn't");
            int size = fieldOptions.getRootOptions().size();
            logger.info("there are " + size + "options in list now");
            logger.info("creating new option \"" + newOption + "\"");
            Option newOpt = optMgr.createOption(fieldConf, null, (long) (size + 1), newOption);
            logger.info("added option \"" + newOpt.getValue() + "\"");
            return true;
        }
    }

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
     * @param field_id the <em>key</em> of the <em>customfield</em> from GET parameter
     * @param proj_id the <em>key</em> of the <em>project</em> from GET parameter
     * @param new_opt  string - the <em>new option</em> from GET parameter
     * @return response in XML format
     *************************************************************************/
    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getMessage(@QueryParam("field_id") String field_id
            , @QueryParam("proj_id") String proj_id
            , @QueryParam("new_opt") String new_opt) {
        Logger logger = settingLogger();
        logger.info("starting getMessage method...");
        MutableOptionsList mol = new MutableOptionsList(field_id, proj_id, logger);
        Response response;
        if (mol.addNew(new_opt, logger)) {
            response = Response.ok(new CfOptChangeModel(mol.field.getName()
                    , mol.project.getName(), mol.fieldConfSch.getName()
                    , mol.fieldConf.getName(), mol.getOptionsString(), mol.fieldKey
                    , mol.projectKey)).build();
        } else {
            logger.warning("returning null response, cuz something went wrong on previous stages");
            response = Response.ok(new CfOptChangeModel(null, null, null, null
                    , null, null, null)).build();
        }
        logger.info("constructed response, returning...");
        LogManager.getLogManager().reset();
        return response;
    }
}
