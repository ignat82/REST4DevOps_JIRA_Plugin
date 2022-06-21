package HCBplugins.DTO;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static HCBplugins.Constants.DEFAULT_RECEIVED;

public class RequestParameters {
    private final String fieldKey;
    private final String projectKey;
    private final String issueTypeId;
    private final String newOption;
    private final String action;
    private static final Logger logger = LoggerFactory.
         getLogger(RequestParameters.class.getName());

    public RequestParameters(String fieldKey,
                             String projectKey,
                             String issueTypeId,
                             String newOption,
                             String action) {
        logger.info("RequestParameters constructed");
        this.fieldKey = (!StringUtils.isEmpty(fieldKey))
                ? fieldKey : DEFAULT_RECEIVED;
        this.projectKey = (!StringUtils.isEmpty(projectKey))
                ? projectKey : DEFAULT_RECEIVED;
        this.issueTypeId = (!StringUtils.isEmpty(issueTypeId))
                ? issueTypeId : DEFAULT_RECEIVED;
        this.newOption = (!StringUtils.isEmpty(newOption))
                ? newOption : DEFAULT_RECEIVED;
        this.action = (!StringUtils.isEmpty(action))
                ? action : DEFAULT_RECEIVED;
    }

    public RequestParameters(String fieldKey,
                             String projectKey,
                             String issueTypeId) {
        this(fieldKey,
             projectKey,
             issueTypeId,
             DEFAULT_RECEIVED,
             DEFAULT_RECEIVED);
    }

    public RequestParameters() {
        this(DEFAULT_RECEIVED,
             DEFAULT_RECEIVED,
             DEFAULT_RECEIVED,
             DEFAULT_RECEIVED,
             DEFAULT_RECEIVED);
        logger.info("dummy RequestParameters constructed");
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public String getNewOption() {
        return newOption;
    }

    public String getIssueTypeId() {
        return issueTypeId;
    }

    public String getAction() {
        return action;
    }
}
