package org.tiaacref.ad;

import java.util.List;

import org.apache.log4j.Logger;

import com.buildforge.services.client.api.APIClientConnection;
import com.buildforge.services.client.dbo.AccessGroup;
import com.buildforge.services.client.dbo.Project;
import com.buildforge.services.client.dbo.Step;
import com.buildforge.services.common.dbo.StepDBO;

public class BFOperations {

	public void setFailContinue()
	{
        APIClientConnection conn = null;
        try
        {

	        conn = App.getBFConnection();

	        List<Project> ProjectList = Project.findAll (conn);
//	        printSonarScanDisabled(conn, ProjectList);
	        printSonarScan(conn, ProjectList);
//	        printSonarScanInline(conn, ProjectList);

if(false)
{
	        for (Project p : ProjectList)
	        {
	        	String projectName = p.getName();

	            Project project = Project.findByName(conn, projectName);
		        List<Step> stepList = project.getSteps();

		        for (Step s : stepList)
    	        {
		        	String stepName = s.getDescription();
    	            if(projectName.equals("Advice Admin Tools 1.0") && stepName.equals("Sonar scan"))
    	            {
    		            System.out.println (projectName);
        	            System.out.println ("  " + stepName);
    	            	s.setOnFail(StepDBO.OnFail.CONTINUE);
    	            	s.setFailNotify(getAccessGroup(conn, "Build Engineer"));
    	            	s.update();
    	            	project.update();
    	            	return;
    	            }
    	        }
	        }
}
        }
	    catch(Exception e)
	    {
	    	e.printStackTrace();
	    }
        finally
        {
			App.closeConn(conn);
        }
	}

	public void printSonarScanInline(APIClientConnection conn, List<Project> ProjectList) throws Exception
	{
        for (Project p : ProjectList)
        {
        	String projectName = p.getName();

            Project project = Project.findByName(conn, projectName);
	        List<Step> stepList = project.getSteps();

//            if(projectName.startsWith("Unified"))
            {
		        for (Step s : stepList)
		        {
		        	String stepName = s.getDescription();
		            if(stepName.equals("Sonar scan") && null!= s.getInlineChainUuid() && s.getInlineChainUuid().equals("68123a700c631000f4df43cc5d085d08"))
		            {
			            System.out.println (projectName + " Sonar scan" + " Inline=" + s.getInlineChainUuid());
//			            if(null != project.getEnvironmentUuid())
//			            {
//				            Environment e = Environment.findByUuid(conn, project.getEnvironmentUuid());
//				            EnvironmentEntry envPROJECT = e.getEntry("PROJECT");
//				            EnvironmentEntry envBF_PROJECTNAME = e.getEntry("BF_PROJECTNAME");
//				            if(null != envPROJECT && !projectName.equals(envPROJECT.getParameterValue()))
//				            {
//					            if (null != envPROJECT) System.out.println ("    envPROJECT=" + envPROJECT.getParameterValue());
//					            if (null != envBF_PROJECTNAME) System.out.println ("    envBF_PROJECTNAME=" + envBF_PROJECTNAME.getParameterValue());
//				            }
//			            }
		            }
		        }
            }
        }
	}

	public void printSonarScan(APIClientConnection conn, List<Project> ProjectList) throws Exception
	{
        for (Project p : ProjectList)
        {
        	String projectName = p.getName();

            Project project = Project.findByName(conn, projectName);
	        List<Step> stepList = project.getSteps();

//            if(projectName.startsWith("Unified"))
            {
		        for (Step s : stepList)
		        {
		        	String stepName = s.getDescription();
		            if(stepName.equals("Sonar scan"))
		            {
		            	String commandText = s.getCommandText();
		            	if(commandText.indexOf("sonar-runner") > 0)
		            	{
				            System.out.println (projectName + " Sonar scan" + " Active=" + s.getActive() + " Continue=" + s.getOnFail());
				            System.out.println ("----------------------------------------------------------------------------------------------------");
				            System.out.println ("    " + s.getCommandText());
				            System.out.println ("----------------------------------------------------------------------------------------------------");
		            	}
		            }
		        }
            }
        }
	}

	public void printSonarScanDisabled(APIClientConnection conn, List<Project> ProjectList) throws Exception
	{
        for (Project p : ProjectList)
        {
        	String projectName = p.getName();

            Project project = Project.findByName(conn, projectName);
	        List<Step> stepList = project.getSteps();

	        for (Step s : stepList)
	        {
	        	String stepName = s.getDescription();
	            if(stepName.equals("Sonar scan") && !s.getActive())
	            {
		            System.out.println (projectName + " Sonar scan" + " Active=FALSE");
	            }
	        }
        }
	}

	public int getAccessGroup(APIClientConnection conn, String name)
	{
		int retValue = 0;
        try
        {
        	List<AccessGroup> agList = AccessGroup.findAll(conn);
	        for (AccessGroup ag : agList)
	        {
	        	if(ag.getName().equals(name))
	        	{
	        		retValue = ag.getLevel();
	        	}
	        }
        }
	    catch(Exception e)
	    {
			Logger.getLogger(App.class).error("", e);
	    }
		return retValue;
	}
	
}
