package HCBplugins.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;

public class WebWorkAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(WebWorkAction.class);

    @Override
    public String execute() throws Exception {
        log.info("WebWorkAction.execute() running");
        return "webwork-module.name";
    }
}
