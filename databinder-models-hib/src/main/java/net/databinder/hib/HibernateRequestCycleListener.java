package net.databinder.hib;

/**
 * Request cycle listener that should be notified on the first use of a data session.
 */
public interface HibernateRequestCycleListener {
	public void dataSessionRequested(Object key);
}
