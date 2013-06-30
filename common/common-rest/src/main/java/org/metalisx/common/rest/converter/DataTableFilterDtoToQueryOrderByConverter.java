package org.metalisx.common.rest.converter;

import java.util.Map;

import org.metalisx.common.domain.dto.ContextDto;
import org.metalisx.common.domain.dto.OrderBy;
import org.metalisx.common.rest.service.RestException;
import org.metalisx.common.rest.utils.RestServiceUtils;


public class DataTableFilterDtoToQueryOrderByConverter {

	private static final String ASC = "asc";
	private static final String DESC = "desc";
	
    public static OrderBy getQueryOrderByFromQueryParam(ContextDto contextDto,
            Map<String, String> columnMap) {
        String sortColumn = contextDto.getOrderBy().getColumnName();
        String sortOrder = contextDto.getOrderBy().getColumnDirection();
        OrderBy orderBy = new OrderBy();
        if (!RestServiceUtils.isBlank(sortColumn)) {
            orderBy.setColumnName(columnMap.get(sortColumn));
            if (orderBy.getColumnName() == null) {
                throw new RestException("Invalid sort column index " + sortColumn);
            }
        } else {
            orderBy.setColumnName(columnMap.get("0"));
        }
        if (!RestServiceUtils.isBlank(sortOrder)) {
            if (ASC.equalsIgnoreCase(sortOrder)) {
                orderBy.setColumnDirection(ASC);
            }
            if (DESC.equalsIgnoreCase(sortOrder)) {
                orderBy.setColumnDirection(DESC);
            }
            if (orderBy.getColumnDirection() == null) {
                throw new RestException("Invalid sort order " + sortOrder);
            }
        } else {
            orderBy.setColumnDirection(ASC);
        }
        return orderBy;
    }

}
