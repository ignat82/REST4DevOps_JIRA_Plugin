package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.PluginSettingsService;

import javax.inject.Inject;

@Slf4j
@Getter
public class ConfigurationWebworkAction extends JiraWebActionSupport {
    private final String currentSettings;
    @Inject
    public ConfigurationWebworkAction(PluginSettingsService pluginSettingsService) {
        currentSettings = pluginSettingsService.getSettings().getCommaSeparatedields();
        log.info("current settings are " + currentSettings);
    }

    @Override
    public String execute() throws Exception {
        log.info("ConfigurationWebworkAction.execute() running");
        return "configuration-page";
    }
}
