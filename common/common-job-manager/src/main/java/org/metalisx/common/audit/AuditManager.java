package org.metalisx.common.audit;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Asynchronous;
import javax.enterprise.event.Observes;
import org.apache.log4j.Logger;

/**
 * Audit data manager. Is used to listen for events of a {@link AuditData}
 * implementation so the audit data can be saved in the audit list of the
 * manager for later retrieval. Retrieval can be done as-is or as an ordered
 * list on the date of the audit data.
 *
 * How to use:
 * <ul>
 * <li>create a POJO sub class of {@link Auditdata}</li>
 * <li>create a sub class of {@link AuditManager} for the POJO and annotate it
 * with @Singleton from the javax.ejb.Singleton package</li>
 * <li>in your code add an javax.enterprise.event.Event for the POJO and fire
 * it</li>
 * <li>use the getAuditList or getSortedAuditList of the manager to retrieve the
 * list of objects fired by the events</li>
 * </ul>
 * 
 * @author Stefan.Oude.Nijhuis
 */
public abstract class AuditManager<T extends AuditData<?>> {

	private static final Logger LOGGER = Logger.getLogger(AuditManager.class.getName());

	private ArrayList<T> auditList = new ArrayList<>();

	private Date dateStarted;

	@PostConstruct
	public void postConstruct() {
		dateStarted = new Date();
		LOGGER.info("AuditManager started : date started = " + dateStarted);
	}

	@PreDestroy
	public void preDestroy() {
		LOGGER.info("AuditManager destroyed : date started = " + dateStarted);
	}

	@Asynchronous
	public void add(@Observes T auditData) {
		this.auditList.add(auditData);
	}

	public ArrayList<T> getAuditList() {
		return this.auditList;
	}

	public void reset() {
		this.auditList = new ArrayList<>();
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<T> getSortedAuditList() {
		// This will return a shallow copy, keeping the original list in this
		// class unchanged.
		ArrayList<T> clone = (ArrayList<T>) auditList.clone();
		clone.sort((T o1, T o2) -> o1.getDate().compareTo(o2.getDate()));
		return this.auditList;
	}

    public void logAuditList() {
        logAuditList(getAuditList());
    }
    
    public void logSortedAuditList() {
        logAuditList(getSortedAuditList());
    }
    
    private void logAuditList(ArrayList<T> auditList) {
        LOGGER.info("============== logAuditList : start ==============");
        auditList.forEach((auditData) -> LOGGER.info(auditData.toString()));
        // First, last and duration are always determined with the sorted audit list.
        ArrayList<T> auditListSorted = getSortedAuditList();
        AuditData<?> firstAuditData = auditListSorted.get(0);
        AuditData<?> lastAuditData = auditListSorted.get(auditListSorted.size() - 1);
        LOGGER.info("First (sorted) : " + firstAuditData);
        LOGGER.info("Last (sorted) : " + lastAuditData);
        LOGGER.info("Duration: " + Duration.between(firstAuditData.getDate(), lastAuditData.getDate()));
        LOGGER.info("============== logAuditList : end ==============");
    }

}
