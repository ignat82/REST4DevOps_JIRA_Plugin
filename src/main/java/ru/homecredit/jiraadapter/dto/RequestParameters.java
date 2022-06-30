package ru.homecredit.jiraadapter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import ru.homecredit.jiraadapter.Constants;

import static ru.homecredit.jiraadapter.Constants.DEFAULT_RECEIVED;

@Getter
@Setter
@Slf4j
public class RequestParameters {
    private final String fieldKey;
    private final String projectKey;
    private final String issueTypeId;
    private final String newOption;
    private final Action action;

    @Getter
    public enum Action {
        ADD ("add"),
        ENABLE ("enable"),
        DISABLE ("disable"),
        NOT_RECOGNIZED ("not recognized");

        private final String code;
        private static final Action[] ALL_VALUES = Action.values();

        Action(String code) {
            this.code = code;
        }



    }

    public RequestParameters(String fieldKey,
                             String projectKey,
                             String issueTypeId,
                             String newOption,
                             String action) {
        this.fieldKey = StringUtils.defaultIfEmpty(fieldKey, DEFAULT_RECEIVED);
        this.projectKey = StringUtils.defaultIfEmpty(projectKey, DEFAULT_RECEIVED);;
        this.issueTypeId = StringUtils.defaultIfEmpty(issueTypeId, DEFAULT_RECEIVED);;
        this.newOption = StringUtils.defaultIfEmpty(newOption, DEFAULT_RECEIVED);;
        this.action = actionFromCode(action);
    }

    private Action actionFromCode(String code) {
        for (Action action : Action.ALL_VALUES) {
            if (action.getCode().equals(code)) {
                return action;
            }
        }
        return Action.NOT_RECOGNIZED;
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
