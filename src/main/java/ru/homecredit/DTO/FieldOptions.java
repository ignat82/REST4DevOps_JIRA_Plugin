package ru.homecredit.DTO;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homecredit.Constants;

/**
 * transport class to handle single REST request to /options endpoint
 */
@Getter
@Setter
public class FieldOptions {

    private static final Logger logger = LoggerFactory.
          getLogger(FieldOptions.class.getName());

    private RequestParameters requestParameters;
    private FieldParameters fieldParameters;
    private String fieldOptionsString = Constants.DEFAULT_ACQUIRED;
    private String[] fieldOptionsArr;
    private boolean optionAdded = false;

    public FieldOptions() {
        this(new RequestParameters());
        logger.info("dummy FieldOptions constructed");
    }

    /**
     *
     */
    public FieldOptions(RequestParameters requestParameters) {
        logger.info("starting FieldOptions instance construction");
        this.requestParameters = requestParameters;
    }
}

