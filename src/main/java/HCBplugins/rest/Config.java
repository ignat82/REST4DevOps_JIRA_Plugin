package HCBplugins.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public final class Config {
    private static final Logger       logger = LoggerFactory.
            getLogger(Config.class.getName());
    @XmlElementWrapper(name = "editableFields")
    @XmlElement(name = "field")
    private              List<String> editableFields;

    public Config() {
        logger.info("starting Config instance construction");
    }

    public List<String> getEditableFields()
    {
        return new ArrayList<>(editableFields);
    }

    public void setEditableFields(List<String> editableFields)
    {
        this.editableFields = new ArrayList<>(editableFields);
    }

}
