package ru.homecredit.rest;

import ru.homecredit.DTO.FieldParameters;
import ru.homecredit.DTO.RequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.homecredit.DTO.FieldOptions;

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
    @XmlAttribute(name = "action")
    private String action;
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
        RequestParameters requestParameters = fieldOptions.getRequestParameters();
        fieldKey = requestParameters.getFieldKey();
        projectKey = requestParameters.getProjectKey();
        issueTypeId = requestParameters.getIssueTypeId();
        newOption = requestParameters.getNewOption();
        action = requestParameters.getAction();

        FieldParameters fieldParameters = fieldOptions.getFieldParameters();
        fieldName = fieldParameters.getFieldName();
        projectName = fieldParameters.getProjectName();
        fieldConfigName = fieldParameters.getFieldConfigName();
        // fieldOptionsString = mutableOptionsList.getFieldOptionsString();
        this.fieldOptions = fieldOptions.getFieldOptionsArr();
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
