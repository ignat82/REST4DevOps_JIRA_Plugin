package ru.homecredit.DTO;

import lombok.Getter;
import lombok.Setter;
import ru.homecredit.Constants;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Getter
@Setter
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
                ? fieldKey : Constants.DEFAULT_RECEIVED;
        this.projectKey = (!StringUtils.isEmpty(projectKey))
                ? projectKey : Constants.DEFAULT_RECEIVED;
        this.issueTypeId = (!StringUtils.isEmpty(issueTypeId))
                ? issueTypeId : Constants.DEFAULT_RECEIVED;
        this.newOption = (!StringUtils.isEmpty(newOption))
                ? newOption : Constants.DEFAULT_RECEIVED;
        this.action = (!StringUtils.isEmpty(action))
                ? action : Constants.DEFAULT_RECEIVED;
    }

    public RequestParameters(String fieldKey,
                             String projectKey,
                             String issueTypeId) {
        this(fieldKey,
             projectKey,
             issueTypeId,
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED);
    }

    public RequestParameters() {
        this(Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED,
             Constants.DEFAULT_RECEIVED);
        logger.info("dummy RequestParameters constructed");
    }
}
