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
        Response response;
        if (field_id != null && proj_id != null) {
            logger.info("field_id = " + field_id + "; proj_id = " + proj_id + ";");
            response = Response.ok(new CfOptChangeModel(field_id, proj_id
                    , getContSetOpt(field_id, proj_id, new_opt, logger))).build();
        } else {
            logger.info("field_id || proj_id are null");
            //closeHandlers(logger);
            response = Response.ok(new CfOptChangeModel(field_id, proj_id
                    , null)).build();
        }
        logger.info("constructed response, returning...");
        LogManager.getLogManager().reset();
        return response;
    }


    private String[] getContSetOpt(String field_id, String proj_id
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
