package HCBplugins.rest;

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
    public String fieldKey = "not provided";
    public String projectKey = "not provided";
    public String newOption = "not provided";
    public String fieldName = "not provided";
    public String projectName = "not provided";
    public String fieldConfigName = "failed to acquire";
    public String fieldOptionsString = "failed to acquire";

    /**********************************************************************
     * constructor just puts the field_key, project_key and new_option
     * GET parameters to appropriate attributes
     * @param fieldKey the key of the <em>customfield</em> from GET parameter
     * @param projectKey the key of the <em>project</em> from GET parameter
     * @param newOption new option content from GET parameter
     * @param logger just the logger, initialized before invoking the
     *               constructor to keep track of what's going on
     *********************************************************************/
    MutableOptionsList(String fieldKey, String projectKey, String newOption
            , Logger logger) {
        logger.info("starting constructor... field and project keys " +
                "received are " + fieldKey + "; " + projectKey +
                "; new option received is " + newOption);
        if (fieldKey != null) {
            this.fieldKey = fieldKey;
        }
        if (projectKey != null) {
            this.projectKey = projectKey;
        }
        if (newOption != null) {
            this.newOption = newOption;
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
    public void addNew(Logger logger
            , FieldManager fieldManager
            , ProjectManager projectManager
            , FieldConfigSchemeManager fieldConfigSchemeManager
            , OptionsManager optionsManager) {
        logger.info("starting addNew method... ");
        // what this raw use of parametrized class is bad for?
        ConfigurableField field = fieldManager.getConfigurableField(fieldKey);
        logger.info("field " + fieldKey + " acquired as " + field);
        Project project = projectManager.getProjectByCurrentKeyIgnoreCase(projectKey);
        logger.info("project " + projectKey + " acquired as " + project);
        if ((field != null) && (project != null) && !newOption.equals("failed to acquire")) {
            fieldName = field.getName();
            projectName = project.getName();
        } else {
            logger.log(Level.WARNING, "failed to add new option");
            return;
        }
        // could s less check for null further with one try-catch block
        FieldConfigScheme fieldConfigScheme = fieldConfigSchemeManager.getRelevantConfigScheme(project, field);
        if (fieldConfigScheme != null) {
            logger.info("field Configuration Schema acquired as " + fieldConfigScheme);
        } else {
            logger.log(Level.WARNING
                    , "FieldConfigSchemeManager fails to get Field Config Schema");
            return;
        }
        /* potential problem here. field can have more than one and only
        configuration in in a given project.
        it's necessary to implement the issue type dependency
        */
        FieldConfig fieldConfig = fieldConfigScheme.getOneAndOnlyConfig();
        if (fieldConfig != null) {
            fieldConfigName = fieldConfig.getName();
            logger.info("field configuration acquired as " + fieldConfigName);
        } else {
            logger.log(Level.WARNING, "failed to acquire FieldConfig");
            return;
        }
        Options fieldOptions = optionsManager.getOptions(fieldConfig);
        if (fieldOptions != null) {
            logger.info("field options acquired");
            fieldOptionsString = getOptionsString(fieldOptions);
            logger.info("field options are " + fieldOptionsString);
        } else {
            logger.log(Level.WARNING, "failed to acquire field options");
            return;
        }
        logger.info("trying to add new option \"" + newOption + "\"");
        appendOptionToOptions(fieldConfig, optionsManager, fieldOptions, newOption, logger);
        fieldOptionsString = getOptionsString(optionsManager.getOptions(fieldConfig));
    }

    /*********************************************************************
     * method returns the list of options of customfield in a String representation
     * @param fieldOptions Options Jira Object
     * @return <em>{"option_1", ... , "option_n"}</em> the string of options
     * of invoking the method field instance
     ********************************************************************/
    private String getOptionsString(Options fieldOptions) {
        StringBuilder optionsString = new StringBuilder("{");
        for (Option option : fieldOptions) {
            optionsString.append("\"").append(option.getValue()).append("\", ");
        }
        optionsString.setLength(optionsString.length() - 2);
        return optionsString.append("}").toString();
    }

    /**********************************************************************
     * @param fieldConfig FieldConfig Jira Object
     * @param optMgr OptionsManager Jira Object
     * @param fieldOptions Options Jira Object
     * @param newOption string - the <em>new option</em> from GET parameter
     * @param logger just the logger, initialized before invoking the
     *          constructor to keep track of what's going on
     *********************************************************************/
    private void appendOptionToOptions(FieldConfig fieldConfig, OptionsManager optMgr
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
        Option newOpt = optMgr.createOption(fieldConfig, null, (long) (size + 1), newOption);
        logger.info("added option \"" + newOpt.getValue() + "\"");
    }
}
