package ru.homecredit.rest;

import ru.homecredit.Constants;
import ru.homecredit.DTO.FieldParameters;
import ru.homecredit.DTO.RequestParameters;
import ru.homecredit.DTO.FieldOptions;
import com.atlassian.jira.issue.context.IssueContextImpl;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.ConfigurableField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * class for manging customfield options trough Jira Java API by handling the
 * data, received from controller, and returning to controlletr the transport
 * FieldOption transport object
 */
public class FieldOptionsService {
    private static final Logger logger = LoggerFactory.getLogger(FieldOptionsService.class);
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final OptionsManager optionsManager;
    private final PluginSettingsService pluginSettingsService;

    /**
     * constructor just receives and saves in fields the instances of Jira beans,
     * received fron controller
     * @param fieldManager - customfield manager
     * @param projectManager - project manager
     * @param optionsManager - field options manager
     * @param pluginSettingsFactory - factory to acquire plugin settings service
     */
    public FieldOptionsService(FieldManager fieldManager,
                               ProjectManager projectManager,
                               OptionsManager optionsManager,
                               PluginSettingsFactory pluginSettingsFactory) {
        logger.info("starting FieldOptionsService constructor");
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.optionsManager = optionsManager;
        this.pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);

        logger.info("constructed FieldOptionsService instance");
    }

    /**
     * method for appending new option to customfield
     * @param requestBody json string with parameters from POST request body
     * @return FieldOptions transport object
     */


    public FieldOptions postOption(String requestBody) {
        logger.info("starting postOption method with initializing FieldOptions");
        FieldOptions fieldOptions = initializeFieldOptions(requestBody);
        logger.info("getting fieldParameters from FieldOptions");
        if (fieldOptions == null) {
            logger.error("shutting down postOption cuz fieldOptions object wasn't constructed");
            return null;
        }
        if (!fieldOptions.getFieldParameters().isPermittedToEdit()) {
            logger.warn("shutting down postOption cuz field is not permitted for edit");
            return fieldOptions;
        }
        if (!fieldOptions.getFieldParameters().isValidContext()) {
            logger.error("shutting down postOption cuz invalid FieldContext");
            return fieldOptions;
        }
        String action = fieldOptions.getRequestParameters().getAction();
        switch (action) {
            case (Constants.DEFAULT_RECEIVED): {
                logger.error("shutting down postOption cuz action parameter not provided");
                return fieldOptions;
            }
            case (Constants.ADD): {
                logger.info("trying to add new Option");
                String newOptionValue = fieldOptions.getRequestParameters().getNewOption();
                if (newOptionValue.equals(Constants.DEFAULT_RECEIVED)) {
                    logger.error("shutting down postOption cuz newOption not provided");
                    return fieldOptions;
                }
                logger.info("trying to add new option \"{}\"", newOptionValue);
                if (Arrays.asList(fieldOptions.getFieldOptionsArr()).contains(newOptionValue)) {
                    logger.warn("new option \"{}\" already exist", newOptionValue);
                    return fieldOptions;
                }
                int size = fieldOptions.getFieldOptionsArr().length;
                optionsManager.createOption(fieldOptions.getFieldParameters().getFieldConfig(),
                                            null,
                                            (long) (size + 1),
                                            newOptionValue);
                fieldOptions.setOptionAdded(true);
                logger.info("added option \"{}\" to Options", newOptionValue);
                /* acquiring Options object and Options from it once again, cuz the
                new one was appended */
                initializeOptions(fieldOptions);
                return fieldOptions;
            }
            case (Constants.DISABLE): {
                String optionValue = fieldOptions.getRequestParameters().getNewOption();
                logger.info("trying to disable option \"{}\"", optionValue);
                Options options = optionsManager.getOptions(
                        fieldOptions.getFieldParameters().getFieldConfig()
                );
                options.getOptionForValue(optionValue, null).setDisabled(true);
                logger.info("disabled option \"{}\"", optionValue);
                return fieldOptions;
            }
            case (Constants.ENABLE): {
                String optionValue = fieldOptions.getRequestParameters().getNewOption();
                logger.info("trying to enable option \"{}\"", optionValue);
                Options options = optionsManager.getOptions(
                        fieldOptions.getFieldParameters().getFieldConfig()
                );
                if (options.getOptionForValue(optionValue, null) != null) {
                    options.getOptionForValue(optionValue, null).setDisabled(false);
                    logger.info("enabled option \"{}\"", optionValue);
                } else {
                    logger.error("option {} seems not to exist. shutting down", optionValue);
                }
                return fieldOptions;
            }
        }
        return fieldOptions;
    }


    /**
     * method to acquire the options of given field in given context
     * @param fieldKey - jira field key
     * @param projectKey - jira project key
     * @param issueTypeId - jira issue type id
     * @return - FieldOptions transport object
     */
    public FieldOptions initializeFieldOptions(String fieldKey,
                                               String projectKey,
                                               String issueTypeId) {
        logger.info("starting initializeFieldOptions" +
                            "(String fieldKey, String projectKey, String issueTypeId) method");
        RequestParameters requestParameters =
                new RequestParameters(fieldKey, projectKey, issueTypeId);
        FieldParameters fieldParameters = initializeFieldParameters(requestParameters);
        if (fieldParameters == null) {
            logger.error("shutting down initializeFieldOptions method cuz" + "" +
                                 "fieldParameters==null");
            return null;
        }
        FieldOptions fieldOptions = new FieldOptions();
        fieldOptions.setRequestParameters(requestParameters);
        fieldOptions.setFieldParameters(fieldParameters);
        initializeOptions(fieldOptions);
        return fieldOptions;
    }

    /**
     * same method but for POST requests, which parameters are received in
     * @param requestBody - json string of POST request parameters
     * @return FieldOptions transport object
     */
    public FieldOptions initializeFieldOptions(String requestBody) {
        logger.info("starting initializeFieldOptions(String requestBody) method");
        RequestParameters requestParameters = extractRequestParameters(requestBody);
        FieldParameters fieldParameters = initializeFieldParameters(requestParameters);
        if (requestParameters == null || fieldParameters == null) {
            logger.error("shutting down initializeFieldOptions method cuz" + "" +
                 "either requestParameters==null or fieldParameters==null");
            return null;
        }
        FieldOptions fieldOptions = new FieldOptions();
        fieldOptions.setRequestParameters(requestParameters);
        fieldOptions.setFieldParameters(fieldParameters);
        initializeOptions(fieldOptions);
        return fieldOptions;
    }

    private RequestParameters extractRequestParameters(String requestBody) {
        logger.info("starting extractRequestParameters(String requestBody) method");
        RequestParameters requestParameters = null;
        try {
            JSONObject requestJSON = new JSONObject(requestBody);
            requestParameters =
                    new RequestParameters(requestJSON.getString("fieldKey"),
                                          requestJSON.getString("projectKey"),
                                          requestJSON.getString("issueTypeId"),
                                          requestJSON.getString("newOption"),
                                          requestJSON.getString("action"));
        } catch (JSONException e) {
            logger.error("could not parse request body - {}", requestBody);
            logger.error("exception is - {}", e.getMessage());
        }
        return requestParameters;
    }

    /**
     *
     */
    private FieldParameters initializeFieldParameters(RequestParameters requestParameters) {
        logger.info("starting initializeFieldParameters method");
        FieldParameters fieldParameters = new FieldParameters();
        try {
            ConfigurableField field = fieldManager.
                 getConfigurableField(requestParameters.getFieldKey());
            logger.info("field {} acquired as {}", requestParameters.getFieldKey(),
                        fieldParameters.getFieldName());
            fieldParameters.setFieldName(field.getName());
            Project project = projectManager.
                  getProjectByCurrentKeyIgnoreCase(requestParameters.getProjectKey());
            logger.info("project {} acquired as {}",
                        requestParameters.getProjectKey(), project.getName());
            fieldParameters.setProjectName(project.getName());
            IssueContextImpl issueContext = new IssueContextImpl(project.getId(),
                   requestParameters.getIssueTypeId());
            logger.info("issue context acquired as " + issueContext.toString());
            FieldConfig fieldConfig = field.getRelevantConfig(issueContext);
            logger.info("field configuration acquired as {}", fieldConfig.getName());
            fieldParameters.setFieldConfig(fieldConfig);
            fieldParameters.setFieldConfigName(fieldConfig.getName());
            fieldParameters.setValidContext(true);
            boolean permittedToEdit = pluginSettingsService.getSettings().getEditableFields().
                    contains(requestParameters.getFieldKey());
            logger.info("field permitted to edit in plugin settings {}", permittedToEdit);
            fieldParameters.setPermittedToEdit(permittedToEdit);
        } catch (Exception e) {
            logger.error("failed to initialize field parameters with error {}",
                         e.getMessage());
            return null;
        }
        return fieldParameters;
    }
    /*
    private FieldParameters initializeFieldParameters(RequestParameters requestParameters) {
        logger.info("starting initializeFieldParameters method");
        FieldParameters fieldParameters = new FieldParameters();
        ConfigurableField field = Objects.requireNonNull(fieldManager.
               getConfigurableField(requestParameters.getFieldKey()),
                    "failed to acquire field " + requestParameters.getFieldKey());
        logger.info("field {} acquired as {}", requestParameters.getFieldKey(),
                    fieldParameters.getFieldName());
        fieldParameters.setFieldName(field.getName());
        Project project =
                Objects.requireNonNull(projectManager.
                       getProjectByCurrentKeyIgnoreCase(requestParameters.getProjectKey()),
                       "failed to acquire project " + requestParameters.getProjectKey());
        logger.info("project {} acquired as {}",
                    requestParameters.getProjectKey(), project.getName());
        fieldParameters.setProjectName(project.getName());
        IssueContextImpl issueContext = new IssueContextImpl(project.getId(),
                              requestParameters.getIssueTypeId());
        logger.info("issue context acquired as " + issueContext.toString());
        FieldConfig fieldConfig = Objects.requireNonNull(field.
              getRelevantConfig(issueContext), "failed to acquire FieldConfig from context");
        logger.info("field configuration acquired as {}", fieldConfig.getName());
        fieldParameters.setFieldConfig(fieldConfig);
        fieldParameters.setFieldConfigName(fieldConfig.getName());
        fieldParameters.setValidContext(true);
        boolean permittedToEdit = pluginSettingsService.getSettings().getEditableFields().
                contains(requestParameters.getFieldKey());
        logger.info("field permitted to edit in plugin settings {}", permittedToEdit);
        fieldParameters.setPermittedToEdit(permittedToEdit);
        return fieldParameters;
    }
    /*

    /**
     * method to initialize options of field, attributes of which are stored in
     * @param fieldOptions - transport object
     */
    private void initializeOptions(FieldOptions fieldOptions) {
        logger.info("starting initializeOptions method");
        Options options = Objects.requireNonNull(optionsManager.
              getOptions(fieldOptions.getFieldParameters().getFieldConfig()),
              "failed to acquire Options object");
        // acquiring string representation
        fieldOptions.setFieldOptionsString(options.toString());
        // and array representation
        // fieldOptions.setFieldOptionsArr(getStringArrayFromOptionsObject(options));
         fieldOptions.setFieldOptionsArr(options.stream().map(op -> op.getValue()).toArray(String[]::new));
        logger.info("field options are {}", fieldOptions.getFieldOptionsString());
    }


    /**
     * helper method to convert Options object to String array
     * @param fieldOptions field options object
     * @return array of String with field options values
     */
    private String[] getStringArrayFromOptionsObject(Options fieldOptions) {
        String[] fieldOptionsArr = new String[fieldOptions.size()];
        int i = 0;
        for (Option option : fieldOptions) {
            fieldOptionsArr[i++] = option.getValue();
        }
        return fieldOptionsArr;
    }
}
