package HCBplugins.rest;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public class CfOptChangeModel {

    @XmlElement(name = "fieldName")
    private String fieldName;
    @XmlElement(name = "projectName")
    private String projectName;
    @XmlElement(name = "fieldConfigurationScheme")
    private String fieldConfigurationScheme;
    @XmlElement(name = "fieldConfiguration")
    private String fieldConfiguration;
    @XmlElement(name = "optionsString")
    private String fieldOptionsString;
    @XmlAttribute
    private String customfieldKey;
    @XmlAttribute
    private String projectKey;

    public CfOptChangeModel() {
    }

    public CfOptChangeModel(String fName, String pName, String fcsName
            , String fcName, String fOptStr, String fKey, String pKey) {
        this.fieldName = fName;
        this.projectName = pName;
        this.fieldConfigurationScheme = fcsName;
        this.fieldConfiguration = fcName;
        this.fieldOptionsString = fOptStr;
        this.customfieldKey = fKey;
        this.projectKey = pKey;
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
