package org.tiaacref.ad;

import java.util.HashMap;
import java.util.Map;

public class SOTProject
{
	public String name = null;
	public int startTime;
	public boolean isNewBuild = false;
	Map bfVars = new HashMap();
	
	
	public boolean isNewBuild()
	{
		boolean retValue = true;
		// if this build occurred after right now, then we need its results
//		if(startTime > currentTime)
		if(true)
		{
			retValue=true;
		}
		else
		{
			retValue=false;
		}
		return retValue;
	}
}
