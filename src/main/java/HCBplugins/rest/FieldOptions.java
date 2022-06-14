package HCBplugins.rest;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static HCBplugins.rest.Constants.*;

public class FieldOptions {
    private final String fieldKey;
    private final String projectKey;
    private String newOption;
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
        newOption = DEFAULT_RECEIVED;
    }

    public FieldOptions(String fieldKey,
                        String projectKey,
                        String newOption) {
        logger.info("starting FieldOptions instance construction");
        this.fieldKey = fieldKey;
        this.projectKey = projectKey;
        this.newOption = (StringUtils.isEmpty(newOption))
                ? DEFAULT_RECEIVED
                : newOption;
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

    public void setNewOption(String newOption) {
        this.newOption = newOption;
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
}
