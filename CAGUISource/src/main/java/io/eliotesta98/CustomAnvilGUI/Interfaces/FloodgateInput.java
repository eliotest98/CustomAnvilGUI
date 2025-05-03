package io.eliotesta98.CustomAnvilGUI.Interfaces;

public class FloodgateInput {

    private String type;
    private String label;
    private String placeholder;
    private String defaultText;

    public FloodgateInput(String type, String label, String placeholder, String defaultText) {
        this.type = type;
        this.label = label;
        this.placeholder = placeholder;
        this.defaultText = defaultText;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    @Override
    public String toString() {
        return "FloodgateInput{" +
                "type='" + type + '\'' +
                ", label='" + label + '\'' +
                ", placeholder='" + placeholder + '\'' +
                ", defaultText='" + defaultText + '\'' +
                '}';
    }
}
