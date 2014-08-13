package org.tiaacref.ad;

import java.util.List;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.buildforge.services.client.api.APIClientConnection;
import com.buildforge.services.client.dbo.AccessGroup;
import com.buildforge.services.client.dbo.Project;
import com.buildforge.services.client.dbo.Step;
import com.buildforge.services.common.dbo.StepDBO;

/**
 *
 */
public class App
{
	static String bfHost = "buildforge-1.ops.tiaa-cref.org";
	// TODO: use a service account
	static String user = "ashrafa";
	// TODO: password is taken in from command line, can we store password in encrypted form in property file
	static String pass = "";

	public static APIClientConnection getBFConnection()
	{
        APIClientConnection conn = null;
        try
        {
	        conn = new APIClientConnection (bfHost);
	        conn.authUser (user, pass, "AD");
        }
	    catch(Exception e)
	    {
			Logger.getLogger(App.class).error("", e);
	    }
        finally
        {
        }
        return conn;
	}
	
	public static void closeConn(APIClientConnection conn) {
		try {
			if (null != conn) {
				conn.close();
			}
		} catch (Exception e) {
			Logger.getLogger(App.class).error("", e);
		}
	}

	public static void main(String[] args)
    {
		System.out.println("Starting...");

		APIClientConnection conn = null;
		try 
		{
			Properties props = System.getProperties();
			String password = props.getProperty("password");
			if(!StringUtils.isBlank(password))
			{
				pass = password;
			}
			else
			{
				Logger.getLogger(App.class).error("usage: -Dpassword=xxx");
				System.exit(0);
			}

			conn = getBFConnection();
			SonarOverTime sot = new SonarOverTime();
//			sot.listProjects(conn);
			sot.checkBundles(conn);

		} 
		catch (Exception e) 
		{
			Logger.getLogger(App.class).error("", e);
		}
		finally 
		{
			closeConn(conn);
		}
    }
}
