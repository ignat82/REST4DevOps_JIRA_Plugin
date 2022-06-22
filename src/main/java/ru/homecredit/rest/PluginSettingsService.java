package ru.homecredit.rest;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

/**
 * servise class to retreive and rewrite plugin settings - the array of strings
 * with jira customfield keys
 */
public class PluginSettingsService {
    private final PluginSettings pluginSettings;
    private static final Logger logger = LoggerFactory.
            getLogger(PluginSettingsService.class.getName());

    /**
     * constructor creates settingsObject
     * @param pluginSettingsFactory - injected by spring to invoking class
     */
    public PluginSettingsService(PluginSettingsFactory pluginSettingsFactory) {
        logger.info("started PluginSettingsService constructor");
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    /**
     * receives the current plugin settings from jira
     * @return - XML transport object
     */
    public PluginSettingsXML getSettings() {
        PluginSettingsXML pluginSettingsXML = new PluginSettingsXML();
        logger.info("started getSettings method");
        List<String> fieldKeys = (List<String>)
                pluginSettings.get(PluginSettingsXML.class.getName() + ".editableFields");
        logger.info("editableFields are: {}.", fieldKeys);
        pluginSettingsXML.setEditableFields(fieldKeys);
        return pluginSettingsXML;
    }

    /**
     * parses the request body for fieldKeys parameter (comma-separated list of
     * customfield keys and saves them in jira
     * @param requestBody - json string received from POST request
     * @return  - XML transport object
     */
    public PluginSettingsXML saveSettings(String requestBody) {
        logger.info("started saveSettings method");
        logger.info("requestBody is: {}", requestBody);
        try {
            String[] fieldsKeys = new JSONObject(requestBody).
                    getString("settingsString").split(",");
            for (String key : fieldsKeys) {
                logger.info("key is: {}", key);
            }
            pluginSettings.put(PluginSettingsXML.class.getName() +
                                       ".editableFields", Arrays.asList(fieldsKeys));
        } catch (JSONException jsonException){
            logger.error("caught {} when parsing requestBody", jsonException.getMessage());
        }
        return getSettings();
    }
}
