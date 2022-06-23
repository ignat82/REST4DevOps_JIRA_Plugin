package ru.homecredit.jiraadapter.rest;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * class to handle POST request to /settings endpoint - receiving and updating
 * the keys of customfield, which options will be permitted to handle by
 * requests to /options endpoint
 */
@Path("/settings")
@Named
@Slf4j
public class PluginSettingsController {
    private final PluginSettingsService pluginSettingsService;

    /**
     * constructor just initializes log and acquires settingsService object
     * @param pluginSettingsFactory - jira bean to be injected by spring
     */
    @Inject
    public PluginSettingsController(PluginSettingsFactory pluginSettingsFactory) {
        log.info("starting FieldOptionsController instance construction");
        pluginSettingsService = new PluginSettingsService(pluginSettingsFactory);
    }

    /**
     * method to handle get request trough invoking settingsService
     * @param requestBody - json string with request parameters
     * @return
     */
    @POST
    @AnonymousAllowed
    @Produces({MediaType.APPLICATION_JSON, MediaType.APPLICATION_XML})
    public Response postSettings(String requestBody) {
        log.info("********** starting postSettings method ************");
        log.trace("request body received is - {}", requestBody);
        return ((requestBody == null || requestBody.equals("")))
                ? Response.ok(pluginSettingsService.getSettings()).build()
                : Response.ok(pluginSettingsService.saveSettings(requestBody)).build();
    }
}