package ch.swissbytes.module.shared.utils;

public class OrderBy {
    public static final String ASCENDING = "ASCENDING";
    public static final String DESCENDING = "DESCENDING";

    private String sortField;
    private String sortOrder;

    public OrderBy() {
    }

    public OrderBy(String sortField, String sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }
}
