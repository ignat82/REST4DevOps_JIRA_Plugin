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

public class FieldOptionsService {
    private static final Logger                   logger =
            LoggerFactory.getLogger(FieldOptionsService.class);
    private final        FieldManager             fieldManager;
    private final        ProjectManager           projectManager;
    private final        FieldConfigSchemeManager fieldConfigSchemeManager;
    private final OptionsManager        optionsManager;
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

    public FieldOptions initializeMoo(String fieldKey, String projectKey) {
        logger.info("starting initializeMoo (String fieldKey, String projectKey) method");
        FieldOptions moo = new FieldOptions(fieldKey, projectKey, "");
        try {
            initializeField(moo);
            initializeOptions(moo);
        } catch (Exception exception) {
            logger.warn("got exception when initializing MutableOptionObject: {}",
                        exception.getMessage());
            logger.warn("shutting down initializeMoo method");
            return moo;
        }
        return moo;
    }

    public FieldOptions initializeMoo(String requestBody) {
        logger.info("starting initializeMoo (String requestBody) method");
        FieldOptions moo;
        try {
            JSONObject requestJSON = new JSONObject(requestBody);
            moo = new FieldOptions(requestJSON.getString("fieldKey"),
                                   requestJSON.getString("projectKey"),
                                   requestJSON.getString("newOption"));
        } catch (JSONException jsonException) {
            logger.error("caught {} when parsing parameters from requestBody",
                         jsonException.getMessage());
            moo = new FieldOptions();
        }
        try {
            initializeField(moo);
            initializeOptions(moo);
        } catch (Exception exception) {
            logger.warn("got exception when initializing MutableOptionObject: {}",
                        exception.getMessage());
            logger.warn("shutting down initializeMoo method");
            return moo;
        }
        return moo;
    }

    public FieldOptions addNewOption(String requestBody) {
        logger.info("starting addNewOption method");
        FieldOptions moo = initializeMoo(requestBody);
        if (!pluginSettingsService.getSettings().getEditableFields().contains(moo.getFieldKey())) {
            logger.warn("field {} is not permitted for edit by plugin settings. " +
                                "shutting down", moo.getFieldKey());
            return moo;
        }
        if (!moo.isValidContext()) {
            logger.error("shutting down addNewOption cuz FieldOptions " +
                                 "has invalid FieldContext in it");
            return moo;
        }
        if (!moo.getNewOption().equals("not provided")) {
            logger.info("trying to add new option \"{}\"", moo.getNewOption());
            if (Arrays.asList(moo.getFieldOptionsArr()).contains(moo.getNewOption())) {
                logger.warn("new option \"{}\" already exist", moo.getNewOption());
                return moo;
            }
            int size = moo.getFieldOptionsArr().length;
            optionsManager.createOption(moo.getFieldConfig(),
                                        null,
                                        (long) (size + 1),
                                        moo.getNewOption());
            moo.setOptionAdded(true);
            logger.info("added option \"{}\" to Options", moo.getNewOption());
            /* acquiring Options object and Options from it once again, cuz the
            new one was appended */
            initializeOptions(moo);
        } else {
            logger.warn("failed to add new option due its not provided in REST request");
            logger.warn("shutting down addNewOption method");
        }
        return moo;
    }

    private void initializeField(FieldOptions moo) {
        logger.info("starting initializeField method");
        // initializing field
        ConfigurableField field = Objects.requireNonNull(fieldManager.
              getConfigurableField(moo.getFieldKey()),
              "failed to acquire field " + moo.getFieldKey());
        moo.setFieldName(field.getName());
        logger.info("field {} acquired as {}", moo.getFieldKey(), moo.getFieldName());
        // initializing project
        Project project = Objects.requireNonNull(projectManager.
                    getProjectByCurrentKeyIgnoreCase(moo.getProjectKey()),
                    "failed to acquire project " + moo.getProjectKey());
        moo.setProjectName(project.getName());
        logger.info("project {} acquired as {}",
                    moo.getProjectKey(), moo.getProjectName());
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
        moo.setFieldConfig(fieldConfig);
        moo.setFieldConfigName(fieldConfig.getName());
        moo.setValidContext(true);
        logger.info("field configuration acquired as {}", moo.getFieldConfigName());
    }

    private void initializeOptions(FieldOptions moo) {
        logger.info("starting initializeOptions method");
        Options options = Objects.
                requireNonNull(optionsManager.getOptions(moo.getFieldConfig()),
                               "failed to acquire Options object");
        // acquiring string representation
        moo.setFieldOptionsString(options.toString());
        // and array representation
        moo.setFieldOptionsArr(getStringArrayFromOptionsObject(options));
        logger.info("field options are {}", moo.getFieldOptionsString());
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
