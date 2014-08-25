package org.tiaacref.ad;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.commons.dbutils.DbUtils;
import org.apache.log4j.Logger;
import org.h2.tools.Server;

public class DBManager 
{
	private static DBManager dbManager = new DBManager();
	public static Server SERVER = null;
	
	private void DBManager(){}
	
	public static DBManager getInstance()
	{
		return dbManager;
	}

	public synchronized void start()
	{
		try
		{
			if(null == SERVER)
			{
				SERVER = Server.createTcpServer().start();
			}
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(DBManager.class).error("", e);
	    }
	}

	public synchronized void stop()
	{
		try
		{
			if(null != SERVER)
			{
				SERVER.stop();
			}
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(DBManager.class).error("", e);
	    }
	}
	
	public Connection getConnection()
	{
		Connection conn = null;
		try
		{
			Class.forName("org.h2.Driver");
	        conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(DBManager.class).error("", e);
	    }
		return conn;
	}	

	public String[] getBundles()
	{
       	ArrayList<String> retValue = new ArrayList<String>();
		Connection conn = null;
		try
		{
			Class.forName("org.h2.Driver");
	        conn = getConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "SELECT NAME FROM BUNDLES";
	        ResultSet rs = stmt.executeQuery(sql);

	        while(rs.next())
	        {
	           String name = rs.getString("NAME");
	           retValue.add(name);
	        }
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(DBManager.class).error("", e);
	    }
        finally
        {
        	try 
        	{
				DbUtils.close(conn);
			}
        	catch (SQLException e) 
        	{
    	    	Logger.getLogger(DBManager.class).error("", e);
			}
        }
		return retValue.toArray(new String[0]);
	}
	
	public String[] getProjectsFromBundle(String bundle)
	{
       	ArrayList<String> retValue = new ArrayList<String>();
		Connection conn = null;
		try
		{
			Class.forName("org.h2.Driver");
	        conn = getConnection();
	        Statement stmt = conn.createStatement();
	        String sql = "SELECT NAME FROM BUNDLES";
	        ResultSet rs = stmt.executeQuery(sql);

	        while(rs.next())
	        {
	           String name = rs.getString("NAME");
	           retValue.add(name);
	        }
		}
	    catch(Exception e)
	    {
	    	Logger.getLogger(DBManager.class).error("", e);
	    }
        finally
        {
        	try 
        	{
				DbUtils.close(conn);
			}
        	catch (SQLException e) 
        	{
    	    	Logger.getLogger(DBManager.class).error("", e);
			}
        }
		return retValue.toArray(new String[0]);
	}

	
}
