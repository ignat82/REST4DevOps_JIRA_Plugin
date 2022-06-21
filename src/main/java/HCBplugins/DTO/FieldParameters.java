package HCBplugins.DTO;

import com.atlassian.jira.issue.fields.config.FieldConfig;

import static HCBplugins.Constants.DEFAULT_ACQUIRED;

public class FieldParameters {
    private String fieldName = DEFAULT_ACQUIRED;
    private String projectName = DEFAULT_ACQUIRED;
    private FieldConfig fieldConfig;
    private String fieldConfigName = DEFAULT_ACQUIRED;

    private boolean validContext;
    private boolean isPermittedToEdit;

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

    public FieldConfig getFieldConfig() {
        return fieldConfig;
    }

    public void setFieldConfig(FieldConfig fieldConfig) {
        this.fieldConfig = fieldConfig;
    }

    public String getFieldConfigName() {
        return fieldConfigName;
    }

    public void setFieldConfigName(String fieldConfigName) {
        this.fieldConfigName = fieldConfigName;
    }

    public boolean isValidContext() {
        return validContext;
    }

    public void setValidContext(boolean validContext) {
        this.validContext = validContext;
    }

    public boolean isPermittedToEdit() {
        return isPermittedToEdit;
    }

    public void setPermittedToEdit(boolean permittedToEdit) {
        isPermittedToEdit = permittedToEdit;
    }
}
