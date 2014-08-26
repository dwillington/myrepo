package org.tiaacref.ad;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.buildforge.services.client.api.APIClientConnection;
import com.buildforge.services.client.dbo.Build;
import com.buildforge.services.client.dbo.Log;
import com.buildforge.services.client.dbo.Project;
import com.buildforge.services.client.dbo.Result;
import com.buildforge.services.common.dbo.BuildDBO;

/**
 * mvn package exec:java -Dexec.mainClass="org.tiaacref.ad.App"
 */
public class SonarOverTime 
{
	
	public static String ANT_COMMAND_PREFIX = "cmd.exe /C ant -Dpassword=" + App.pass + " ";

	/**
	 * if the last build time for this project is newer than lastScanTime
	 *   stage project
	 *   return true
	 */
	public SOTProjectData getProjectData(APIClientConnection conn, String bundleParent, String project, long lastScanTime)
	{
		SOTProjectData retValue = null;
		
		retValue = getLatestProjectBuildData(conn, project);
		retValue.parentBundle = bundleParent;
		retValue.name = project;
		Logger.getLogger(SonarOverTime.class).debug(retValue.bfVars.get("BF_PROJECTNAME_PHYS"));
		Logger.getLogger(SonarOverTime.class).debug(retValue.bfVars.get("BF_ROOT"));
		Logger.getLogger(SonarOverTime.class).debug(retValue.bfVars.get("BF_HOST"));

		return retValue;
	}

	public void listProjects(APIClientConnection conn)
	{
        try
        {
        	String projectList[] = BFProjectsSingleton.getAllProjects(conn);
	        for (int i=0; i<projectList.length; i++)
	        {
        		Logger.getLogger(SonarOverTime.class).debug(projectList[i]);
	        }
        }
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }
	}
	
	public void stageProject(SOTProjectData sotProject)
	{
		try
		{
			Logger.getLogger(SonarOverTime.class).debug("staging project " + "/Temp" + sotProject.bfVars.get("BF_ROOT"));
			
			File f = new File("/Temp" + sotProject.bfVars.get("BF_ROOT"));
			if (f.exists() && f.isDirectory()) 
			{
				Logger.getLogger(SonarOverTime.class).error(sotProject.bfVars.get("BF_PROJECTNAME_PHYS") + " already staged, skipping...");
			}
			else
			{				
				int exitValue = runAnt(sotProject, "");
				if(exitValue != 0)
				{
					Logger.getLogger(SonarOverTime.class).error("staging project " + sotProject.bfVars.get("BF_PROJECTNAME_PHYS") + " failed");
				}
				else
				{
					sotProject.staged = true;
				}
			}
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }		
	}

	/**
	 * what folder is the bundle in?
	 * sonar-runner
	 */
	public void scanBundle(SOTProjectData sotProject)
	{
		try
		{
			Logger.getLogger(SonarOverTime.class).debug("scanning project " + sotProject.bfVars.get("BF_PROJECTNAME_PHYS"));            
            int exitValue = runAnt(sotProject, "sonar-runner");
			if(exitValue != 0)
			{
				Logger.getLogger(SonarOverTime.class).error("scanning project " + sotProject.bfVars.get("BF_PROJECTNAME_PHYS") + " failed");
			}
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }		
	}

	/**
 	 * sot.project.dir=/bfbuilds/Unified_Desktop_Transactions_5.0/5.0.0.1245
	 * sot.project.name=Unified_Desktop_Transactions_5.0
	 * sot.PROJ_DIR=UnifiedDesktopTransactionsEAR
	 * sot.project.key=fd711283-ab47-489e-85ef-afc6c3322d38
	 */
	public int runAnt(SOTProjectData sotProject, String target) throws IOException
	{
        String command = ANT_COMMAND_PREFIX + 
        		" -Dsot.parent.bundle=\"" + sotProject.parentBundle + "\"" + 
           		" -Dsot.project.name.spaces=\"" + sotProject.name + "\"" +
           	    " -Dsot.project.name=" +  sotProject.bfVars.get("BF_PROJECTNAME_PHYS") + 
        		" -Dsot.project.dir=" + sotProject.bfVars.get("BF_ROOT") + 
        		" -Dsot.PROJ_DIR=" + sotProject.bfVars.get("PROJ_DIR")  + 
        		" -Dsot.project.key=" + BundleManager.getProjectKey(sotProject.name) + " " + target;
        return runCommnad(command);
	}

	public int runCommnad(String command)
	{
		int exitVal = 0;
		try
		{
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec(command);

            StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), "ERROR");            
            StreamGobbler outputGobbler = new  StreamGobbler(proc.getInputStream(), "OUTPUT");
            errorGobbler.start();
            outputGobbler.start();
            // any error???
            exitVal = proc.waitFor();
            Logger.getLogger(SonarOverTime.class).debug("ExitValue: " + exitVal);
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }				
        return exitVal;
	}

	/**
	 * given a projectName, find its last successful build, and populate its data
	 */
	public SOTProjectData getLatestProjectBuildData(APIClientConnection conn, String projectName)
	{
		SOTProjectData retValue = new SOTProjectData();
		Build latestBuild = null;

        try
        {
        	String latestProjectName = getLatestProjectName(conn, projectName);
        	if(!StringUtils.isBlank(latestProjectName))
        	{
        		latestBuild = getLatestBuild(conn, latestProjectName);
            	if(null != latestBuild)
            	{
        			retValue.startTime = latestBuild.getStartTime();
        			//TODO: only populate BFVars if this project should be built
//        			if(retValue.isNewBuild())
        			{
        				populateBFVars(conn, latestBuild, retValue);
        			}
            	}
    			else
    			{
    				Logger.getLogger(SonarOverTime.class).debug("Can't retrieve build for project " + projectName);
    			}
        	}
			else
			{
				Logger.getLogger(SonarOverTime.class).debug("Can't retrieve latest project for " + projectName);
			}
        }
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }
        return retValue;
	}

	/**
	 * populate a Map of of BF_ variables give the build log:
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
	public void populateBFVars(APIClientConnection conn, Build latestBuild, SOTProjectData sotProject)
	{
		try
        {
        	java.util.List<Result> resultList = latestBuild.getResults();
	        for (Result r : resultList)
	        {
		        java.util.List<Log> logList = Log.findByResult(conn, r);
		        for (Log l : logList)
		        {
		        	if(l.getMessageText().startsWith("BF_") || l.getMessageText().startsWith("PROJ_DIR"))
		        	{
		        		String parts[] = l.getMessageText().split("=");
//		        		Logger.getLogger(SonarOverTime.class).debug(l.getMessageText());
		        		if(null != parts && parts.length == 2)
		        		{
		        			if(parts[0].equals("BF_HOST"))
		        			{
		        				parts[1] = parts[1].substring(0, parts[1].indexOf(':'));
		        			}
		        			sotProject.bfVars.put(parts[0], parts[1]);
		        		}
		        	}
		        }
		        // just check the first step result logs
		        break;
	        }
	    }
	    catch(Exception e)
	    {
    		Logger.getLogger(SonarOverTime.class).error("", e);
	    }
	    finally
	    {
	    }
	}

	/**
	 * returns the last BF project name which starts with 'name'.
	 * so if name="abc", and there exists "abc.1.0", "abc.1.1", "abc.1.2"
	 * 		then "abc.1.2" will be returned
	 */
	public String getLatestProjectName(APIClientConnection conn, String name)
	{
		String retValue = null;

        try
        {
        	// TODO: fix so an empty call to getAllUnique doesn't do the work of populating getAll
        	// use static allProjectsList - gets created once; prevent 2 calls to BF to populate whole list
        	// use singleton getAllProjects()
        	BFProjectsSingleton.getAllUniqueProjects(conn);
        	String allProjectList[] = BFProjectsSingleton.getAllProjects(conn);

//	        TODO: use a non lexical sorting algorithm so 3.11 shows up last
//        	MATCHES: Unified Desktop Core 2.4
//        	MATCHES: Unified Desktop Core 2.5
//        	MATCHES: Unified Desktop Core 3.10
//        	MATCHES: Unified Desktop Core 3.11
//        	MATCHES: Unified Desktop Core 3.4
//        	MATCHES: Unified Desktop Core 3.5
//        	MATCHES: Unified Desktop Core 3.6
//        	MATCHES: Unified Desktop Core 3.7
//        	MATCHES: Unified Desktop Core 3.8
//        	MATCHES: Unified Desktop Core 3.9	        

        	for (int i=0; i<allProjectList.length; i++)
	        {
	        	String projectName = allProjectList[i];
	        	if(projectName.startsWith(name))
	        	{
		        	if(projectName.matches(name + " \\d.*"))
		        	{
		        		Logger.getLogger(SonarOverTime.class).debug("MATCHES: " + projectName);
		        		retValue = projectName;
		        	}
	        	}
	        }
        }
	    catch(Exception e)
	    {
    		Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }
        return retValue;
	}
	
	/**
	 * get latest Build object given project name including version
	 */
	public Build getLatestBuild(APIClientConnection conn, String projectName)
	{
		Build retValue = null;

        try
        {
            Project project = Project.findByName(conn, projectName);
            java.util.List<Build> buildList = project.getBuilds();	            
	        for (Build b : buildList)
	        {
	        	if(b.getResult() == BuildDBO.Result.PASSED)
	        	{
	        		retValue = b;
//		        	Logger.getLogger(SonarOverTime.class).debug(projectName + " " + b.getTag());
	        	}
	        }
        }
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }
        return retValue;
	}
	
	/**
	 * get bundle folders
	 *   for each bundle folder, get all bundle projects
	 *     for each bundle project
	 *       if bundle project should be built
	 *         fetch bundle
	 *         buildBundle = true
	 */
	public void checkBundles(APIClientConnection conn)
	{
        try
        {
        	String bundles[] = BundleManager.getBundles();
        	if(null != bundles)
        	{
        		for(int i=0; i<bundles.length; i++)
        		{
    	        	Logger.getLogger(SonarOverTime.class).debug("Working on bundle " + bundles[i]);        		
        			String bundleFolders[] = BundleManager.getProjectsFromBundle(bundles[i]);
        			if(null != bundleFolders)
        			{
        				SOTProjectData projectDatas[] = new SOTProjectData[bundleFolders.length];

        				long lastScanTime =  BundleManager.getLastScanTime(bundles[i]);
        				
        				boolean scanBundle = false;
	        			for(int j=0; j<bundleFolders.length; j++)
	        			{
	        				SOTProjectData projectData = getProjectData(conn, bundles[i], bundleFolders[j], lastScanTime);
	        				projectDatas[j] = projectData;
	        				// if bundle project should be built
	        				if(projectData.isNewBuild(lastScanTime))
	        				{
	        					scanBundle = true;
	        					stageProject(projectData);
	        				}
	        			}
	        			BundleManager.setModuleNames(bundles[i], projectDatas);

        				if(scanBundle)
	        			{
	        				// assume all projects have has been staged including their sonar-project.properties
	        				// if no recent build was found, the last build is already staged ???
	        				// stage parent bundle sonar-project.properties
	        				// scan bundle: scan individual bundle folders + scan parent bundle
		        			for(int j=0; j<bundleFolders.length; j++)
		        			{
		        				//if(projectDatas[j].staged)
		        				{
		        					scanBundle(projectDatas[j]);
		        				}
		        			}
	        			}
	        			
	        			// scan parent bundle

	        			// set last scan time to now
	        			//BundleManager.setLastScanTime(bundles[i]);
        			}
        			else
        			{
        				Logger.getLogger(SonarOverTime.class).warn("No folders found for bundle " + bundles[i]);
        			}
        		}
        	}
        	else
        	{
	        	Logger.getLogger(SonarOverTime.class).warn("No bundles found");        		
        	}
        }
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }
	}

}
