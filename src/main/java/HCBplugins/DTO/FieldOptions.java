package HCBplugins.DTO;

import HCBplugins.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

/**
 * transport class to handle single REST request to /options endpoint
 */
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
        setRequestParameters(requestParameters);
    }

    public RequestParameters getRequestParameters() {
        return requestParameters;
    }

    public void setRequestParameters(RequestParameters requestParameters) {
        this.requestParameters = requestParameters;
    }

    public FieldParameters getFieldParameters() {
        return fieldParameters;
    }

    public void setFieldParameters(FieldParameters fieldParameters) {
        this.fieldParameters = fieldParameters;
    }

    public String getFieldOptionsString() { return fieldOptionsString; }
    public void setFieldOptionsString(String fieldOptionsString) {
        this.fieldOptionsString = fieldOptionsString; }

    public String[] getFieldOptionsArr() {
        return (fieldOptionsArr != null)
                ? Arrays.copyOf(fieldOptionsArr, fieldOptionsArr.length)
                : null; }
    public void setFieldOptionsArr(String[] fieldOptionsArr) {
        this.fieldOptionsArr = (fieldOptionsArr != null)
                ? Arrays.copyOf(fieldOptionsArr, fieldOptionsArr.length)
                : null; }

    public boolean isOptionAdded() { return optionAdded; }
    public void setOptionAdded(boolean optionAdded) { this.optionAdded = optionAdded; }
}
