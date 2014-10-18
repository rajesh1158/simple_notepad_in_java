package com.notepad;

import java.io.File;
import java.io.IOException;

public class TestMain {

	private static final String propFileDir = System.getProperty("java.io.tmpdir");
	private static final String propFileName = "simpad.properties";
	private static final String frameFileName = "frames.properties";
	
	public static void main(String a[])
	{
		File propFile = new File(propFileDir, propFileName);
		File frameFile = new File(propFileDir, frameFileName);
		try
		{
			propFile.createNewFile();
			frameFile.createNewFile();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		
		new Notepad();
	}

	public static String getPropfiledir() {
		return propFileDir;
	}

	public static String getPropfilename() {
		return propFileName;
	}

	public static String getFramefilename() {
		return frameFileName;
	}
}