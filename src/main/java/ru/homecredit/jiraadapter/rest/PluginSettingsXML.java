package ru.homecredit.jiraadapter.rest;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
public final class PluginSettingsXML {
    @XmlElementWrapper(name = "editableFields")
    @XmlElement(name = "field")
    private              List<String> editableFields;

    public PluginSettingsXML() {
        log.info("starting PluginSettingsXML instance construction");
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
