package org.tiaacref.ad;

import java.util.HashMap;
import java.util.Map;

public class SOTProjectData
{
	public String name = null; // bundle name, i.e Unified Desktop Bulk Email
	public String parentBundle = null; // parent bundle, i.e Unified Desktop

	public int startTime;
	public boolean isNewBuild = false;
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
		return retValue;
	}
}
