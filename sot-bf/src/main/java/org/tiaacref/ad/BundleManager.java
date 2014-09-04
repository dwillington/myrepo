package org.tiaacref.ad;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class BundleManager 
{

	public static String BUNDLES_DIR = "";
	public static String BUNDLE_KEYS_FILE = "src/main/resources/keys.properties";
	public static String BUNDLES_FILE = "src/main/resources/bundles.properties";
	
	static
	{
		 if (System.getProperty("os.name").startsWith("Windows")) 
		 {
		     // includes: Windows 2000,  Windows 95, Windows 98, Windows NT, Windows Vista, Windows XP
		 }
		 else 
		 {
		     // everything else
			 BUNDLES_DIR += "/home/ashrafa";
		 }
		 BUNDLES_DIR += "/Temp/sot_workspace/bundles";
	}

	public static String[] getBundles()
	{
//		return getFoldersFrom(BUNDLES_DIR);
		ArrayList<String> list = new ArrayList<String>();
		String[] retValue = null;
		try
        {
			retValue = getPropertyNames(BUNDLES_FILE);
			for(int i=0; i<retValue.length; i++)
			{
				// filter out values here
				if(!retValue[i].contains("lastScanTime"))
				{
					list.add(retValue[i]);
				}
			}
        }
	    catch(Exception e)
	    {
	    	Logger.getLogger(BundleManager.class).error("", e);
	    }
		return list.toArray(new String[0]);
	}

	public static String[] getProjectsFromBundle(String bundle)
	{
//		return getFoldersFrom(BUNDLES_DIR + "/" + bundle);
		ArrayList<String> projectsList = new ArrayList<String>();
		try
        {
			String projects = getPropertyValue(bundle, BUNDLES_FILE);
			String split[] = projects.split(",");
			if (null != split)
			{
				for(int i=0; i<split.length; i++)
				{
					projectsList.add(StringUtils.strip(split[i]));
				}
			}
			else
			{
		    	Logger.getLogger(BundleManager.class).error("error");				
			}
        }
	    catch(Exception e)
	    {
	    	Logger.getLogger(BundleManager.class).error("", e);
	    }
		return projectsList.toArray(new String[0]);
	}

	public static long getLastScanTime(String bundle) throws IOException
	{
		long retValue = 0;
    	String lastScanTime = getPropertyValue(bundle + ".lastScanTime", BUNDLES_FILE);
    	if(StringUtils.isNotBlank(lastScanTime))
    	{
    		retValue = Long.valueOf(lastScanTime).longValue();
    	}
    	return retValue;
	}

	public static void setLastScanTime(String bundle) throws IOException
	{
		// number of seconds elapsed since midnight on January 1, 1970, UTC
		String lastScanTime = String.valueOf(System.currentTimeMillis()/1000);
    	setPropertyValue(bundle + ".lastScanTime", lastScanTime, BUNDLES_FILE);
	}
	
	public static void setModuleNames(String bundle, SOTProjectData projectDatas[]) throws IOException
	{
		String moduleNames = "";
		for(int j=0; j<projectDatas.length; j++)
		{
			if(projectDatas[j].startTime != 0)
			{
				moduleNames += projectDatas[j].bfVars.get("PROJ_DIR") + ",";
			}
		}
		setPropertyValue("sonar.modules", moduleNames.substring(0, moduleNames.length()-1), BUNDLES_DIR + "/" + bundle + "/sonar-project.properties");
	}

	public static String getProjectKey(String project) throws IOException
	{
		String retValue = null;
		// TODO: use project to lookup a key in a database, if not found, create one with guid
		// can also use file:
		//   read file for key
		//     if not found, create key, add entry
		retValue = getPropertyValue(project, BUNDLE_KEYS_FILE);
		if(StringUtils.isBlank(retValue))
		{
			retValue = java.util.UUID.randomUUID().toString();
	    	Logger.getLogger(BundleManager.class).debug("creating key=" + retValue + " for project=" + project);
			setPropertyValue(project, retValue, BUNDLE_KEYS_FILE );
		}
		return retValue;
	}

	public static String getPropertyValue(String propertyName, String fileName) throws IOException
	{
		String retValue = null;
		Properties prop = new Properties();
		InputStream input = null;	 
		try 
		{
			input = new FileInputStream(fileName);
			prop.load(input);
	 		retValue = prop.getProperty(propertyName);	 
		} 
		finally 
		{
			closeStream(input);
		}
		return retValue;
	}

	public static String[] getPropertyNames(String fileName) throws IOException
	{
		Set<String> retValue = null;
		Properties prop = new Properties();
		InputStream input = null;	 
		try 
		{
			input = new FileInputStream(fileName);
			prop.load(input);
	 		retValue = prop.stringPropertyNames();	 
		} 
		finally 
		{
			closeStream(input);
		}
		return retValue.toArray(new String[0]);
	}
	
	/**
	 * http://stackoverflow.com/questions/7601259/how-do-i-edit-a-properties-file-without-trashing-the-rest-of-it
	 */
	public static void setPropertyValue(String name, String value, String fileName) throws IOException
	{
		Properties prop = new Properties();
		OutputStream output = null;
	 	InputStream input = null;	 
		
	 	try 
		{
	 		// load properties first, otherwise you lose old properties
			input = new FileInputStream(fileName);
			prop.load(input);
	 
			output = new FileOutputStream(fileName);	 
			prop.setProperty(name, value);
			prop.store(output, null);
		} 
		finally 
		{
			closeStream(output);
			closeStream(input);
		}
	}
	
	public static void closeStream(InputStream input)
	{
		if (input != null) 
		{
			try 
			{
				input.close();
			} 
			catch (IOException e) 
			{
		    	Logger.getLogger(BundleManager.class).error("", e);
			}
		}		
	}

	public static void closeStream(OutputStream input)
	{
		if (input != null) 
		{
			try 
			{
				input.close();
			} 
			catch (IOException e) 
			{
		    	Logger.getLogger(BundleManager.class).error("", e);
			}
		}		
	}

//	public static String[] getFoldersFrom(String folder)
//	{
//		String retValue [] = null;
//
//		File file = null;
//		try
//        {
//        	ArrayList<String> folders = new ArrayList<String>();
//        	file = new File(folder);        	
//            // Reading directory contents
//            File[] files = file.listFiles();
//        	if(null != files)
//        	{
//	            for (int i = 0; i < files.length; i++) 
//	            {
//	            	if(files[i].isDirectory())
//	            	{
//		            	folders.add(files[i].getName());
//		    	    	Logger.getLogger(BundleManager.class).debug(files[i].getName());
//	            	}
//	            }
//        	}
//        	if(folders.size() > 0)
//        	{
//        		retValue = folders.toArray(new String[0]);
//        	}
//        }
//	    catch(Exception e)
//	    {
//	    	Logger.getLogger(BundleManager.class).error("", e);
//	    }
//        finally
//        {
//        }
//        return retValue;
//	}

}
