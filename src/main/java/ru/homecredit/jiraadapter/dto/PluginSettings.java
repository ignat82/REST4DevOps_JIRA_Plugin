package ru.homecredit.jiraadapter.dto;

import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

// XML markup is necessary for JSON serialization
@XmlRootElement(name = "response")
@XmlAccessorType(XmlAccessType.FIELD)
@Slf4j
public final class PluginSettings {
    @XmlElementWrapper(name = "editableFields")
    @XmlElement(name = "field")
    private List<String> editableFields;

    public PluginSettings() {
        log.info("starting PluginSettings instance construction");
    }

    public List<String> getEditableFields()
    {
        return new ArrayList<>(editableFields);
    }

    public String getCommaSeparatedields() {
        StringBuilder stringBuilder = new StringBuilder();
        for (String editableField : editableFields) {
            stringBuilder.append(editableField).append(",");
        }
        if (stringBuilder.length() != 0) {
            stringBuilder.setLength(stringBuilder.length() - 1);
        }
        return stringBuilder.toString();
    }

    public void setEditableFields(List<String> editableFields)
    {
        this.editableFields = new ArrayList<>(editableFields);
    }

}
