package org.tiaacref.ad;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

public class BundleManager 
{

	public static String BUNDLES_DIR = "C:/Temp/sot_workspace/bundles";
	public static String BUNDLE_KEYS_FILE = BUNDLES_DIR + "/keys.properties";

	public static String[] getBundles()
	{
		return getFoldersFrom(BUNDLES_DIR);
	}

	public static String[] getProjectsFromBundleFolder(String bundle)
	{
		return getFoldersFrom(BUNDLES_DIR + "/" + bundle);
	}

	public static String[] getFoldersFrom(String folder)
	{
		String retValue [] = null;

		File file = null;
		try
        {
        	ArrayList<String> folders = new ArrayList<String>();
        	file = new File(folder);        	
            // Reading directory contents
            File[] files = file.listFiles();
        	if(null != files)
        	{
	            for (int i = 0; i < files.length; i++) 
	            {
	            	if(files[i].isDirectory())
	            	{
		            	folders.add(files[i].getName());
//		    	    	Logger.getLogger(BundleManager.class).debug(files[i].getName());
	            	}
	            }
        	}
        	if(folders.size() > 0)
        	{
        		retValue = folders.toArray(new String[0]);
        	}
        }
	    catch(Exception e)
	    {
	    	Logger.getLogger(BundleManager.class).error("", e);
	    }
        finally
        {
        }
        return retValue;
	}
	
	public static long getLastScanTime(String bundle) throws IOException
	{
    	String lastScanTime = getPropertyValue("lastScanTime", BUNDLES_DIR + "/" + bundle + "/bundle.properties");
    	return Long.valueOf(lastScanTime).longValue();
	}

	public static String getProjectKey(String project) throws IOException
	{
		String retValue = null;
		// TODO: use project to lookup a key in a database, if not found, create one with guid
		// can also use file:
		//   read file for key
		//     if not found, create key, add entry
		retValue = getPropertyValue(project, BUNDLES_DIR + "/keys.properties");
		if(StringUtils.isBlank(retValue))
		{
			String uuid = java.util.UUID.randomUUID().toString();
	    	Logger.getLogger(BundleManager.class).debug("creating key=" + uuid + " for project=" + project);
			setPropertyValue(project, uuid, BUNDLE_KEYS_FILE );
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
		return retValue;
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
			if (output != null) 
			{
				try 
				{
					output.close();
				} 
				catch (IOException e) 
				{
			    	Logger.getLogger(BundleManager.class).error("", e);
				}
			}
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
	}

}
