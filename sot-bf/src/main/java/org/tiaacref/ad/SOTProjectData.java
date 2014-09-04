package org.tiaacref.ad;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class SOTProjectData
{
	public String name = null; // bundle name, i.e Unified Desktop Bulk Email
	public String parentBundle = null; // parent bundle, i.e Unified Desktop

	public int startTime;
	public boolean isNewBuild = false;
	
	// was the project successfully staged locally for scanning
	public boolean staged = false;

	/**
	 * contains name/value pairs from the BF build step log
	    BF_AGENT_VERSION=7.1.3.2-0-0012
		BF_AGENT_PLATFORM=linux 2.6.32-358.el6.x86_64 #1 SMP Tue Jan 29 11:47:41 EST 2013
		BF_D=140813
		BF_T=152156
		BF_J=224
		BF_W=3
		BF_PROJECTNAME=Unified Desktop My BoB 2.9
		BF_USER=ser_bfbuild
		BF_USER_LOGIN=ser_bfbuild
		BF_USER_EMAIL=
		BF_CLASS=RTC Build
	 */
	Map<String, String> bfVars = new HashMap<String, String>();
	
	public boolean isNewBuild(long lastScanTime)
	{

		boolean retValue = false;
		if(startTime > lastScanTime)
		{
			retValue = true;
		}
		else
		{
			retValue = false;
		}
		Logger.getLogger(SOTProjectData.class).debug(name + " startTime " + startTime + "-----------" + "lastScanTime " +  lastScanTime + "=" + (retValue ? "NEW BUILD" : "old build"));
		return retValue;
	}
}
