package net.databinder.hib;

/**
 * Interface that should be notified on the first use of a data (hibernate) session.
 */
public interface HibernateSessionStarter {
	public void dataSessionRequested(Object key);
}
