package tgtools.tasklibrary.entity;

import java.util.Comparator;

public class ColumnInfoComparator implements Comparator<ColumnInfo> {
    @Override
    public int compare(ColumnInfo paramT1, ColumnInfo paramT2) {
        return new Integer(paramT1.getIndex()).compareTo(paramT2.getIndex());
    }
}