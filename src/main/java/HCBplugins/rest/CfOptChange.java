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

        private String getOptionsString() {
            StringBuilder optStr = new StringBuilder("{");
            Options fieldOptions = optMgr.getOptions(fieldConf);
            for (Option option : fieldOptions) {
                optStr.append("\"").append(option.getValue()).append("\", ");
            }
            optStr.setLength(optStr.length() - 2);
            return optStr.append("}").toString();
        }

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


    private String[] setOpt(String field_id, String proj_id
            , String new_opt, Logger logger) {
        logger.info("starting getOptStrFromId method...");
        String[] contextAndOptions = new String[5];
        FieldManager fMgr = ComponentAccessor.getFieldManager();
        logger.info("field manager acquired - " + fMgr);
        ConfigurableField field = fMgr.getConfigurableField(field_id);
        logger.info("field acquired - " + field);
        contextAndOptions[0] = field.getName();
        logger.info("field name is " + field.getName());

        ProjectManager pMgr = ComponentAccessor.getProjectManager();
        Project project = pMgr.getProjectByCurrentKeyIgnoreCase(proj_id);
        logger.info("project manager acquired - " + pMgr);
        contextAndOptions[1] = project.getName();
        logger.info("project name is " + project.getName());

        FieldConfigSchemeManager fcsMgr = ComponentAccessor.getFieldConfigSchemeManager();
        FieldConfigScheme fieldConfSh = fcsMgr.getRelevantConfigScheme(project, field);
        contextAndOptions[2] = fieldConfSh.getName();
        logger.info("field configuration scheme name is " + fieldConfSh.getName());

        FieldConfig config = fieldConfSh.getOneAndOnlyConfig();
        contextAndOptions[3] = config.getName();
        logger.info("field configuration name is " + config.getName());

        OptionsManager optMgr = ComponentAccessor.getOptionsManager();
        Options cfOptions = optMgr.getOptions(config);

        boolean optExist = false;
        if (new_opt != null) {
            for (Option option : cfOptions) {
                if (option.getValue().equals(new_opt)) {
                    optExist = true;
                    logger.warning("new option \"" + new_opt + "\" already exist");
                    break;
                }
            }
        }
        int size = cfOptions.getRootOptions().size();
        logger.info("there are " + size + "options in list now");
        if (!optExist) {
            logger.info("creating new option \"" + new_opt + "\"");
            Option newOpt = optMgr.createOption(config, null, (long) (size + 1), new_opt);
            logger.info("added option \"" + newOpt.getValue() + "\"");
            cfOptions = optMgr.getOptions(config);
            size = cfOptions.getRootOptions().size();
            logger.info("there are " + size + "options in list now");
        }
        StringBuilder optStr = new StringBuilder("{");
        for (Option option : cfOptions) {
            optStr.append("\"").append(option.getValue()).append("\", ");
        }
        optStr.setLength(optStr.length() - 2);
        contextAndOptions[4] = optStr.append("}").toString();
        logger.info("returning contextAndOptions array");
        return contextAndOptions;
    }
}
