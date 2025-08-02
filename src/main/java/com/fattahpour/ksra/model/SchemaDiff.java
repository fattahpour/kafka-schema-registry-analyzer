package com.fattahpour.ksra.model;

import java.util.List;

/**
 * Simple representation of differences between two schema versions.
 */
public class SchemaDiff {

    private final List<String> addedFields;
    private final List<String> removedFields;
    private final List<String> changedFields;

    public SchemaDiff(List<String> addedFields, List<String> removedFields, List<String> changedFields) {
        this.addedFields = addedFields;
        this.removedFields = removedFields;
        this.changedFields = changedFields;
    }

    public List<String> getAddedFields() {
        return addedFields;
    }

    public List<String> getRemovedFields() {
        return removedFields;
    }

    public List<String> getChangedFields() {
        return changedFields;
    }
}
