package org.metalisx.common.audit;

import java.time.LocalDateTime;

/**
 * Audit data container for object of type <code>T</code>.
 *
 * @author Stefan.Oude.Nijhuis
 */
public abstract class AuditData<T> {

	public LocalDateTime date;

	public T data;

	public AuditData(LocalDateTime date, T data) {
		this.date = date;
		this.data = data;
	}

	public LocalDateTime getDate() {
		return this.date;
	}

	public T getData() {
		return this.data;
	}

	@Override
	public String toString() {
		return "AuditData{" + "date=" + date + ", data=" + data + '}';
	}

}
