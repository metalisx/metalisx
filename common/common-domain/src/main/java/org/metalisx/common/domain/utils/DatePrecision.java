package org.metalisx.common.domain.utils;


public enum DatePrecision {

    YEAR, MONTH, DAY, HOUR, MINUTE, SECOND, MILLISECOND;

    public DatePrecision getNext() {
        return this.ordinal() == MILLISECOND.ordinal() ? MILLISECOND : values()[this.ordinal() + 1];
    }

}
