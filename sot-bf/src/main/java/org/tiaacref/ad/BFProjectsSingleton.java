package org.tiaacref.ad;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import com.buildforge.services.client.api.APIClientConnection;
import com.buildforge.services.client.dbo.Project;

/**
 * Singleton class to fetch all project names and all unique project names (i.e without versions)
 * only once from BF so as to avoid multiple calls to BF API.
 */
public class BFProjectsSingleton 
{

	private static String[] allProjects = null;
	private static String[] allUniqueProjects = null;

	protected BFProjectsSingleton() 
	{
	      // Exists only to defeat instantiation.
	}
	
	public static boolean isSOTProjectName(String name)
	{
		boolean retValue = false;
		String[] parts = name.split(" ");
		if(parts.length > 1 && parts[parts.length-1].matches("([\\d\\.]+\\.)*(\\d+)$"))
		{
			retValue = true;
		}
		else
		{
//    		Logger.getLogger(BFProjectsSingleton.class).debug(name + " is not a valid SOT project name");
		}
		return retValue;
	}
	
	private static void initialize(APIClientConnection conn)
	{
        synchronized(BFProjectsSingleton.class) 
        {
            if(allUniqueProjects == null) 
            {
	           	ArrayList<String> uniqueProjectsList = new ArrayList<String>();
	           	ArrayList<String> allProjectsList = new ArrayList<String>();
               try
               {
               	String projectName = null;
       	        List<Project> ProjectList = Project.findAll(conn);
           		Logger.getLogger(BFProjectsSingleton.class).debug(ProjectList.size() + " projects found in Build Forge");


           		for (Project p : ProjectList)
       	        {
           			if(isSOTProjectName(p.getName()))
           			{
           				allProjectsList.add(p.getName());
           			}

           			if(p.getName().matches(".*\\d$"))
       	        	{        	        	
	        	        	if(null != projectName && p.getName().startsWith(projectName)) continue;
	        	        	projectName = p.getName();
	                		projectName = projectName.replaceAll(" \\d.*", "");
	                		uniqueProjectsList.add(projectName);
       	        	}
       	        }

           		allUniqueProjects = uniqueProjectsList.toArray(new String[0]);

       	        allProjects = sortAllProjects(allProjectsList);

               }
	       	    catch(Exception e)
	       	    {
	       	    	Logger.getLogger(BFProjectsSingleton.class).error("", e);
	       	    }
            }
        }		
	}

	public static String[] getAllProjects(APIClientConnection conn)
	{
		if(null == allProjects)
		{
			initialize(conn);
		}
		return allProjects;
	}

	/**
	 * returns the unique project name of any project group which has one or more versions.
	 * BRMS Mutual Funds Rules 1.0
	 * BRMS Mutual Funds Rules 1.1
     * becomes 'BRMS Mutual Funds Rules' 
	 */
	public static String[] getAllUniqueProjects(APIClientConnection conn) 
	{
      if(allUniqueProjects == null) 
      {
			initialize(conn);
      }
      return allUniqueProjects;
	}

	/**
	 * Fully sorts all projects according to their version.
	 * NOTE: According to this function,  2.10.1 > 2.1.  This is not true according to semantic versioning (www.semver.org).

		Atom Framework 2.9
		Atom Framework 2.9.1
		Atom Framework 3.0
		Atom Framework 3.0.1
		Atom Framework 3.1
		Atom Profile 0.0
		Atom Profile 2.0
		Atom Profile 2.1
		Atom Profile 2.10
		Atom Profile 2.10.1
		Atom Profile 2.11
	 */
	private static String[] sortAllProjects(ArrayList<String> allProjects)
	{
		Logger.getLogger(SonarOverTime.class).debug("Sorting " + allProjects.size() + " projects");

		String lastName = null;
		int versionDigits = 0;
    	ArrayList<String> namesToSort = new ArrayList<String>();
		ArrayList<String> sortedProjects = new ArrayList<String>();

		for (String pName : allProjects)
		{
			// split projectName into name / version
			String[] parts = pName.split(" ");
			String name = pName.replaceAll(" \\d.*", "");
			String version = parts[parts.length-1];
			versionDigits = computeVersionSize(versionDigits, version);
	
			if(null == lastName || name.equals(lastName))
			{
				namesToSort.add(pName);
				lastName = name;
			}
			else
			{
				VERSION_COMPARATOR vComp = new VERSION_COMPARATOR();
				vComp.versionSize = versionDigits;
				Collections.sort(namesToSort, vComp);
				
				sortedProjects.addAll(namesToSort);
				namesToSort.clear();
				namesToSort.add(pName);
				lastName = name;
				versionDigits = 0;
			}
			
			// if(null == lastName)
			//   lastName = name
			//   add name + version to array namesToSort
			// else if name.equals(lastName)
			//   lastName = name 
			//   add name + version to array namesToSort
			// else
			//   sort(namesToSort)
			//   lastName = name
			//   namesToSort = null
			//   add name + version to array namesToSort

//			Logger.getLogger(SonarOverTime.class).debug(pName + " " + getVersionScore(version));			
		}
		
		return sortedProjects.toArray(new String[0]);		
	}

	/**
	 * return the greater of [number of digits in new version, versionSize] 
	 */
	public static int computeVersionSize(int versionSize, String version)
	{
		int retValue = versionSize;
    	String [] versionParts = version.split("\\.");
    	if(versionParts.length > versionSize)
    	{
    		retValue = versionParts.length;
    	}
		return retValue;
	}

	/**
	 * 
	 *
	 */
	public static class VERSION_COMPARATOR implements Comparator<String>
	{
		public int versionSize = 0;
	    public int compare(String arg0, String arg1) 
	    {
	    	return getVersionScore(arg0, versionSize) - getVersionScore(arg1, versionSize);
	    }
	};

	/**
	 * versionSize is to solve the problem that of computing a score based on the largest number of digits within a similar group:
	 	Atom Framework 2.0
		Atom Framework 2.1
		Atom Framework 2.11
		Atom Framework 2.12
		Atom Framework 2.2
		Atom Framework 2.2.1
	 */
	private static int getVersionScore(String projectName, int versionSize)
	{
		int retValue = 0;
		String[] parts = projectName.split(" ");
		String name = projectName.replaceAll(" \\d.*", "");
		String version = parts[parts.length-1];
    	String [] versionParts = version.split("\\.");
    	if(versionParts.length == 0)
    	{
    		retValue = Integer.parseInt(version);
    	}
    	else
    	{
	    	for(int i=0; i<versionParts.length; i++)
	    	{
	    		retValue += Integer.parseInt(versionParts[i]) * Math.pow(10, (versionSize-i-1));
	    	}
    	}    	
    	return retValue;
	}
	
}
