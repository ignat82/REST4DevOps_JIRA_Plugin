package ru.homecredit.jiraadapter.rest;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.PluginSettings;

import java.util.Arrays;
import java.util.List;

/**
 * servise class to retreive and rewrite plugin settings - the array of strings
 * with jira customfield keys
 */
@Slf4j
public class PluginSettingsService {
    private final com.atlassian.sal.api.pluginsettings.PluginSettings pluginSettings;

    /**
     * constructor creates settingsObject
     * @param pluginSettingsFactory - injected by spring to invoking class
     */
    public PluginSettingsService(PluginSettingsFactory pluginSettingsFactory) {
        log.info("started PluginSettingsService constructor");
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    /**
     * receives the current plugin settings from jira
     * @return - XML transport object
     */
    public PluginSettings getSettings() {
        PluginSettings pluginSettings = new PluginSettings();
        log.info("started getSettings method");
        try {
            List<String> fieldKeys = (List<String>)
                    this.pluginSettings.get(PluginSettings.class.getName() + ".editableFields");
            pluginSettings.setEditableFields(fieldKeys);
            log.info("editableFields are: {}.", fieldKeys);
        } catch (Exception e) {
            log.error("failed to acquire plugin settings with error " + e);
        }
        return pluginSettings;
    }

    /**
     * parses the request body for fieldKeys parameter (comma-separated list of
     * customfield keys and saves them in jira
     * @param requestBody - json string received from POST request
     * @return  - XML transport object
     */
    public PluginSettings saveSettings(String requestBody) {
        log.info("started saveSettings method");
        try {
            String[] fieldsKeys = new JSONObject(requestBody).
                    getString("settingsString").split(",");
            for (String key : fieldsKeys) {
                log.trace("key is: {}", key);
            }
            pluginSettings.put(PluginSettings.class.getName() +
                                       ".editableFields", Arrays.asList(fieldsKeys));
        } catch (JSONException jsonException){
            log.error("caught {} when parsing requestBody", jsonException.getMessage());
        }
        return getSettings();
    }
}
