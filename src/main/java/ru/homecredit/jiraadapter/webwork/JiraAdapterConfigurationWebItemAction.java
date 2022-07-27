package ru.homecredit.jiraadapter.webwork;

import com.atlassian.jira.web.action.JiraWebActionSupport;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JiraAdapterConfigurationWebItemAction extends JiraWebActionSupport
{
    @Override
    public String execute() throws Exception {
        log.info("JiraAdapterConfigurationWebItemAction.execute() running");
        return "configuration-page";
    }
}
