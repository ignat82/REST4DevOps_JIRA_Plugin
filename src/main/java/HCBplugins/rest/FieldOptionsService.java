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
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

import static HCBplugins.rest.Constants.*;

public class FieldOptionsService {
    private static final Logger logger = LoggerFactory.getLogger(FieldOptionsService.class);
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final FieldConfigSchemeManager fieldConfigSchemeManager;
    private final OptionsManager optionsManager;
    private final PluginSettingsService pluginSettingsService;

    public FieldOptionsService(FieldManager fieldManager,
                               ProjectManager projectManager,
                               FieldConfigSchemeManager fieldConfigSchemeManager,
                               OptionsManager optionsManager,
                               PluginSettingsFactory pluginSettingsFactory) {
        logger.info("starting FieldOptionsService constructor");
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.fieldConfigSchemeManager = fieldConfigSchemeManager;
        this.optionsManager = optionsManager;
        this.pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);

        logger.info("constructed FieldOptionsService instance");
    }

    public FieldOptions initializeFieldOptions(String fieldKey, String projectKey) {
        logger.info("starting initializeFieldOptions" +
                            "(String fieldKey, String projectKey) method");
        FieldOptions fieldOptions = new FieldOptions(fieldKey, projectKey, "");
        try {
            initializeField(fieldOptions);
            initializeOptions(fieldOptions);
        } catch (Exception exception) {
            logger.warn("got exception when initializing MutableOptionObject: {}",
                        exception.getMessage());
            logger.warn("shutting down initializeFieldOptions method");
            return fieldOptions;
        }
        return fieldOptions;
    }

    public FieldOptions initializeFieldOptions(String requestBody) {
        logger.info("starting initializeFieldOptions (String requestBody) method");
        FieldOptions fieldOptions;
        try {
            JSONObject requestJSON = new JSONObject(requestBody);
            fieldOptions = new FieldOptions(requestJSON.getString("fieldKey"),
                                            requestJSON.getString("projectKey"),
                                            requestJSON.getString("newOption"));
        } catch (JSONException jsonException) {
            logger.error("caught {} when parsing parameters from requestBody",
                         jsonException.getMessage());
            fieldOptions = new FieldOptions();
        }
        try {
            initializeField(fieldOptions);
            initializeOptions(fieldOptions);
        } catch (Exception exception) {
            logger.warn("got exception when initializing MutableOptionObject: {}",
                        exception.getMessage());
            logger.warn("shutting down initializeFieldOptions method");
            return fieldOptions;
        }
        return fieldOptions;
    }

    public FieldOptions addNewOption(String requestBody) {
        logger.info("starting addNewOption method");
        FieldOptions fieldOptions = initializeFieldOptions(requestBody);
        if (!pluginSettingsService.getSettings().getEditableFields().
                contains(fieldOptions.getFieldKey())) {
            logger.warn("field {} is not permitted for edit by plugin settings. " +
                                "shutting down", fieldOptions.getFieldKey());
            return fieldOptions;
        }
        if (!fieldOptions.isValidContext()) {
            logger.error("shutting down addNewOption cuz FieldOptions " +
                                 "has invalid FieldContext in it");
            return fieldOptions;
        }
        if (!fieldOptions.getNewOption().equals(DEFAULT_RECEIVED)) {
            logger.info("trying to add new option \"{}\"", fieldOptions.getNewOption());
            if (Arrays.asList(fieldOptions.getFieldOptionsArr()).
                    contains(fieldOptions.getNewOption())) {
                logger.warn("new option \"{}\" already exist", fieldOptions.getNewOption());
                return fieldOptions;
            }
            int size = fieldOptions.getFieldOptionsArr().length;
            optionsManager.createOption(fieldOptions.getFieldConfig(),
                                        null,
                                        (long) (size + 1),
                                        fieldOptions.getNewOption());
            fieldOptions.setOptionAdded(true);
            logger.info("added option \"{}\" to Options", fieldOptions.getNewOption());
            /* acquiring Options object and Options from it once again, cuz the
            new one was appended */
            initializeOptions(fieldOptions);
        } else {
            logger.warn("failed to add new option due its not provided in REST request");
            logger.warn("shutting down addNewOption method");
        }
        return fieldOptions;
    }

    private void initializeField(FieldOptions fieldOptions) {
        logger.info("starting initializeField method");
        // initializing field
        ConfigurableField field = Objects.requireNonNull(fieldManager.
              getConfigurableField(fieldOptions.getFieldKey()),
              "failed to acquire field " + fieldOptions.getFieldKey());
        fieldOptions.setFieldName(field.getName());
        logger.info("field {} acquired as {}", fieldOptions.getFieldKey(),
                    fieldOptions.getFieldName());
        // initializing project
        Project project = Objects.requireNonNull(projectManager.
                    getProjectByCurrentKeyIgnoreCase(fieldOptions.getProjectKey()),
                    "failed to acquire project " + fieldOptions.getProjectKey());
        fieldOptions.setProjectName(project.getName());
        logger.info("project {} acquired as {}",
                    fieldOptions.getProjectKey(), fieldOptions.getProjectName());
        // initializing fieldConfigScheme from them
        FieldConfigScheme fieldConfigScheme = Objects.
                requireNonNull(fieldConfigSchemeManager.
                getRelevantConfigScheme(project, field),
                "FieldConfigSchemeManager fails to get FieldConfigScheme");
        logger.info("FieldConfigScheme acquired as {}", fieldConfigScheme);
        /******************************************************************
         potential problem here. field can have more than one and only
         configuration in in a given project.
         it's necessary to implement the issue type dependency
         ******************************************************************/
        // initializing fieldConfiguration from configurationScheme
        FieldConfig fieldConfig = Objects.requireNonNull(fieldConfigScheme.
                getOneAndOnlyConfig(), "failed to acquire FieldConfig");
        fieldOptions.setFieldConfig(fieldConfig);
        fieldOptions.setFieldConfigName(fieldConfig.getName());
        fieldOptions.setValidContext(true);
        logger.info("field configuration acquired as {}", fieldOptions.getFieldConfigName());
    }

    private void initializeOptions(FieldOptions fieldOptions) {
        logger.info("starting initializeOptions method");
        Options options = Objects.
                requireNonNull(optionsManager.getOptions(fieldOptions.getFieldConfig()),
                               "failed to acquire Options object");
        // acquiring string representation
        fieldOptions.setFieldOptionsString(options.toString());
        // and array representation
        fieldOptions.setFieldOptionsArr(getStringArrayFromOptionsObject(options));
        // fieldOptions.setFieldOptionsArr((String[]) options.toArray());
        logger.info("field options are {}", fieldOptions.getFieldOptionsString());
    }

    private String[] getStringArrayFromOptionsObject(Options fieldOptions) {
        String[] fieldOptionsArr = new String[fieldOptions.size()];
        int i = 0;
        for (Option option : fieldOptions) {
            fieldOptionsArr[i++] = option.getValue();
        }
        return fieldOptionsArr;
    }
}
