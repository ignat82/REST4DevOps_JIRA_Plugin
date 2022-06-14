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
    private final String fieldKey;
    private final String projectKey;
    private final String issueTypeId;
    private final String newOption;
    private FieldConfig fieldConfig;
    private String fieldName = DEFAULT_ACQUIRED;
    private String projectName = DEFAULT_ACQUIRED;
    private String fieldConfigName = DEFAULT_ACQUIRED;
    private String fieldOptionsString = DEFAULT_ACQUIRED;
    private String[] fieldOptionsArr;
    private boolean optionAdded = false;
    private boolean validContext = false;
    private static final Logger logger = LoggerFactory.
            getLogger(FieldOptions.class.getName());

    public FieldOptions() {
        fieldKey = DEFAULT_RECEIVED;
        projectKey = DEFAULT_RECEIVED;
        issueTypeId = DEFAULT_RECEIVED;
        newOption = DEFAULT_RECEIVED;
    }

    /**
     * constructor puts the received request parameters to fields.
     * further methods will populate the rest of fields, and finally
     * the object will be provided for repacking acquired parameters to XML
     * @param fieldKey - jira customfield key
     * @param projectKey - jira project key
     * @param issueTypeId - jira issue type id
     * @param newOption - the value of new option
     */
    public FieldOptions(String fieldKey,
                        String projectKey,
                        String issueTypeId,
                        String newOption) {
        logger.info("starting FieldOptions instance construction");
        logger.info("issue type id received is {}", issueTypeId);
        this.fieldKey = (!StringUtils.isEmpty(fieldKey))
                ? fieldKey : DEFAULT_RECEIVED;
        this.projectKey = (!StringUtils.isEmpty(projectKey))
                ? projectKey : DEFAULT_RECEIVED;
        this.issueTypeId = (!StringUtils.isEmpty(issueTypeId))
                ? issueTypeId : DEFAULT_RECEIVED;
        this.newOption = (!StringUtils.isEmpty(newOption))
                ? newOption : DEFAULT_RECEIVED;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFieldConfigName() {
        return fieldConfigName;
    }

    public void setFieldConfigName(String fieldConfigName) {
        this.fieldConfigName = fieldConfigName;
    }

    public String getFieldOptionsString() {
        return fieldOptionsString;
    }

    public void setFieldOptionsString(String fieldOptionsString) {
        this.fieldOptionsString = fieldOptionsString;
    }

    public String[] getFieldOptionsArr() {
        return (fieldOptionsArr == null)
                ? null
                : Arrays.copyOf(fieldOptionsArr, fieldOptionsArr.length);
    }

    public void setFieldOptionsArr(String[] fieldOptionsArr) {
        this.fieldOptionsArr = fieldOptionsArr;
    }

    public FieldConfig getFieldConfig() {
        return fieldConfig;
    }

    public void setFieldConfig(FieldConfig fieldConfig) {
        this.fieldConfig = fieldConfig;
    }

    public String getNewOption() {
        return newOption;
    }

    public boolean isOptionAdded() {
        return optionAdded;
    }

    public void setOptionAdded(boolean optionAdded) {
        this.optionAdded = optionAdded;
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public boolean isValidContext() {
        return validContext;
    }

    public void setValidContext(boolean validContext) {
        this.validContext = validContext;
    }

    public String getIssueTypeId() {
        return issueTypeId;
    }
}
