package HCBplugins.rest;

import java.util.Arrays;


public class PluginSettings {
    private String[] fieldKeys;
    PluginSettings() {
    }
    public String[] getFieldKeys() {
        return (fieldKeys != null)
                ? Arrays.copyOf(fieldKeys, fieldKeys.length)
                : null;
    }
    public void setFieldKeys(String[] fieldKeys) {
        this.fieldKeys = (fieldKeys != null)
            ? Arrays.copyOf(fieldKeys, fieldKeys.length)
            : null;
    }

}
