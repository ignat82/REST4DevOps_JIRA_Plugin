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

    //mol.fKey, mol.fName, mol.pKey, mol.pName, mol.fConfName, mol.newOpt, mol.optList
    public CfOptChangeModel(String fKey, String fName, String pKey, String pName
            , String fcName, String nOpt, String fOptStr) {
        fieldName = fName;
        projectName = pName;
        fieldConfiguration = fcName;
        newOption = nOpt;
        fieldOptionsString = fOptStr;
        customfieldKey = fKey;
        projectKey = pKey;
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
