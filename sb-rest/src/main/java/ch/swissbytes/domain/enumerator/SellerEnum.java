package ch.swissbytes.domain.enumerator;

public enum SellerEnum {

    SWISSBYTES("SWISSBYTES"),
    UBIDATA("UBIDATA");

    private String label;


    SellerEnum(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
