package ru.homecredit.jiraadapter.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import ru.homecredit.jiraadapter.dto.request.Request;

import java.util.HashMap;
import java.util.Map;

/**
 * transport class to handle single REST request to /options endpoint
 */
@Getter
@Setter
@Slf4j
public class FieldOptions {

    private Request request;
    private FieldParameters fieldParameters;
    private String[] fieldOptionsArr;
    private Map<String, Boolean> isDisabled = new HashMap<>();
    private boolean result = false;

    public FieldOptions() {
        this(new Request());
    }

    /**
     *
     */
    public FieldOptions(Request request) {
        this.request = request;
    }
}

