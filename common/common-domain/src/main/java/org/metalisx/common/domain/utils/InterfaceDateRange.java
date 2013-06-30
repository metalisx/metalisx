package org.metalisx.common.domain.utils;

import java.util.Date;

public interface InterfaceDateRange {

    Date getStartDate();
    
    void setStartDate(Date startDate);

    Date getEndDate();
    
    void setEndDate(Date endDate);

    String getRange();
    
    void setRange(String range);
    
}
