package HCBplugins.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.util.Arrays;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Config {
    private static final Logger logger = LoggerFactory.
            getLogger(Config.class.getName());
    @XmlElementWrapper(name = "editableFields")
    @XmlElement(name = "field")
    private String[] editableFields;

    public Config() {
        logger.info("starting Config instance construction");
    }

    public String[] getEditableFields()
    {
        return Arrays.copyOf(editableFields, editableFields.length);
    }

    public void setEditableFields(String[] editableFields)
    {
        this.editableFields = Arrays.copyOf(editableFields, editableFields.length);
    }

}
