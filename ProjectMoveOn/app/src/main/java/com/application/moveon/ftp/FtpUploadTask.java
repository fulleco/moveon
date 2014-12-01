package com.application.moveon.ftp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import android.os.AsyncTask;
import android.util.Log;

public class FtpUploadTask extends AsyncTask<String, Void, FTPClient> {

	private File file;
	private boolean status = false;
	private String login = "";
	private String name = "";

	public FtpUploadTask(File file, String login, String name) {
		this.file = file;
		this.login = login;
		this.name = name;
	}

	protected FTPClient doInBackground(String... args) {
		
		FTPClient mFTPClient = new FTPClient();

		// connecting to the host
		try {
			mFTPClient.connect("91.121.17.174", 21);
			status = mFTPClient.login("app_mobile", "uzhUfgm5gubGlz#mgrz");
		
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {  
            FileInputStream srcFileStream = new FileInputStream(file);  
            boolean status = mFTPClient.makeDirectory("appli_hiddenphoto/"+login+"/");  
            mFTPClient.enterLocalPassiveMode();
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
            mFTPClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);
            status = mFTPClient.storeFile("appli_hiddenphoto/"+login+"/"+name,  
                      srcFileStream);  
            Log.e("Status", String.valueOf(status));  
            srcFileStream.close();  
       } catch (Exception e) {  
            e.printStackTrace();  
       }  
		return mFTPClient;
	}

	protected void onPostExecute(FTPClient result) {
	}

}