package ru.homecredit.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
public final class PluginSettingsXML {
    private static final Logger       logger = LoggerFactory.
            getLogger(PluginSettingsXML.class.getName());
    @XmlElementWrapper(name = "editableFields")
    @XmlElement(name = "field")
    private              List<String> editableFields;

    public PluginSettingsXML() {
        logger.info("starting PluginSettingsXML instance construction");
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
