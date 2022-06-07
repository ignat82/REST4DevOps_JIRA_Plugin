package HCBplugins.rest;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class SettingsService {
    private final PluginSettings pluginSettings;
    private static final Logger logger = LoggerFactory.
            getLogger(SettingsService.class.getName());

    public SettingsService(PluginSettingsFactory pluginSettingsFactory) {
        logger.info("started SettingsService constructor");
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    public Config getSettings() {
        Config pluginConfig = new Config();
        logger.info("started getSettings method");
        logger.info("editableFields are: {}.", pluginSettings.get(Config.class.getName() +
                                                                  ".editableFields"));
        List<String> fieldKeys = (List<String>)
                pluginSettings.get(Config.class.getName() + ".editableFields");
        logger.info("retrieved list");
        pluginConfig.setEditableFields(fieldKeys);

        return pluginConfig;
    }

    public Config setSettings(String requestBody) {
        String[] fieldsKeys;

        logger.info("started setSettings method");
        logger.info("settingsString is: {}", requestBody);
        try {
            fieldsKeys = new JSONObject(requestBody).getString("settingsString").split(",");
            for (String key : fieldsKeys) {
                logger.info("key is: {}", key);
            }
            pluginSettings.put(Config.class.getName() +
                                       ".editableFields", Arrays.asList(fieldsKeys));
        } catch (JSONException jsonException){
            logger.error("caught {} when parsing requestBody", jsonException.getMessage());
        }
        return getSettings();
    }
}
