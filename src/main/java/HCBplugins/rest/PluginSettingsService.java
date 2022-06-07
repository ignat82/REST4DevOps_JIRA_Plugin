package HCBplugins.rest;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.sal.api.pluginsettings.PluginSettings;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class PluginSettingsService {
    private final PluginSettings pluginSettings;
    private static final Logger logger = LoggerFactory.
            getLogger(PluginSettingsService.class.getName());

    public PluginSettingsService(PluginSettingsFactory pluginSettingsFactory) {
        logger.info("started PluginSettingsService constructor");
        pluginSettings = pluginSettingsFactory.createGlobalSettings();
    }

    public PluginSettingsXML getSettings() {
        PluginSettingsXML pluginSettingsXML = new PluginSettingsXML();
        logger.info("started getSettings method");
        List<String> fieldKeys = (List<String>)
                pluginSettings.get(PluginSettingsXML.class.getName() + ".editableFields");
        logger.info("editableFields are: {}.", fieldKeys);
        pluginSettingsXML.setEditableFields(fieldKeys);
        return pluginSettingsXML;
    }

    public PluginSettingsXML setSettings(String requestBody) {
        logger.info("started setSettings method");
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
