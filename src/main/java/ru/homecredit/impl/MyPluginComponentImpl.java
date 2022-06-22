package ru.homecredit.impl;

import ru.homecredit.api.MyPluginComponent;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService ({MyPluginComponent.class})
@Named ("myPluginComponent")
public class MyPluginComponentImpl implements MyPluginComponent {
    private static final Logger logger = LoggerFactory.getLogger(
            MyPluginComponentImpl.class.getName());
    private final  ApplicationProperties applicationProperties;
    private final  PluginSettingsFactory pluginSettingsFactory;
    public final   FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final  OptionsManager optionsManger;

    @Inject
    public MyPluginComponentImpl(@ComponentImport ApplicationProperties applicationProperties,
                                 @ComponentImport PluginSettingsFactory pluginSettingsFactory,
                                 @ComponentImport FieldManager fieldManager,
                                 @ComponentImport ProjectManager projectManager,
                                 @ComponentImport OptionsManager optionsManger) {
        logger.info("creating MyPluginComponentImpl instance");
        this.applicationProperties = applicationProperties;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.optionsManger = optionsManger;
    }

    public String getName()
    {
        if(null != applicationProperties)
        {
            return "myComponent:" + applicationProperties.getDisplayName();
        }

        return "myComponent";
    }
}
