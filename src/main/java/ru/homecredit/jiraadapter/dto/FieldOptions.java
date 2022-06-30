package ru.homecredit.jiraadapter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * transport class to handle single REST request to /options endpoint
 */
@Getter
@Setter
@Slf4j
public class FieldOptions {

    private RequestParameters requestParameters;
    private FieldParameters fieldParameters;
    //private String fieldOptionsString = Constants.DEFAULT_ACQUIRED;
    private String[] fieldOptionsArr;
    private Map<String, Boolean> isDisabled = new HashMap<>();
    private boolean optionAdded = false;

    public FieldOptions() {
        this(new RequestParameters());
    }

    /**
     *
     */
    public FieldOptions(RequestParameters requestParameters) {
        this.requestParameters = requestParameters;
    }
}

