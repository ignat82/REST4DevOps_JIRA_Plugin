package ru.homecredit.jiraadapter.web;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.service.PluginSettingsService;

import javax.inject.Inject;
import javax.inject.Named;
import javax.ws.rs.*;
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
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * constructor just initializes log and acquires settingsService object
     * @param pluginSettingsService - jira bean to be injected by spring
     */
    @Inject
    public PluginSettingsController(PluginSettingsService pluginSettingsService) {
        log.info("starting FieldOptionsController instance construction");
        this.pluginSettingsService = pluginSettingsService;
    }

    /**
     * method to handle get request trough invoking settingsService
     * @param requestBody - json string with request parameters
     * @return - jsonResponse
     */
    @POST
    @AnonymousAllowed
    @Produces(MediaType.APPLICATION_JSON)
    public Response postSettings(String requestBody) {
        log.info("********** starting postSettings method ************");
        return ((requestBody == null || requestBody.equals("")))
                ? Response.ok(gson.toJson(
                        pluginSettingsService.getSettings())).build()
                : Response.ok(gson.toJson(
                        pluginSettingsService.saveSettings(requestBody))).build();
    }

    @GET
    @AnonymousAllowed
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSettings(@QueryParam("fieldKey") String fieldKey) {
        log.trace("************* starting getSettings method... ************");
        return Response.ok(gson.toJson(
                pluginSettingsService.getSettings())).build();
    }
}
