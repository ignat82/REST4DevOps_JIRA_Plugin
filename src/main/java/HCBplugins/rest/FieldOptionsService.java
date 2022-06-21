package HCBplugins.rest;

import HCBplugins.DTO.FieldOptions;
import HCBplugins.DTO.FieldParameters;
import HCBplugins.DTO.RequestParameters;
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

import static HCBplugins.Constants.DEFAULT_RECEIVED;

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


    public FieldOptions addNewOption(String requestBody) {
        logger.info("starting addNewOption method with initializing FieldOptions");
        FieldOptions fieldOptions = initializeFieldOptions(requestBody);
        logger.info("getting fieldParameters from FieldOptions");
        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        RequestParameters requestParameters = fieldOptions.getRequestParameters();
        if (!fieldParameters.isPermittedToEdit()) {
            logger.warn("shutting down addNewOption cuz field is not permitted for edit");
            return fieldOptions;
        }
        if (!fieldParameters.isValidContext()) {
            logger.error("shutting down addNewOption cuz invalid FieldContext");
            return fieldOptions;
        }
        String newOptionValue = requestParameters.getNewOption();
        if (newOptionValue.equals(DEFAULT_RECEIVED)) {
            logger.error("shutting down addNewOption cuz newOption not provided");
            return fieldOptions;
        }
        logger.info("trying to add new option \"{}\"", newOptionValue);
        if (Arrays.asList(fieldOptions.getFieldOptionsArr()).contains(newOptionValue)) {
            logger.warn("new option \"{}\" already exist", newOptionValue);
            return fieldOptions;
        }
        int size = fieldOptions.getFieldOptionsArr().length;
        optionsManager.createOption(fieldParameters.getFieldConfig(),
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

    public FieldOptions enableOption(String requestBody) {
        logger.info("starting enable Option method with initializing FieldOptions");
        FieldOptions fieldOptions = initializeFieldOptions(requestBody);
        logger.info("getting fieldParameters from FieldOptions");
        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        RequestParameters requestParameters = fieldOptions.getRequestParameters();
        if (!fieldParameters.isPermittedToEdit()) {
            logger.warn("shutting down cuz field is not permitted for edit");
            return fieldOptions;
        }
        if (!fieldParameters.isValidContext()) {
            logger.error("shutting down cuz invalid FieldContext");
            return fieldOptions;
        }
        String optionValue = requestParameters.getNewOption();
        logger.info("trying to enable option \"{}\"", optionValue);
        Options options = optionsManager.getOptions(fieldParameters.getFieldConfig());
        if (options.getOptionForValue(optionValue, null) != null) {
            options.getOptionForValue(optionValue, null).setDisabled(false);
            logger.info("enabled option \"{}\"", requestParameters.getNewOption());
        } else {
            logger.error("option {} seems not to exist. shutting down",
                         requestParameters.getNewOption());
        }
        return fieldOptions;
    }

    public FieldOptions disableOption(String requestBody) {
        logger.info("starting disableOption method with initializing FieldOptions");
        FieldOptions fieldOptions = initializeFieldOptions(requestBody);
        logger.info("getting fieldParameters from FieldOptions");
        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        RequestParameters requestParameters = fieldOptions.getRequestParameters();
        if (!fieldParameters.isPermittedToEdit()) {
            logger.warn("shutting down cuz field is not permitted for edit");
            return fieldOptions;
        }
        if (!fieldParameters.isValidContext()) {
            logger.error("shutting down cuz invalid FieldContext");
            return fieldOptions;
        }
        String optionValue = requestParameters.getNewOption();
        logger.info("trying to disable option \"{}\"", optionValue);
        Options options = optionsManager.getOptions(fieldParameters.getFieldConfig());
        options.getOptionForValue(optionValue, null).setDisabled(true);
        logger.info("disabled option \"{}\"", requestParameters.getNewOption());
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
                            "(String fieldKey, String projectKey) method");
        logger.info("issue type id received is {}", issueTypeId);

        RequestParameters requestParameters =
                new RequestParameters(fieldKey, projectKey, issueTypeId);

        FieldOptions fieldOptions = new FieldOptions(requestParameters);
        try {
            FieldParameters fieldParameters =
                    initializeFieldParameters(requestParameters);
            fieldOptions.setFieldParameters(fieldParameters);
            initializeOptions(fieldOptions);
        } catch (Exception exception) {
            logger.warn("got exception when initializing MutableOptionObject: {}",
                        exception.getMessage());
            logger.warn("shutting down initializeFieldOptions method");
            fieldOptions.setFieldParameters(new FieldParameters());
            return fieldOptions;
        }
        return fieldOptions;
    }

    /**
     * same method but for POST requests, which parameters are received in
     * @param requestBody - json string of POST request parameters
     * @return FieldOptions transport object
     */
    public FieldOptions initializeFieldOptions(String requestBody) {
        logger.info("starting initializeFieldOptions (String requestBody) method");
        FieldOptions fieldOptions;
        RequestParameters requestParameters;
        FieldParameters fieldParameters;

        try {
            // this block fail if ANY parameter not provided
            JSONObject requestJSON = new JSONObject(requestBody);
            requestParameters =
                    new RequestParameters(requestJSON.getString("fieldKey"),
                                          requestJSON.getString("projectKey"),
                                          requestJSON.getString("issueTypeId"),
                                          requestJSON.getString("newOption"),
                                          requestJSON.getString("action"));
            fieldOptions = new FieldOptions(requestParameters);
        } catch (JSONException jsonException) {
            logger.error("caught {} when parsing parameters from requestBody",
                         jsonException.getMessage());
            logger.warn("shutting down initializeFieldOptions method");
            fieldOptions = new FieldOptions();
            fieldOptions.setFieldParameters(new FieldParameters());
            return fieldOptions;
            // requestParameters = new RequestParameters();
        }
        try {
            fieldParameters = initializeFieldParameters(requestParameters);
            fieldOptions.setFieldParameters(fieldParameters);
            initializeOptions(fieldOptions);
        } catch (Exception exception) {
            logger.warn("got exception when initializing FieldOptions: {}",
                        exception.getMessage());
            logger.warn("shutting down initializeFieldOptions method");
            return fieldOptions;
        }
        return fieldOptions;
    }

    /**
     *
     */
    private FieldParameters initializeFieldParameters(RequestParameters requestParameters) {
        logger.info("starting initializeFieldParameters method");
        FieldParameters fieldParameters = new FieldParameters();
        ConfigurableField field = Objects.requireNonNull(fieldManager.
               getConfigurableField(requestParameters.getFieldKey()),
                    "failed to acquire field " + requestParameters.getFieldKey());
        fieldParameters.setFieldName(field.getName());
        logger.info("field {} acquired as {}", requestParameters.getFieldKey(),
                    fieldParameters.getFieldName());
        Project project =
                Objects.requireNonNull(projectManager.
                       getProjectByCurrentKeyIgnoreCase(requestParameters.getProjectKey()),
                       "failed to acquire project " + requestParameters.getProjectKey());
        fieldParameters.setProjectName(project.getName());
        logger.info("project {} acquired as {}",
                    requestParameters.getProjectKey(), fieldParameters.getProjectName());
        IssueContextImpl issueContext = new IssueContextImpl(project.getId(),
                              requestParameters.getIssueTypeId());
        logger.info("issue context acquired as " + issueContext.toString());
        FieldConfig fieldConfig = Objects.requireNonNull(field.
              getRelevantConfig(issueContext), "failed to acquire FieldConfig from context");
        fieldParameters.setFieldConfig(fieldConfig);
        fieldParameters.setFieldConfigName(fieldConfig.getName());
        fieldParameters.setValidContext(true);
        logger.info("field configuration acquired as {}",
                    fieldParameters.getFieldConfigName());
        boolean permittedToEdit = pluginSettingsService.getSettings().getEditableFields().
                contains(requestParameters.getFieldKey());
        logger.info("field permitted to edit in plugin settings {}", permittedToEdit);
        fieldParameters.setPermittedToEdit(permittedToEdit);
        return fieldParameters;
    }

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
        fieldOptions.setFieldOptionsArr(getStringArrayFromOptionsObject(options));
        // fieldOptions.setFieldOptionsArr((String[]) options.toArray());
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
