package HCBplugins.rest;

import com.atlassian.jira.util.json.JSONException;
import com.atlassian.jira.util.json.JSONObject;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/settings")
@Named
public class SettingsChangeController {
    private final SettingsService settingsService;
    private final static Logger logger =
            LoggerFactory.getLogger(SettingsChangeController.class.getName());

    @Inject
    public SettingsChangeController(PluginSettingsFactory pluginSettingsFactory) {
        settingsService = new SettingsService(pluginSettingsFactory);
    }

    @POST
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    // @Consumes({MediaType.APPLICATION_JSON, "application/json"})
    public Response getSomething(String requestBody) throws JSONException {
        // logger.warn(MediaType.APPLICATION_JSON);
        logger.warn("request body received is - {}", requestBody);
        String settingsString = new JSONObject(requestBody).getString("settingsString");
        logger.info("settingsString is {}", settingsString);
        return ((requestBody == null || requestBody.equals("")))
                ? Response.ok(settingsService.getSettings()).build()
                : Response.ok(settingsService.setSettings(requestBody)).build();
    }
}
