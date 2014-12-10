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

	private String path;
	private boolean status = false;
	private String name = "";
    private String email = "";

	public FtpUploadTask(String path, String name, String email) {
		this.path = path;
		this.name = name;
        this.email = email;
	}

	protected FTPClient doInBackground(String... args) {
		
		FTPClient mFTPClient = new FTPClient();

		// connecting to the host
		try {
			mFTPClient.connect("ftp.martinezhugo.com", 21);
			status = mFTPClient.login("martinezhugo", "dj$bG0u8v[");
            Log.e("Status connection", String.valueOf(status));
		
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		try {  
            FileInputStream srcFileStream = new FileInputStream(path);
            mFTPClient.changeWorkingDirectory("www/pfe/images/");
            status = mFTPClient.makeDirectory(email+"/");
            Log.e("Status makeDirectory", String.valueOf(status));
            mFTPClient.enterLocalPassiveMode();
            mFTPClient.setFileType(FTP.BINARY_FILE_TYPE, FTP.BINARY_FILE_TYPE);
            mFTPClient.setFileTransferMode(FTP.BINARY_FILE_TYPE);

            Log.i("ANTHO", "path "+email+"/"+name);
            status = mFTPClient.storeFile(email+"/"+name,
                    srcFileStream);
            Log.e("Status upload", String.valueOf(status));
            srcFileStream.close();  
       } catch (Exception e) {  
            e.printStackTrace();  
       }  
		return mFTPClient;
	}

	protected void onPostExecute(FTPClient result) {
	}

}