package ru.homecredit.DTO;

import com.atlassian.jira.issue.fields.config.FieldConfig;
import lombok.Data;

import static ru.homecredit.Constants.DEFAULT_ACQUIRED;

@Data
public class FieldParameters {
    private String fieldName = DEFAULT_ACQUIRED;
    private String projectName = DEFAULT_ACQUIRED;
    private FieldConfig fieldConfig;
    private String fieldConfigName = DEFAULT_ACQUIRED;

    private boolean validContext;
    private boolean isPermittedToEdit;
}
