package HCBplugins.rest;

import javax.xml.bind.annotation.*;
import java.util.Arrays;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class PackingResponseToXML {

    @XmlAttribute
    private String fieldKey;
    @XmlAttribute
    private String projectKey;
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


    public PackingResponseToXML() {
    }

    public PackingResponseToXML(MutableOptionsList mutableOptionsList) {
        fieldName = mutableOptionsList.getFieldName();
        projectName = mutableOptionsList.getProjectName();
        fieldConfigName = mutableOptionsList.getFieldConfigName();
        newOption = mutableOptionsList.getNewOption();
        // fieldOptionsString = mutableOptionsList.getFieldOptionsString();
        fieldOptions = mutableOptionsList.getFieldOptionsArr();
        fieldKey = mutableOptionsList.getFieldKey();
        projectKey = mutableOptionsList.getProjectKey();
        result = Boolean.toString(mutableOptionsList.getResult());

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
