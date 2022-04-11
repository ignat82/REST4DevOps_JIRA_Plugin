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
    private String customfieldId;
    @XmlAttribute
    private String projectKey;

    public CfOptChangeModel() {
    }

    public CfOptChangeModel(String customfieldId, String projectKey
            , String[] contextAndOptions) {
        this.fieldName = contextAndOptions[0];
        this.projectName = contextAndOptions[1];
        this.fieldConfigurationScheme = contextAndOptions[2];
        this.fieldConfiguration = contextAndOptions[3];
        this.fieldOptionsString = contextAndOptions[4];
        this.customfieldId = customfieldId;
        this.projectKey = projectKey;
    }

    public String getOptStr() {
        return fieldOptionsString;
    }

    public void setOptStr(String optStr) {
        this.fieldOptionsString = optStr;
    }

    public String getCustomfieldId() {
        return customfieldId;
    }

    public void setCustomfieldId(String customfieldId) {
        this.customfieldId = customfieldId;
    }


}
