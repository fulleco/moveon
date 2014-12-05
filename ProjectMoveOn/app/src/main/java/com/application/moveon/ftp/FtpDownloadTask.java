package com.application.moveon.ftp;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.SocketException;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;

import android.os.AsyncTask;

public class FtpDownloadTask extends AsyncTask<String, Void, FTPClient> {

	String path = "";
	String destination = "";
    String email = "";
	boolean status = false;

	public FtpDownloadTask(String path, String destination, String email) {
		this.path = path;
		this.destination = destination;
        this.email = email;
	}

	protected FTPClient doInBackground(String... args) {

		FTPClient mFTPClient = new FTPClient();

		// connecting to the host
		try {
            mFTPClient.connect("ftp.martinezhugo.com", 21);
            status = mFTPClient.login("martinezhugo", "dj$bG0u8v[");
		
		} catch (SocketException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// now check the reply code, if positive mean connection success
		if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
			/*
			 * Set File Transfer Mode
			 * 
			 * To avoid corruption issue you must specified a correct transfer
			 * mode, such as ASCII_FILE_TYPE, BINARY_FILE_TYPE, EBCDIC_FILE_TYPE
			 * .etc. Here, I use BINARY_FILE_TYPE for transferring text, image,
			 * and compressed files.
			 */
			try {
				mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
				mFTPClient.enterLocalPassiveMode();
				mFTPClient.changeWorkingDirectory("www/moveon/pfe/");
				FileOutputStream desFileStream = new FileOutputStream(
						destination);
				status = mFTPClient.retrieveFile(path, desFileStream);
				desFileStream.close();

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mFTPClient;
	}

	protected void onPostExecute(FTPClient result) {
	}

}