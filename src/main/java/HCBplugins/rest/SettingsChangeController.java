package HCBplugins.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/settings")
@Named
public class SettingsChangeController {
    private final SettingsService settingsService;

    @Inject
    public SettingsChangeController(PluginSettingsFactory pluginSettingsFactory) {
        settingsService = new SettingsService(pluginSettingsFactory);
    }

    @GET
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response getSomething(@QueryParam("settings_string") String settingsString) {
        return ((settingsString == null || settingsString.equals("")))
                ? Response.ok(settingsService.getSettings()).build()
                : Response.ok(settingsService.setSettings(settingsString)).build();
    }
}
