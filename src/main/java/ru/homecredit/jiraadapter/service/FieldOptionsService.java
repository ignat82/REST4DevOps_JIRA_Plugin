package ru.homecredit.jiraadapter.service;

import com.atlassian.jira.issue.context.IssueContextImpl;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.customfields.option.Option;
import com.atlassian.jira.issue.customfields.option.Options;
import com.atlassian.jira.issue.fields.ConfigurableField;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.FieldConfig;
import com.atlassian.jira.project.Project;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.Constants;
import ru.homecredit.jiraadapter.dto.FieldOptions;
import ru.homecredit.jiraadapter.dto.FieldParameters;
import ru.homecredit.jiraadapter.dto.request.Request;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

import static ru.homecredit.jiraadapter.dto.request.Request.Action.DISABLE;

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
        Request request = extractRequestParameters(requestBody);
        FieldOptions fieldOptions = initializeFieldOptions(request);
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
        switch (fieldOptions.getRequest().getAction()) {
            case NOT_RECOGNIZED: {
                log.error("shutting down postOption cuz action parameter not provided");
                return fieldOptions;
            }
            case ADD: {
                log.trace("trying to add new Option");
                String newOptionValue = fieldOptions.getRequest().getNewOption();
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
            case DISABLE:
            case ENABLE: {
                String optionValue = fieldOptions.getRequest().getNewOption();
                Options options = optionsManager.getOptions(
                        fieldOptions.getFieldParameters().getFieldConfig()
                );
                if (options.getOptionForValue(optionValue, null) != null) {
                    options.getOptionForValue(optionValue, null).setDisabled(
                            fieldOptions.getRequest().getAction() == DISABLE
                    );
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
     * @param request
     * @return - FieldOptions transport object
     */
    public FieldOptions initializeFieldOptions(Request request) {
        log.info("starting initializeFieldOptions" +
                            "(Request request) method");
        FieldOptions fieldOptions = new FieldOptions();
        fieldOptions.setRequest(request);
        FieldParameters fieldParameters = initializeFieldParameters(request);
        if (fieldParameters == null) {
            log.error("shutting down initializeFieldOptions method cuz fieldParameters==null");
            fieldOptions.setFieldParameters(new FieldParameters());
            return fieldOptions;
        }
        fieldOptions.setFieldParameters(fieldParameters);
        initializeOptions(fieldOptions);
        return fieldOptions;
    }

    private Request extractRequestParameters(String requestBody) {
        log.info("starting extractRequestParameters(String requestBody) method");
        Request request = null;
        try {
            request = gson.fromJson(requestBody, Request.class);
            if (request.getAction() == null) {
                log.warn("got null action. setting default");
                request.setAction(Request.Action.NOT_RECOGNIZED);
            }
            log.info("json deserialized \n{}", request);
        } catch (Exception e) {
            log.error("could not parse request body - {}", requestBody);
            log.error("exception is - {}", e.getMessage());
        }
        return request;
    }

    /**
     *
     */
    private FieldParameters initializeFieldParameters(Request request) {
        log.info("starting initializeFieldParameters method");
        FieldParameters fieldParameters = new FieldParameters();
        try {
            // log.info("field key is " + request.getFieldKey());
            ConfigurableField field = fieldManager.
                 getConfigurableField(request.getFieldKey());
            fieldParameters.setFieldName(field.getName());
            // log.info("field name is " + field.getName());
            Project project = projectManager.
                  getProjectByCurrentKeyIgnoreCase(request.getProjectKey());
            fieldParameters.setProjectName(project.getName());
            IssueContextImpl issueContext = new IssueContextImpl(project.getId(),
                                                                 request.getIssueTypeId());
            FieldConfig fieldConfig = field.getRelevantConfig(issueContext);
            fieldParameters.setFieldConfig(fieldConfig);
            fieldParameters.setFieldConfigName(fieldConfig.getName());
            fieldParameters.setValidContext(true);
            log.info("valid context " + fieldParameters.isValidContext());
            boolean permittedToEdit = pluginSettingsService.getSettings().getEditableFields().
                    contains(request.getFieldKey());
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
