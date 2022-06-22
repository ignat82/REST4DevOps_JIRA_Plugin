package ru.homecredit.jiraadapter.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.homecredit.jiraadapter.Constants;

@Getter
@Setter
@Slf4j
public class RequestParameters {
    private final String fieldKey;
    private final String projectKey;
    private final String issueTypeId;
    private final String newOption;
    private final String action;

    public RequestParameters(String fieldKey,
                             String projectKey,
                             String issueTypeId,
                             String newOption,
                             String action) {
        log.trace("RequestParameters constructed");
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
        log.trace("dummy RequestParameters constructed");
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("fieldKey = ").append(fieldKey).append("; projectKey = ").
                append(projectKey).append("; issueTypeId = ").append(issueTypeId).
                             append("; new option = ").append(newOption).
                             append("; action = ").append(action).append(".");
        return stringBuilder.toString();
    }

}
