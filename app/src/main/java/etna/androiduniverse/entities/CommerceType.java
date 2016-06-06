package etna.androiduniverse.entities;


public class CommerceType {

    private String label;
    private int icon;

    public CommerceType(String label, int icon) {
        this.label = label;
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public int getIcon() {
        return icon;
    }
}
