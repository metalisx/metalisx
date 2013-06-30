package org.metalisx.common.rest.converter;

import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.Limit;
import org.metalisx.common.rest.utils.RestServiceUtils;


public class DataTableFilterDtoToQueryLimitConverter {

    public static Limit getQueryLimitFromQueryParam(ContextDto contextDto) {
        String displayStart = String.valueOf(contextDto.getLimit().getStart());
        String displayLength = String.valueOf(contextDto.getLimit().getCount());
        Limit queryLimit = new Limit();
        if (!RestServiceUtils.isBlank(displayStart)) {
            queryLimit.setStart(Integer.valueOf(displayStart));
        } else {
            queryLimit.setStart(0);
        }
        if (!RestServiceUtils.isBlank(displayLength)) {
            queryLimit.setCount(Integer.valueOf(displayLength));
        } else {
            queryLimit.setCount(10);
        }
        return queryLimit;
    }

}
