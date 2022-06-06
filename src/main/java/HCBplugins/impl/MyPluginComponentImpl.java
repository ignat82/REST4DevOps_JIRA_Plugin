package HCBplugins.impl;

import HCBplugins.api.MyPluginComponent;
import com.atlassian.jira.issue.customfields.manager.OptionsManager;
import com.atlassian.jira.issue.fields.FieldManager;
import com.atlassian.jira.issue.fields.config.manager.FieldConfigSchemeManager;
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
    private static Logger                logger = LoggerFactory.getLogger(MyPluginComponentImpl.class.getName());
    @ComponentImport
    private final  ApplicationProperties applicationProperties;
    @ComponentImport
    private final  PluginSettingsFactory pluginSettingsFactory;
    @ComponentImport
    public final   FieldManager          fieldManager;
    @ComponentImport
    private final ProjectManager projectManager;
    @ComponentImport
    private final FieldConfigSchemeManager fieldConfigSchemeManager;
    @ComponentImport
    private final  OptionsManager        optionsManger;

    @Inject
    public MyPluginComponentImpl(ApplicationProperties applicationProperties,
                                 PluginSettingsFactory pluginSettingsFactory,
                                 FieldManager fieldManager,
                                 ProjectManager projectManager,
                                 FieldConfigSchemeManager fieldConfigSchemeManager,
                                 OptionsManager optionsManger) {
        logger.info("creating MyPluginComponentImpl instance");
        this.applicationProperties = applicationProperties;
        this.pluginSettingsFactory = pluginSettingsFactory;
        this.fieldManager = fieldManager;
        this.projectManager = projectManager;
        this.fieldConfigSchemeManager = fieldConfigSchemeManager;
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
