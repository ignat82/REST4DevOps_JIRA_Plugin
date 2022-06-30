package ru.homecredit.jiraadapter.rest;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

/**
 * servise class to retreive and rewrite plugin settings - the array of strings
 * with jira customfield keys
 */
@Slf4j
public class PluginSettingsService {
    private final PluginSettings pluginSettings;

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
    public PluginSettingsXML getSettings() {
        PluginSettingsXML pluginSettingsXML = new PluginSettingsXML();
        log.info("started getSettings method");
        List<String> fieldKeys = (List<String>)
                pluginSettings.get(PluginSettingsXML.class.getName() + ".editableFields");
        pluginSettingsXML.setEditableFields(fieldKeys);
        log.trace("editableFields are: {}.", fieldKeys);
        return pluginSettingsXML;
    }

    /**
     * parses the request body for fieldKeys parameter (comma-separated list of
     * customfield keys and saves them in jira
     * @param requestBody - json string received from POST request
     * @return  - XML transport object
     */
    public PluginSettingsXML saveSettings(String requestBody) {
        log.info("started saveSettings method");
        try {
            String[] fieldsKeys = new JSONObject(requestBody).
                    getString("settingsString").split(",");
            for (String key : fieldsKeys) {
                log.trace("key is: {}", key);
            }
            pluginSettings.put(PluginSettingsXML.class.getName() +
                                       ".editableFields", Arrays.asList(fieldsKeys));
        } catch (JSONException jsonException){
            log.error("caught {} when parsing requestBody", jsonException.getMessage());
        }
        return getSettings();
    }
}
