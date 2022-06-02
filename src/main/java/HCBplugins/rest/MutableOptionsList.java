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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;


/***************************************************************************
 * Class that represents Jira entity "Options list" which by Jira logic
 * exists only in specific "context", another Jira entity, defined by
 * specific combination of "field", "project" and "issue type"
 * dependency of issue type is not implemented yet
 **************************************************************************/
public class MutableOptionsList {
    private final Logger logger;
    private final String fieldKey;
    private final String projectKey;
    private String newOption;
    private String fieldName = "failed to acquire";
    private String projectName = "failed to acquire";
    private String fieldConfigName = "failed to acquire";
    private String fieldOptionsString = "failed to acquire";
    private String[] fieldOptionsArr;
    private boolean result = false;

    /**********************************************************************
     * constructor just puts the field_key, project_key and new_option
     * GET parameters to appropriate attributes
     * @param fieldKey the key of the <em>customfield</em> from GET parameter
     * @param projectKey the key of the <em>project</em> from GET parameter
     * @param newOption new option content from GET parameter
     *********************************************************************/
    MutableOptionsList(String fieldKey, String projectKey, String newOption) {
        logger = LoggerFactory.getLogger(MutableOptionsList.class);
        logger.info("starting constructor... field and project keys " + "received are " + fieldKey
                + "; " + projectKey + "; new option received is " + newOption);
        this.fieldKey = fieldKey;
        this.projectKey = projectKey;
        this.newOption = newOption;
    }


    /*************************************************************************
     * core method, which acquires all the necessary Jira objects, fills the
     * class attributes with values, received from these Jira objects methods
     * and calls two another methods for getting the string representation
     * of options list and putting new value in it
     * @param fieldManager jira object to manipulate fields
     * @param projectManager jira object to manipulate projects
     * @param fieldConfigSchemeManager jira object to manipulate configuration
     *                                 schemas
     * @param optionsManager jira object to manipulate field options
     ************************************************************************/
    public void addNewOption(FieldManager fieldManager,
                             ProjectManager projectManager,
                             FieldConfigSchemeManager fieldConfigSchemeManager,
                             OptionsManager optionsManager) {
        // what this raw use of parametrized class is bad for?
        ConfigurableField field;
        Project project;
        FieldConfigScheme fieldConfigScheme;
        FieldConfig fieldConfig;
        Options fieldOptions;

        logger.info("starting addNewOption method... ");
        try {
            field = Objects.requireNonNull(fieldManager.getConfigurableField(fieldKey)
                    , "failed to acquire field " + fieldKey);
            fieldName = field.getName();
            logger.info("field " + fieldKey + " acquired as " + field);
            project = Objects.requireNonNull(projectManager.getProjectByCurrentKeyIgnoreCase(projectKey)
                    , "failed to acquire project " + projectKey);
            projectName = project.getName();
            logger.info("project " + projectKey + " acquired as " + project);
            fieldConfigScheme = Objects.requireNonNull(fieldConfigSchemeManager.getRelevantConfigScheme(project, field)
                    , "FieldConfigSchemeManager fails to get Field Config Schema");
            logger.info("field Configuration Schema acquired as " + fieldConfigScheme);
            /*
            potential problem here. field can have more than one and only
            configuration in in a given project.
            it's necessary to implement the issue type dependency
            */
            fieldConfig = Objects.requireNonNull(fieldConfigScheme.getOneAndOnlyConfig()
                    , "failed to acquire FieldConfig");
            fieldConfigName = fieldConfig.getName();
            logger.info("field configuration acquired as " + fieldConfigName);
            fieldOptions = Objects.requireNonNull(optionsManager.getOptions(fieldConfig)
                    , "failed to acquire field options");
            logger.info("field options acquired");
            fieldOptionsString = getOptionsString(fieldOptions);
            logger.info("field options are " + fieldOptionsString);
            fieldOptionsArr = getOptionsArr(fieldOptions);
        } catch (Exception exception) {
            logger.warn(exception.getMessage());
            logger.warn("shutting down addNewOption method");
            return;
        }

        if ((newOption != null) && (!newOption.equals(""))) {
            logger.info("trying to add new option \"" + newOption + "\"");
            appendOptionToOptions(fieldConfig, optionsManager, fieldOptions, newOption);
            /* acquiring Options object and Options from it once again, cuz the
            new one was appended */
            fieldOptions = (optionsManager.getOptions(fieldConfig));
            fieldOptionsString = getOptionsString(optionsManager.getOptions(fieldConfig));
            fieldOptionsArr = getOptionsArr(fieldOptions);
        } else {
            newOption = "not provided";
            logger.warn("failed to add new option due its not provided in GET request");
            logger.warn("shutting down addNewOption method");
        }
    }

    /**
     method returns the list of options from Options object in a single
     comma separated String representation
     @param fieldOptions Options Jira Object
     @return <em>{"option_1", ... , "option_n"}</em> the string of options
     of invoking the method field instance
     */
    private String getOptionsString(Options fieldOptions) {
        StringBuilder optionsString = new StringBuilder("{");
        for (Option option : fieldOptions) {
            optionsString.append("\"").append(option.getValue()).append("\", ");
        }
        optionsString.setLength(optionsString.length() - 2);
        return optionsString.append("}").toString();
    }

    /**
     method returns the array of String with options, containing in fieldOptions
     parameter
     @param fieldOptions Options Jira Object
     @return <em>["option_1", ... , "option_n"]</em> array of Strings
     */
    private String[] getOptionsArr(Options fieldOptions) {
        String[] fieldOptionsArr = new String[fieldOptions.size()];
        int i = 0;
        for (Option option : fieldOptions) {
            fieldOptionsArr[i++] = option.getValue();
        }
        return fieldOptionsArr;
    }

    /**********************************************************************
     * @param fieldConfig FieldConfig Jira Object
     * @param optMgr OptionsManager Jira Object
     * @param fieldOptions Options Jira Object
     * @param newOption string - the <em>new option</em> from GET parameter
     *********************************************************************/
    private void appendOptionToOptions(FieldConfig fieldConfig, OptionsManager optMgr
            , Options fieldOptions, String newOption) {
        logger.info("checking if option already exists");
        if (Arrays.asList(fieldOptionsArr).contains(newOption)) {
            logger.warn("new option \"" + newOption + "\" already exist");
            return;
        }
        logger.info("... it doesn't");
        // getOptions or getRootOptions ?
        int size = fieldOptions.getRootOptions().size();
        logger.info("there are " + size + " options in list now");
        logger.info("creating new option \"" + newOption + "\"");
        Option newOpt = optMgr.createOption(fieldConfig, null, (long) (size + 1), newOption);
        logger.info("added option \"" + newOpt.getValue() + "\"");
        result = true;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getNewOption() {
        return newOption;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getFieldConfigName() {
        return fieldConfigName;
    }

    public String getFieldOptionsString() {
        return fieldOptionsString;
    }

    public Boolean getResult() {
        return result;
    }

    public String[] getFieldOptionsArr() {
        if (fieldOptionsArr == null) {
            logger.error("fieldOptionsArr appeared to be nul");
            return null;
        }
        return Arrays.copyOf(fieldOptionsArr, fieldOptionsArr.length);
    }
}
