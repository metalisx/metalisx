package org.metalisx.common.audit;

import java.time.LocalDateTime;

/**
 * Implementation of {@link AuditData} holding a String.
 * 
 * @author Stefan.Oude.Nijhuis
 */
public class StringAuditData extends AuditData<String> {

	public StringAuditData(LocalDateTime date, String data) {
		super(date, data);
	}

}