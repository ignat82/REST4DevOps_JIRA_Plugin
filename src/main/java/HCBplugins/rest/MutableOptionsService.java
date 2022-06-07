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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

public class MutableOptionsService {
    private static final Logger                   logger =
            LoggerFactory.getLogger(MutableOptionsService.class);
    private final         FieldManager             fieldManager;
    private final        ProjectManager           projectManager;
    private final        FieldConfigSchemeManager fieldConfigSchemeManager;
    private final        OptionsManager           optionsManager;

    public MutableOptionsService(FieldManager fieldManager,
                                 ProjectManager projectManager,
                                 FieldConfigSchemeManager fieldConfigSchemeManager,
                                 OptionsManager optionsManager) {
        logger.info("starting MutableOptionsService constructor");
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.fieldConfigSchemeManager = fieldConfigSchemeManager;
        this.optionsManager = optionsManager;
        logger.info("constructed MutableOptionsService instance");
    }

    public MutableOptionsObject initializeMoo(String fieldKey, String projectKey) {
        MutableOptionsObject moo;
        logger.info("starting initializeMoo method");
        moo = new MutableOptionsObject(fieldKey, projectKey);
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

    private void initializeField(MutableOptionsObject moo) {
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

    public MutableOptionsObject addNewOption(MutableOptionsObject moo,
                                             String newOption) {
        logger.info("starting addNewOption method");
        if (!moo.isValidContext()) {
            logger.error("shutting down addNewOption cuz MutableOptionsObject " +
                                 "has invalid FieldContext in it");
            return moo;
        }
        if ((newOption != null) && (!newOption.equals(""))) {
            logger.info("trying to add new option \"{}\"", newOption);
            moo.setNewOption(newOption);
            if (Arrays.asList(moo.getFieldOptionsArr()).contains(newOption)) {
                logger.warn("new option \"{}\" already exist", newOption);
                return moo;
            }
            int size = moo.getFieldOptionsArr().length;
            optionsManager.createOption(moo.getFieldConfig(),
                                        null,
                                        (long) (size + 1),
                                        newOption);
            moo.setOptionAdded(true);
            logger.info("added option \"{}\" to Options", newOption);
            /* acquiring Options object and Options from it once again, cuz the
            new one was appended */
            initializeOptions(moo);
        } else {
            logger.warn("failed to add new option due its not provided in REST request");
            logger.warn("shutting down addNewOption method");
        }
        return moo;
    }

    private void initializeOptions(MutableOptionsObject moo) {
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
