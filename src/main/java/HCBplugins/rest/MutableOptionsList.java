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

import java.util.logging.Level;
import java.util.logging.Logger;

/***************************************************************************
 * Class that represents Jira entity "Options list" which by Jira logic
 * exists only in specific "context", another Jira entity, defined by
 * specific combination of "field", "project" and "issue type"
 * dependency of issue type is not implemented yet
 **************************************************************************/
public class MutableOptionsList {
    public String fKey = "not provided";
    public String pKey = "not provided";
    public String newOpt = "not provided";
    public String fName = "not provided";
    public String pName = "not provided";
    public String fConfName = "failed to acquire";
    public String optList = "failed to acquire";

    /**********************************************************************
     * constructor just puts the field_key, project_key and new_option
     * GET parameters to appropriate attributes
     * @param field_key the key of the <em>customfield</em> from GET parameter
     * @param proj_key the key of the <em>project</em> from GET parameter
     * @param new_opt new option content from GET parameter
     * @param logger just the logger, initialized before invoking the
     *               constructor to keep track of what's going on
     *********************************************************************/
    MutableOptionsList(String field_key, String proj_key, String new_opt
            , Logger logger) {
        logger.info("starting constructor... field and project keys " +
                "received are " + field_key + "; " + proj_key +
                "; new option received is " + new_opt);
        if (field_key != null) {
            fKey = field_key;
        }
        if (proj_key != null) {
            pKey = proj_key;
        }
        if (new_opt != null) {
            newOpt = new_opt;
        }
    }

    /*************************************************************************
     * core method, which acquires all the necessary Jira objects, fills the
     * class attributes with values, received from these Jira objects methods
     * and calls two another methods for getting the string representation
     * of options list and putting new value in it
     * @param logger  just the logger, initialized before invoking the
     *  constructor to keep track of what's going on
     ************************************************************************/
    public void addNew(Logger logger) {
        logger.info("starting addNew method... ");
        FieldManager fMgr = ComponentAccessor.getFieldManager();
        logger.info("FieldManager acquired - " + fMgr);
        ConfigurableField field = fMgr.getConfigurableField(fKey);
        if (field != null) {
            fName = field.getName();
            logger.info("field " + fKey + " acquired as " + fName);
        } else {
            logger.log(Level.WARNING, "failed to acquire field " + fKey);
        }
        ProjectManager pMgr = ComponentAccessor.getProjectManager();
        logger.info("ProjectManager acquired - " + pMgr);
        Project project = pMgr.getProjectByCurrentKeyIgnoreCase(pKey);
        if (project != null) {
            pName = project.getName();
            logger.info("project " + pKey + " acquired as " + pName);
        } else {
            logger.log(Level.WARNING, "failed to acquire  project " + pKey);
        }
        if (field == null || project == null || newOpt.equals("failed to acquire")) {
            logger.log(Level.WARNING, "failed to add new option");
            return;
        }
        FieldConfigSchemeManager fcsMgr = ComponentAccessor.getFieldConfigSchemeManager();
        if (fcsMgr != null) {
            logger.info("FieldConfigSchemeManager acquired - " + fcsMgr);
        } else {
            logger.log(Level.WARNING
                    , "failed to acquire FieldConfigSchemeManager");
            return;
        }
        FieldConfigScheme fieldConfSch = fcsMgr.getRelevantConfigScheme(project, field);
        if (fieldConfSch != null) {
            logger.info("field Configuration Schema acquired as " + fieldConfSch);
        } else {
            logger.log(Level.WARNING
                    , "FieldConfigSchemeManager fails to get Field Config Schema");
            return;
        }
            /* potential problem here. field can have more than one and only
            configuration in in a given project.
            it's necessary to implement the issue type dependency
            */
        FieldConfig fieldConf = fieldConfSch.getOneAndOnlyConfig();
        if (fieldConf != null) {
            fConfName = fieldConf.getName();
            logger.info("field configuration acquired as " + fConfName);
        } else {
            logger.log(Level.WARNING, "failed to acquire FieldConfig");
            return;
        }
        OptionsManager optMgr = ComponentAccessor.getOptionsManager();
        if (optMgr != null) {
            logger.info("OptionsManager acquired - " + optMgr);
        } else {
            logger.log(Level.WARNING, "failed to acquire OptionsManager");
            return;
        }
        Options fieldOptions = optMgr.getOptions(fieldConf);
        if (fieldOptions != null) {
            logger.info("field options acquired");
            optList = getOptionsString(fieldOptions);
            logger.info("field options are " + optList);
        } else {
            logger.log(Level.WARNING, "failed to acquire field options");
            return;
        }
        logger.info("trying to add new option \"" + newOpt + "\"");
        appendOptionToOptions(fieldConf, optMgr, fieldOptions, newOpt, logger);
        optList = getOptionsString(optMgr.getOptions(fieldConf));
    }

    /*********************************************************************
     * method returns the list of options of customfield in a String representation
     * @param fieldOptions Options Jira Object
     * @return <em>{"option_1", ... , "option_n"}</em> the string of options
     * of invoking the method field instance
     ********************************************************************/
    private String getOptionsString(Options fieldOptions) {
        StringBuilder optStr = new StringBuilder("{");
        for (Option option : fieldOptions) {
            optStr.append("\"").append(option.getValue()).append("\", ");
        }
        optStr.setLength(optStr.length() - 2);
        return optStr.append("}").toString();
    }

    /**********************************************************************
     * @param fieldConf FieldConfig Jira Object
     * @param optMgr OptionsManager Jira Object
     * @param fieldOptions Options Jira Object
     * @param newOption string - the <em>new option</em> from GET parameter
     * @param logger just the logger, initialized before invoking the
     *          constructor to keep track of what's going on
     *********************************************************************/
    private void appendOptionToOptions(FieldConfig fieldConf, OptionsManager optMgr
            , Options fieldOptions, String newOption, Logger logger) {
        logger.info("checking if option already exists");
        for (Option option : fieldOptions) {
            if (option.getValue().equals(newOption)) {
                logger.warning("new option \"" + newOption + "\" already exist");
                return;
            }
        }
        logger.info("... it doesn't");
        int size = fieldOptions.getRootOptions().size();
        logger.info("there are " + size + "options in list now");
        logger.info("creating new option \"" + newOption + "\"");
        Option newOpt = optMgr.createOption(fieldConf, null, (long) (size + 1), newOption);
        logger.info("added option \"" + newOpt.getValue() + "\"");
    }
}
