package HCBplugins.rest;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static HCBplugins.rest.Constants.DEFAULT_ACQUIRED;
import static HCBplugins.rest.Constants.DEFAULT_RECEIVED;

/**
 * transport class to handle single REST request to /options endpoint
 */
public class FieldOptions {

    public static class RequestParameters {
        private final String fieldKey;
        private final String projectKey;
        private final String issueTypeId;
        private final String newOption;
        private final String action;

        public RequestParameters(String fieldKey,
                                 String projectKey,
                                 String issueTypeId,
                                 String newOption,
                                 String action) {
            this.fieldKey = (!StringUtils.isEmpty(fieldKey))
                    ? fieldKey : DEFAULT_RECEIVED;
            this.projectKey = (!StringUtils.isEmpty(projectKey))
                    ? projectKey : DEFAULT_RECEIVED;
            this.issueTypeId = (!StringUtils.isEmpty(issueTypeId))
                    ? issueTypeId : DEFAULT_RECEIVED;
            this.newOption = (!StringUtils.isEmpty(newOption))
                    ? newOption : DEFAULT_RECEIVED;
            this.action = (!StringUtils.isEmpty(action))
                    ? action : DEFAULT_RECEIVED;
        }

        public RequestParameters(String fieldKey,
                                 String projectKey,
                                 String issueTypeId) {
            this(fieldKey,
                 projectKey,
                 issueTypeId,
                 DEFAULT_RECEIVED,
                 DEFAULT_RECEIVED);
        }

        public RequestParameters() {
            this(DEFAULT_RECEIVED,
                 DEFAULT_RECEIVED,
                 DEFAULT_RECEIVED,
                 DEFAULT_RECEIVED,
                 DEFAULT_RECEIVED);
            logger.warn("dummy RequestParameters constructed");
        }

        public String getFieldKey() { return fieldKey; }
        public String getProjectKey() { return projectKey; }
        public String getNewOption() { return newOption; }
        public String getIssueTypeId() { return issueTypeId; }
        public String getAction() { return action; }
    }

    public static class FieldParameters {
        private String fieldName = DEFAULT_ACQUIRED;
        private String projectName = DEFAULT_ACQUIRED;
        private FieldConfig fieldConfig;
        private String fieldConfigName = DEFAULT_ACQUIRED;

        private boolean validContext = false;
        private boolean isPermittedToEdit = false;

        public String getFieldName() { return fieldName; }
        public void setFieldName(String fieldName) { this.fieldName = fieldName; }

        public String getProjectName() { return projectName; }
        public void setProjectName(String projectName) { this.projectName = projectName; }

        public FieldConfig getFieldConfig() { return fieldConfig; }
        public void setFieldConfig(FieldConfig fieldConfig) {
            this.fieldConfig = fieldConfig; }

        public String getFieldConfigName() { return fieldConfigName; }
        public void setFieldConfigName(String fieldConfigName) { this.fieldConfigName = fieldConfigName; }

        public boolean isValidContext() { return validContext; }
        public void setValidContext(boolean validContext) {
            this.validContext = validContext; }

        public boolean isPermittedToEdit() { return isPermittedToEdit; }
        public void setPermittedToEdit(boolean permittedToEdit) {
            isPermittedToEdit = permittedToEdit; }
    }

    private static final Logger logger = LoggerFactory.
          getLogger(FieldOptions.class.getName());

    private RequestParameters requestParameters;
    private FieldParameters fieldParameters;
    private String fieldOptionsString = DEFAULT_ACQUIRED;
    private String[] fieldOptionsArr;
    private boolean optionAdded = false;

    public FieldOptions() {
        this(new RequestParameters());
        logger.info("dummy FieldOptions constructed");
    }

    /**
     *
     */
    public FieldOptions(RequestParameters requestParameters) {
        logger.info("starting FieldOptions instance construction");
        setRequestParameters(requestParameters);
    }

    public RequestParameters getRequestParameters() {
        return new RequestParameters(requestParameters.getFieldKey(),
                                     requestParameters.getProjectKey(),
                                     requestParameters.getIssueTypeId(),
                                     requestParameters.getNewOption(),
                                     requestParameters.getAction());
    }

    public void setRequestParameters(RequestParameters requestParameters) {
        this.requestParameters = new RequestParameters(
                                            requestParameters.getFieldKey(),
                                            requestParameters.getProjectKey(),
                                            requestParameters.getIssueTypeId(),
                                            requestParameters.getNewOption(),
                                            requestParameters.getAction());
    }

    public FieldParameters getFieldParameters() {
        FieldParameters fieldParameters = new FieldParameters();
        fieldParameters.setFieldName(this.fieldParameters.getFieldName());
        fieldParameters.setProjectName(this.fieldParameters.getProjectName());
        fieldParameters.setFieldConfig(this.fieldParameters.getFieldConfig());
        fieldParameters.setFieldConfigName(this.fieldParameters.getFieldConfigName());
        // fieldParameters.setFieldOptionsString(this.fieldParameters.getFieldOptionsString());
        // fieldParameters.setFieldOptionsArr(this.fieldParameters.getFieldOptionsArr());
        fieldParameters.setValidContext(this.fieldParameters.isValidContext());
        fieldParameters.setPermittedToEdit(this.fieldParameters.isPermittedToEdit());
        return fieldParameters;
    }

    public void setFieldParameters(FieldParameters fieldParameters) {
        this.fieldParameters = new FieldParameters();
        this.fieldParameters.setFieldName(fieldParameters.getFieldName());
        this.fieldParameters.setProjectName(fieldParameters.getProjectName());
        this.fieldParameters.setFieldConfig(fieldParameters.getFieldConfig());
        this.fieldParameters.setFieldConfigName(fieldParameters.getFieldConfigName());
        // this.fieldParameters.setFieldOptionsString(fieldParameters.getFieldOptionsString());
        // this.fieldParameters.setFieldOptionsArr(fieldParameters.getFieldOptionsArr());
        this.fieldParameters.setValidContext(fieldParameters.isValidContext());
        this.fieldParameters.setPermittedToEdit(fieldParameters.isPermittedToEdit());
    }

    public String getFieldOptionsString() { return fieldOptionsString; }
    public void setFieldOptionsString(String fieldOptionsString) {
        this.fieldOptionsString = fieldOptionsString; }

    public String[] getFieldOptionsArr() {
        return (fieldOptionsArr != null)
                ? Arrays.copyOf(fieldOptionsArr, fieldOptionsArr.length)
                : null; }
    public void setFieldOptionsArr(String[] fieldOptionsArr) {
        this.fieldOptionsArr = (fieldOptionsArr != null)
                ? Arrays.copyOf(fieldOptionsArr, fieldOptionsArr.length)
                : null; }

    public boolean isOptionAdded() { return optionAdded; }
    public void setOptionAdded(boolean optionAdded) { this.optionAdded = optionAdded; }
}
