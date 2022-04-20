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
        this.setFieldName(mutableOptionsList.fieldName);
        this.setProjectName(mutableOptionsList.projectName);
        this.setFieldConfigName(mutableOptionsList.fieldConfigName);
        this.setNewOption(mutableOptionsList.newOption);
        this.setFieldOptionsString(mutableOptionsList.fieldOptionsString);
        this.setFieldKey(mutableOptionsList.fieldKey);
        this.setProjectKey(mutableOptionsList.projectKey);
    }

    public String getFieldKey() {
        return fieldKey;
    }

    public void setFieldKey(String fieldKey) {
        this.fieldKey = fieldKey;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getNewOption() {
        return newOption;
    }

    public void setNewOption(String newOption) {
        this.newOption = newOption;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getFieldConfigName() {
        return fieldConfigName;
    }

    public void setFieldConfigName(String fieldConfigName) {
        this.fieldConfigName = fieldConfigName;
    }

    public String getFieldOptionsString() {
        return fieldOptionsString;
    }

    public void setFieldOptionsString(String fieldOptionsString) {
        this.fieldOptionsString = fieldOptionsString;
    }
}
