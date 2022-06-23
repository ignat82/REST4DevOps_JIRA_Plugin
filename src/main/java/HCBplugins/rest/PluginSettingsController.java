package HCBplugins.rest;

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
public class PluginSettingsController {
    private final        PluginSettingsService pluginSettingsService;
    private final static Logger                logger =
            LoggerFactory.getLogger(PluginSettingsController.class.getName());

    @Inject
    public PluginSettingsController(PluginSettingsFactory pluginSettingsFactory) {
        logger.info("starting FieldOptionsController instance construction");
        pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);
    }

    @POST
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postSettings(String requestBody) {
        logger.info("********** starting postSettings method ************");
        logger.info("request body received is - {}", requestBody);
        return ((requestBody == null || requestBody.equals("")))
                ? Response.ok(pluginSettingsService.getSettings()).build()
                : Response.ok(pluginSettingsService.setSettings(requestBody)).build();
    }
}