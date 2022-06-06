package HCBplugins.rest;

import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        logger.info("editableFields are: {}", pluginSettings.get(Config.class.getName() +
                                                                  ".editableFields"));

        logger.info("editableFields are: {}", pluginSettings.get(Config.class.getName() + ".editableFields"));
        String settingsString = pluginSettings.get(Config.class.getName() +
                                                                 ".editableFields").toString();
        pluginConfig.setEditableFields(settingsString.split(","));
        return pluginConfig;
    }

    public Config setSettings(String settingsString) {
        logger.info("started setSettings method");
        logger.info("settingsString is: {}", settingsString);
        pluginSettings.put(Config.class.getName() + ".editableFields", settingsString);
        return getSettings();
    }

}
