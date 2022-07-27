package ru.homecredit.jiraadapter.impl;

import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.project.ProjectManager;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.api.MyPluginComponent;

import javax.inject.Inject;
import javax.inject.Named;

@ExportAsService ({MyPluginComponent.class})
@Named ("myPluginComponent")
@Slf4j
public class MyPluginComponentImpl implements MyPluginComponent {
    private final ApplicationProperties applicationProperties;
    private final PluginSettingsFactory pluginSettingsFactory;
    private final FieldManager fieldManager;
    private final ProjectManager projectManager;
    private final OptionsManager optionsManger;

    @Inject
    public MyPluginComponentImpl(@ComponentImport ApplicationProperties applicationProperties,
                                 @ComponentImport PluginSettingsFactory pluginSettingsFactory,
                                 @ComponentImport FieldManager fieldManager,
                                 @ComponentImport ProjectManager projectManager,
                                 @ComponentImport OptionsManager optionsManger) {
        log.trace("creating MyPluginComponentImpl instance");
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
