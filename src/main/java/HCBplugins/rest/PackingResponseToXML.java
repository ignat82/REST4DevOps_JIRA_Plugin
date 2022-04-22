package HCBplugins.rest;

import javax.xml.bind.annotation.*;

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
    @XmlElement(name = "fieldOptionsString")
    private String fieldOptionsString;


    public PackingResponseToXML() {
    }

    public PackingResponseToXML(MutableOptionsList mutableOptionsList) {
        this.fieldName = mutableOptionsList.getFieldName();
        this.projectName = mutableOptionsList.getProjectName();
        this.fieldConfigName = mutableOptionsList.getFieldConfigName();
        this.newOption = mutableOptionsList.getNewOption();
        this.fieldOptionsString = mutableOptionsList.getFieldOptionsString();
        this.fieldKey = mutableOptionsList.getFieldKey();
        this.projectKey = mutableOptionsList.getProjectKey();
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

    public String getFieldOptionsString() {
        return fieldOptionsString;
    }

}
