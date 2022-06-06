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
        logger.info("started getSettings method");
        Config pluginConfig = new Config();
        // logger.info("Config.class.getName() is: {}", Config.class.getName());
        logger.info("editableFields are: {}.", pluginSettings.get(Config.class.getName() +
                                                                  ".editableFields"));
        /*
        String settingsString = pluginSettings.get(Config.class.getName() +
                                                                 ".editableFields").toString();
        pluginConfig.setEditableFields(settingsString.split(","));
        */
        List<String> fieldKeys = (List<String>)
                pluginSettings.get(Config.class.getName() + ".editableFields");
        logger.info("retrieved list");
        pluginConfig.setEditableFields(fieldKeys);

        return pluginConfig;
    }

    public Config setSettings(String settingsString) throws JSONException {
        logger.info("started setSettings method");
        logger.info("settingsString is: {}", settingsString);
        settingsString = new JSONObject(settingsString).getString("settingsString");
        String[] fieldKeys = settingsString.split(",");
        for (String key : fieldKeys) {
            logger.info("key is: {}", key);
        }
        pluginSettings.put(Config.class.getName() +
                                   ".editableFields", Arrays.asList(fieldKeys));
        return getSettings();
    }

}
