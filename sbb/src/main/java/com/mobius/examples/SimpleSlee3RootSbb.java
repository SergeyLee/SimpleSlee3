package com.mobius.examples;

import java.util.Arrays;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.slee.*;
import javax.slee.facilities.ActivityContextNamingFacility;
import javax.slee.facilities.FacilityException;
import javax.slee.facilities.NameAlreadyBoundException;
import javax.slee.facilities.TimerEvent;
import javax.slee.facilities.TimerFacility;
import javax.slee.facilities.TimerID;
import javax.slee.facilities.TimerOptions;
import javax.slee.facilities.Tracer;

import org.mobicents.slee.*;

public abstract class SimpleSlee3RootSbb implements Sbb, SimpleSlee3Root {
	
	private Tracer logger;
	private TimerFacility timerFacility;
	
	// TODO: Perform further operations if required in these methods.
	public void setSbbContext(SbbContext context) {
		this.sbbContext = (SbbContextExt) context;
		this.logger = sbbContext.getTracer(SimpleSlee3RootSbb.class.getSimpleName());
		try {
			Context ctx = (Context) new InitialContext();
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
		TimerOptions options = new TimerOptions();
		//options.setPersistent(true);

		// Set the timer on ACI
		TimerID timerID = this.timerFacility.setTimer(ac, null, System.currentTimeMillis() + 10000, options);
		this.setTimerID(timerID);
	}

	private void cancelTimer() {
		if (this.getTimerID() != null) {
			timerFacility.cancelTimer(this.getTimerID());
		}
	}
	
	public void onTimerEvent(TimerEvent event, ActivityContextInterface aci/*, EventContext eventContext*/) {
		logger.info("onTimerEvent");
		this.cancelTimer();
	}
	
	public abstract void fireTimerEvent(TimerEvent event, ActivityContextInterface aci, Address address);
	
	public void onServiceStartedEvent(javax.slee.serviceactivity.ServiceStartedEvent event, ActivityContextInterface aci/*, EventContext eventContext*/) {
		logger.info("onServiceStartedEvent");
		this.setTimer(aci);
	}
	
	// 'timerID' CMP field setter
	public abstract void setTimerID(TimerID value);

	// 'timerID' CMP field getter
	public abstract TimerID getTimerID();

}
