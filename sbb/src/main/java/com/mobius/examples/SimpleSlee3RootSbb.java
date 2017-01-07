package com.mobius.examples;

import org.mobicents.slee.SbbContextExt;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.slee.*;
import javax.slee.facilities.*;
import javax.slee.serviceactivity.ServiceActivity;

public abstract class SimpleSlee3RootSbb implements Sbb, SimpleSlee3Root {
	
	private Tracer logger;
	private TimerFacility timerFacility;
	
	// TODO: Perform further operations if required in these methods.
	public void setSbbContext(SbbContext context) {
		this.sbbContext = (SbbContextExt) context;
		this.logger = sbbContext.getTracer(SimpleSlee3RootSbb.class.getSimpleName());
		try {
			Context ctx = (Context) new InitialContext();
			logger.info(">>>>>> Lookup for TimerFacility");
			timerFacility = (TimerFacility) ctx.lookup(TimerFacility.JNDI_NAME);
		} catch (Exception ne) {
			logger.severe("Could not set SBB context:", ne);
		}
	}
	
	public void unsetSbbContext() { this.sbbContext = null; }

	// TODO: Implement the lifecycle methods if required
	public void sbbCreate() throws javax.slee.CreateException {}
	public void sbbPostCreate() throws javax.slee.CreateException {}
	public void sbbActivate() {
		
	}
	public void sbbPassivate() {}
	public void sbbRemove() {}
	public void sbbLoad() {}
	public void sbbStore() {}
	public void sbbExceptionThrown(Exception exception, Object event, ActivityContextInterface activity) {}
	public void sbbRolledBack(RolledBackContext context) {}
	
	/**
	 * Convenience method to retrieve the SbbContext object stored in setSbbContext.
	 * 
	 * TODO: If your SBB doesn't require the SbbContext object you may remove this 
	 * method, the sbbContext variable and the variable assignment in setSbbContext().
	 *
	 * @return this SBB's SbbContext object
	 */
	
	protected SbbContextExt getSbbContext() {
		return sbbContext;
	}

	private SbbContextExt sbbContext; // This SBB's SbbContext

	private void setTimer(ActivityContextInterface ac) {
		logger.warning(">>>>>> setTimer");
		TimerOptions options = new TimerOptions();
		//options.setPersistent(true);

		// Set the timer on ACI
		TimerID timerID = this.timerFacility.setTimer(ac, null, System.currentTimeMillis() + 10000, options);

		logger.warning(">>>>>> setTimerID: "+timerID);
		this.setTimerID(timerID);
	}

	private void cancelTimer() {
		logger.warning(">>>>>> cancelTimer");
		if (this.getTimerID() != null) {
			TimerID timerID = this.getTimerID();
			logger.warning(">>>>>> getTimerID: "+timerID);
			timerFacility.cancelTimer(timerID);
		}
	}
	
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci/*, EventContext eventContext*/) {
		logger.warning(">>>>>> onTimerEvent");
		//System.out.println(">>>>>> onTimerEvent");

		this.cancelTimer();

		logger.warning(">>>>>> detach from aci: "+aci);
		//System.out.println(">>>>>> detach from aci: "+aci);
		aci.detach(this.getSbbContext().getSbbLocalObject());
	}
	
	public abstract void fireTimerEvent(TimerEvent event, ActivityContextInterface aci, Address address);
	
	public void onServiceStartedEvent(javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci/*, EventContext eventContext*/) {
		logger.warning(">>>>>> onServiceStartedEvent");
		this.setTimer(aci);
	}

	public void onActivityEndEvent(javax.slee.ActivityEndEvent event, ActivityContextInterface aci) {
		logger.warning(">>>>>> onActivityEndEvent");
		if (aci.getActivity() instanceof ServiceActivity) {
			ServiceActivity sa = (ServiceActivity) aci.getActivity();

		}
	}

	// 'timerID' CMP field setter
	public abstract void setTimerID(TimerID value);

	// 'timerID' CMP field getter
	public abstract TimerID getTimerID();

}
