package HCBplugins.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.util.Arrays;

/**
 * class to pack FieldOptions transport object to xml response
 */
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class FieldOptionsXML {

    @XmlAttribute
    private String fieldKey;
    @XmlAttribute
    private String projectKey;
    @XmlAttribute(name = "issueTypeId")
    private String issueTypeId;
    @XmlAttribute(name = "newOption")
    private String newOption;
    @XmlElement(name = "fieldName")
    private String fieldName;
    @XmlElement(name = "projectName")
    private String projectName;
    @XmlElement(name = "fieldConfigName")
    private String fieldConfigName;
    @XmlElementWrapper(name = "fieldOptions")
    @XmlElement(name = "option")
    private String[] fieldOptions;
    @XmlElement(name = "result")
    private String result;
    private static final Logger logger = LoggerFactory.
            getLogger(FieldOptionsXML.class.getName());


    public FieldOptionsXML() {
        logger.info("starting FieldOptionsXML instance construction");
    }

    /**
     * constructor repacks some transport object fields to xml
     * @param fieldOptions - transport object
     */
    public FieldOptionsXML(FieldOptions fieldOptions) {
        logger.info("packing response to XML...");
        fieldName = fieldOptions.getFieldName();
        projectName = fieldOptions.getProjectName();
        issueTypeId = fieldOptions.getIssueTypeId();
        fieldConfigName = fieldOptions.getFieldConfigName();
        newOption = fieldOptions.getNewOption();
        // fieldOptionsString = mutableOptionsList.getFieldOptionsString();
        this.fieldOptions = fieldOptions.getFieldOptionsArr();
        fieldKey = fieldOptions.getFieldKey();
        projectKey = fieldOptions.getProjectKey();
        result = Boolean.toString(fieldOptions.isOptionAdded());
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

    public String getFieldName() {
        return fieldName;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getFieldConfigName() {
        return fieldConfigName;
    }

    public String[] getFieldOptions() {
        return Arrays.copyOf(fieldOptions, fieldOptions.length);
    }

    public String getResult() {
        return result;
    }
}
