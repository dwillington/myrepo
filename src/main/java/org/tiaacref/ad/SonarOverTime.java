package org.tiaacref.ad;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.buildforge.services.client.api.APIClientConnection;
import com.buildforge.services.client.dbo.Build;
import com.buildforge.services.client.dbo.Log;
import com.buildforge.services.client.dbo.Project;
import com.buildforge.services.client.dbo.Result;
import com.buildforge.services.common.dbo.BuildDBO;

/**
 * C:\Temp\maven-play\buildforge>mvn package exec:java -Dexec.mainClass="org.tiaacref.ad.App" -Dlog4j.configuration=log4j.properties
 */
public class SonarOverTime 
{
	public void getLatestBuilds(APIClientConnection conn)
	{
		
		listProjects(conn);
		
		SOTProject sotProject = new SOTProject();

		String projects[] = getProjectsFromBundleFile();
		for(int i=0; i<projects.length; i++)
		{
			sotProject = getLatestProjectBuild(conn, projects[i]);
			Logger.getLogger(SonarOverTime.class).debug(sotProject.bfVars.get("BF_PROJECTNAME_PHYS"));
			Logger.getLogger(SonarOverTime.class).debug(sotProject.bfVars.get("BF_ROOT"));
			Logger.getLogger(SonarOverTime.class).debug(sotProject.bfVars.get("BF_HOST"));
			stageProject(sotProject);
			break;
		}
	}

	public void listProjects(APIClientConnection conn)
	{
        try
        {
        	String projectName = null;
	        List<Project> ProjectList = Project.findAll(conn);
    		Logger.getLogger(SonarOverTime.class).debug(ProjectList.size());

	        for (Project p : ProjectList)
	        {
	        	if(null != projectName && p.getName().startsWith(projectName)) continue;
	        	projectName = p.getName();
        		projectName = projectName.replaceAll(" \\d.*", "");
        		Logger.getLogger(SonarOverTime.class).debug(projectName);
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

	
	public void stageProject(SOTProject sotProject)
	{
		try
		{
            String command = "cmd.exe /C ant" + " -Dsot.project.name=" + sotProject.bfVars.get("BF_PROJECTNAME_PHYS") + " -Dsot.project.dir=" + sotProject.bfVars.get("BF_ROOT") + " -Dsot.PROJ_DIR=" + sotProject.bfVars.get("PROJ_DIR")  + " -Dsot.project.key=" + getProjectKey(sotProject);
            runCommnad(command);
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(SonarOverTime.class).error("", e);
	    }
        finally
        {
        }		
	}
	
	public String getProjectKey(SOTProject sotProject)
	{
		// TODO: use sotProject.name to lookup a key in a database, if not found, create one with guid
		return "2ae835535bbafbaff56c1a4ba218xxxx";
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

	public SOTProject getLatestProjectBuild(APIClientConnection conn, String projectName)
	{
		SOTProject retValue = new SOTProject();
		Build latestBuild = null;
		
        try
        {
        	retValue.name = projectName;
        	String latestProjectName = getLatestProjectName(conn, projectName);
        	if(null != latestProjectName)
        	{
        		latestBuild = getLatestBuild(conn, latestProjectName);
            	if(null != latestBuild)
            	{
        			retValue.startTime = latestBuild.getStartTime();
        			if(retValue.isNewBuild())
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
	
	public void populateBFVars(APIClientConnection conn, Build latestBuild, SOTProject sotProject)
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
		        		if(null != parts && parts.length ==2)
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

	public String getLatestProjectName(APIClientConnection conn, String name)
	{
		String retValue = null;

        try
        {
	        List<Project> ProjectList = Project.findAll(conn);

	        for (Project p : ProjectList)
	        {
	        	String projectName = p.getName();
	        	if(projectName.matches(name + " \\d.*"))
	        	{
	        		//Logger.getLogger(SonarOverTime.class).debug("MATCHES: " + projectName);
	        		retValue = projectName;
	        	}
	        }
        }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
        finally
        {
        }
        return retValue;
	}
	
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

	public String[] getProjectsFromBundleFile()
	{
		String retValue [] = null;

		BufferedReader reader = null;
        try
        {
        	ArrayList<String> projects = new ArrayList();
    		reader = new BufferedReader(new FileReader("bundles/ud.txt"));
    		String line = null;
    		while ((line = reader.readLine()) != null) 
    		{
    			if(!line.startsWith("#") && !line.equals(""))
    			{
		            Logger.getLogger(SonarOverTime.class).debug(line);
    				String parts[] = line.split("/");
	    			projects.add(parts[0]);
    			}
    		}
    		retValue =  projects.toArray(new String[0]);
        }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
        finally
        {
        	try
        	{
            	if (null != reader)
            	{
            		reader.close();
            	}
        	}
    	    catch(Exception e)
    	    {
    	    	Logger.getLogger(SonarOverTime.class).error("", e);
    	    }
        }
        return retValue;
	}
	
}
