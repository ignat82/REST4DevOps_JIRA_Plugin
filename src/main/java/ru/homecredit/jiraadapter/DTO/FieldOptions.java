package ru.homecredit.jiraadapter.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.Constants;

/**
 * transport class to handle single REST request to /options endpoint
 */
@Getter
@Setter
@Slf4j
public class FieldOptions {

    private RequestParameters requestParameters;
    private FieldParameters fieldParameters;
    private String fieldOptionsString = Constants.DEFAULT_ACQUIRED;
    private String[] fieldOptionsArr;
    private boolean optionAdded = false;

    public FieldOptions() {
        this(new RequestParameters());
        log.trace("dummy FieldOptions constructed");
    }

    /**
     *
     */
    public FieldOptions(RequestParameters requestParameters) {
        log.trace("starting FieldOptions instance construction");
        this.requestParameters = requestParameters;
    }
}

