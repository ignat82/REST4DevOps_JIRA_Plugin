package ru.homecredit.jiraadapter.rest;

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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.Constants;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldParameters;
import ru.homecredit.jiraadapter.dto.RequestParameters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

/**
 * class for manging customfield options trough Jira Java API by handling the
 * data, received from controller, and returning to controlletr the transport
 * FieldOption transport object
 */
@Slf4j
public class FieldOptionsService {
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final OptionsManager optionsManager;
    private final PluginSettingsService pluginSettingsService;
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

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
        log.info("starting FieldOptionsService constructor");
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.optionsManager = optionsManager;
        this.pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);
    }

    /**
     *
     * @return FieldOptions transport object
     */
    public FieldOptions postOption(String requestBody) {
        log.info("starting postOption method");
        RequestParameters requestParameters = extractRequestParameters(requestBody);
        FieldOptions fieldOptions = initializeFieldOptions(requestParameters);
        if (fieldOptions == null) {
            log.error("shutting down postOption cuz fieldOptions object wasn't constructed");
            return null;
        }
        if (!fieldOptions.getFieldParameters().isPermittedToEdit()) {
            log.warn("shutting down postOption cuz field is not permitted for edit");
            return fieldOptions;
        }
        if (!fieldOptions.getFieldParameters().isValidContext()) {
            log.error("shutting down postOption cuz invalid FieldContext");
            return fieldOptions;
        }
        switch (fieldOptions.getRequestParameters().getAction()) {
            case NOT_RECOGNIZED: {
                log.error("shutting down postOption cuz action parameter not provided");
                return fieldOptions;
            }
            case ADD: {
                log.trace("trying to add new Option");
                String newOptionValue = fieldOptions.getRequestParameters().getNewOption();
                if (newOptionValue.equals(Constants.DEFAULT_RECEIVED)) {
                    log.error("shutting down postOption cuz newOption not provided");
                    return fieldOptions;
                }
                log.trace("trying to add new option \"{}\"", newOptionValue);
                if (Arrays.asList(fieldOptions.getFieldOptionsArr()).contains(newOptionValue)) {
                    log.warn("new option \"{}\" already exist", newOptionValue);
                    return fieldOptions;
                }
                int size = fieldOptions.getFieldOptionsArr().length;
                optionsManager.createOption(fieldOptions.getFieldParameters().getFieldConfig(),
                                            null,
                                            (long) (size + 1),
                                            newOptionValue);
                fieldOptions.setResult(true);
                log.trace("added option \"{}\" to Options", newOptionValue);
                /* acquiring Options object and Options from it once again, cuz the
                new one was appended */
                initializeOptions(fieldOptions);
                return fieldOptions;
            }
            case DISABLE: {
                String optionValue = fieldOptions.getRequestParameters().getNewOption();
                Options options = optionsManager.getOptions(
                        fieldOptions.getFieldParameters().getFieldConfig()
                );
                if (options.getOptionForValue(optionValue, null) != null) {
                    options.getOptionForValue(optionValue, null).setDisabled(false);
                    fieldOptions.setResult(true);
                    log.trace("disabled option \"{}\"", optionValue);
                } else {
                    log.error("option {} seems not to exist. shutting down", optionValue);
                }
                return fieldOptions;
            }
            case ENABLE: {
                String optionValue = fieldOptions.getRequestParameters().getNewOption();
                Options options = optionsManager.getOptions(
                        fieldOptions.getFieldParameters().getFieldConfig()
                );
                if (options.getOptionForValue(optionValue, null) != null) {
                    options.getOptionForValue(optionValue, null).setDisabled(false);
                    fieldOptions.setResult(true);
                    log.trace("enabled option \"{}\"", optionValue);
                } else {
                    log.error("option {} seems not to exist. shutting down", optionValue);
                }
                return fieldOptions;
            }
        }
        return fieldOptions;
    }

    /**
     * method to acquire the options of given field in given context
     * @param requestParameters
     * @return - FieldOptions transport object
     */
    public FieldOptions initializeFieldOptions(RequestParameters requestParameters) {
        log.info("starting initializeFieldOptions" +
                            "(RequestParameters requestParameters) method");
        FieldOptions fieldOptions = new FieldOptions();
        fieldOptions.setRequestParameters(requestParameters);
        FieldParameters fieldParameters = initializeFieldParameters(requestParameters);
        if (fieldParameters == null) {
            log.error("shutting down initializeFieldOptions method cuz fieldParameters==null");
            fieldOptions.setFieldParameters(new FieldParameters());
            return fieldOptions;
        }
        fieldOptions.setFieldParameters(fieldParameters);
        initializeOptions(fieldOptions);
        return fieldOptions;
    }

    private RequestParameters extractRequestParameters(String requestBody) {
        log.info("starting extractRequestParameters(String requestBody) method");
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
            log.error("could not parse request body - {}", requestBody);
            log.error("exception is - {}", e.getMessage());
        }
        return requestParameters;
    }

    /**
     *
     */
    private FieldParameters initializeFieldParameters(RequestParameters requestParameters) {
        log.info("starting initializeFieldParameters method");
        FieldParameters fieldParameters = new FieldParameters();
        try {
            ConfigurableField field = fieldManager.
                 getConfigurableField(requestParameters.getFieldKey());
            fieldParameters.setFieldName(field.getName());
            Project project = projectManager.
                  getProjectByCurrentKeyIgnoreCase(requestParameters.getProjectKey());
            fieldParameters.setProjectName(project.getName());
            IssueContextImpl issueContext = new IssueContextImpl(project.getId(),
                   requestParameters.getIssueTypeId());
            FieldConfig fieldConfig = field.getRelevantConfig(issueContext);
            fieldParameters.setFieldConfig(fieldConfig);
            fieldParameters.setFieldConfigName(fieldConfig.getName());
            fieldParameters.setValidContext(true);
            boolean permittedToEdit = pluginSettingsService.getSettings().getEditableFields().
                    contains(requestParameters.getFieldKey());
            fieldParameters.setPermittedToEdit(permittedToEdit);
        } catch (Exception e) {
            log.error("failed to initialize field parameters with error {}",
                      e.getMessage());
            return null;
        }
        return fieldParameters;
    }

    /**
     * method to initialize options of field, attributes of which are stored in
     * @param fieldOptions - transport object
     */
    private void initializeOptions(FieldOptions fieldOptions) {
        log.info("starting initializeOptions method");
        Options options = Objects.requireNonNull(optionsManager.
              getOptions(fieldOptions.getFieldParameters().getFieldConfig()),
              "failed to acquire Options object");
        fieldOptions.setFieldOptionsArr(options.stream().map(op -> op.getValue()).toArray(String[]::new));
        HashMap<String, Boolean> isDisabled = new HashMap<>();
        for (Option option : options) {
            isDisabled.put(option.getValue(), option.getDisabled());
        }
        fieldOptions.setIsDisabled(isDisabled);
        log.trace("field options are {}", fieldOptions.getFieldOptionsArr());
    }

}
