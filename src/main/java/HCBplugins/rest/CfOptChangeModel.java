package HCBplugins.rest;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class CfOptChangeModel {

    @XmlAttribute
    private String customfieldKey;
    @XmlAttribute
    private String projectKey;
    @XmlAttribute(name = "newOption")
    private String newOption;
    @XmlElement(name = "fieldName")
    private String fieldName;
    @XmlElement(name = "projectName")
    private String projectName;
    @XmlElement(name = "fieldConfiguration")
    private String fieldConfiguration;
    @XmlElement(name = "optionsString")
    private String fieldOptionsString;


    public CfOptChangeModel() {
    }

    public CfOptChangeModel(String customfieldKey, String fieldName
            , String projectKey, String projectName, String fieldConfiguration
            , String newOption, String fieldOptionsString) {
        this.fieldName = fieldName;
        this.projectName = projectName;
        this.fieldConfiguration = fieldConfiguration;
        this.newOption = newOption;
        this.fieldOptionsString = fieldOptionsString;
        this.customfieldKey = customfieldKey;
        this.projectKey = projectKey;
    }

    public String getOptStr() {
        return fieldOptionsString;
    }

    public void setOptStr(String optStr) {
        this.fieldOptionsString = optStr;
    }

    public String getCustomfieldKey() {
        return customfieldKey;
    }

    public void setCustomfieldId(String customfieldId) {
        this.customfieldKey = customfieldId;
    }


}
