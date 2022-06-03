package HCBplugins.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public PackingResponseToXML(MutableOptionsObject moo) {
        Logger logger = LoggerFactory.getLogger(PackingResponseToXML.class);
        logger.info("packing response to XML...");
        fieldName = moo.getFieldName();
        projectName = moo.getProjectName();
        fieldConfigName = moo.getFieldConfigName();
        newOption = moo.getNewOption();
        // fieldOptionsString = mutableOptionsList.getFieldOptionsString();
        fieldOptions = moo.getFieldOptionsArr();
        fieldKey = moo.getFieldKey();
        projectKey = moo.getProjectKey();
        result = Boolean.toString(moo.isOptionAdded());
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
