package com.gravypod.cnu;

import java.awt.Desktop;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

public enum Platform {
		
		OSX("Mac", "chrome-mac.zip", null), 
		WIN("Win", "chrome-win32.zip", "mini_installer.exe"), 
		WIN_64("Win_x64", "chrome-win32.zip", "mini_installer.exe"),
		LINUX("Linux", "chrome-linux.zip", null), 
		LINUX_64("Linux_x64", "chrome-linux.zip", null);
		
		public static final String LAST_CHANGED_VERSION = "https://commondatastorage.googleapis.com/chromium-browser-snapshots/%s/LAST_CHANGE"; // Platform
		
		public static final String LATEST_URL = "https://commondatastorage.googleapis.com/chromium-browser-snapshots/%s/%s/%s"; // Platform, version, and file.
		
		private final String platform, zipName, binName;
		
		private Platform(String platform, String zipName, String binName) {
		
			this.platform = platform;
			this.zipName = zipName;
			this.binName = binName;
		}
		
		public String getLastDownloaded(File folder) {
			
			File lastBuild = new File(folder, this.platform + "LAST_CHANGED");
			
			if (!lastBuild.exists() || !lastBuild.canRead()) {
				return null;
			}
			
			try {
				return getFileContents(lastBuild).trim();
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
			
		}
		public void setLastBuild(File folder, String build) {
			try {
				File lastChangedFile = new File(folder, this.platform + "LAST_CHANGED");
				PrintWriter writer = new PrintWriter(lastChangedFile);
				writer.write(build.trim());
				writer.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			
		}
		public String getLatestBuild() {
		
			String build;
			try {
				build = getHTML(String.format(LAST_CHANGED_VERSION, this.platform)).toString().trim();
			} catch (IOException e) {
				build = null;
				e.printStackTrace();
			}
			return build;
		}
		
		public boolean downloadBuild(File folder, boolean installerPrefered, boolean openDownloaded, JProgressBar frame) {
		
			if (!folder.exists() || !folder.canWrite()) {
				return false;
			}
			
			String latestBuild = getLatestBuild();
			
			if (latestBuild == null) {
				return false;
			}
			
			String buildFileName = (installerPrefered && this.binName != null ? binName : zipName);
			
			File build = new File(folder, latestBuild + "_" + buildFileName); // Download installer over zips if they are available
			String url = String.format(LATEST_URL, this.platform, latestBuild, buildFileName);
			
			try {
				downloadFile(build, url, frame);
				
				if (openDownloaded) {
					Desktop.getDesktop().open(new File(folder.getCanonicalPath()));
				}
				
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Error downloading file! \n" + e.toString(), "Error Downloading Chrome!", JOptionPane.ERROR_MESSAGE);
				return false;
			}
			
			setLastBuild(folder, latestBuild);
			
			return true;
		}
		
		private void downloadFile(File file, String u, JProgressBar frame) throws IOException {
			frame.setVisible(true);
			frame.setStringPainted(true);
			BufferedInputStream inputStream = null;
			FileOutputStream outputStream = null;
			
			final URL url = new URL(u);
			final URLConnection connection = url.openConnection();
			final int size = connection.getContentLength();
			
			if (size < 0) {
				System.out.println("Unable to get the latest version of chrome!");
			} else {
				System.out.println("Downloading the latest version of chrome (length: " + size + " bytes, URL: " + u + ")...");
			}
			
			inputStream = new BufferedInputStream(/*new ProgressMonitorInputStream(frame, "Downloading the latest version of chrome (length: " + size + " bytes, URL: " + u + ")...",*/ url.openStream()/*)*/);
			outputStream = new FileOutputStream(file);
			
			final byte data[] = new byte[1024];
			int count;
			double sumCount = 0.0;
			int percentage;
			int lastPercentage = 0;
			frame.setMaximum(100);
			while ((count = inputStream.read(data, 0, 1024)) != -1) {
				outputStream.write(data, 0, count);
				
				sumCount += count;
				
				percentage = (int) Math.ceil(sumCount / size * 100);
				
				if (percentage != lastPercentage) {
					String percent = percentage + "%";
					System.out.println(percent);
					frame.setValue(percentage);
					frame.setString(percent);
				}
				
				lastPercentage = percentage;
			}
			
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			frame.setVisible(false);
		}
		
		private String getHTML(String url) throws IOException {
		
			URL u = new URL(url);
			StringBuilder builder = new StringBuilder();
			
			Scanner sc = new Scanner(u.openStream());
			
			while (sc.hasNextLine()) {
				builder.append(sc.nextLine());
				builder.append('\n');
			}
			
			return builder.toString();
			
		}
		private String getFileContents(File f) throws IOException {
			
			FileInputStream fis = new FileInputStream(f);
			StringBuilder builder = new StringBuilder();
			
			Scanner sc = new Scanner(fis);
			
			while (sc.hasNextLine()) {
				builder.append(sc.nextLine());
				builder.append('\n');
			}
			
			return builder.toString();
			
		}
		
	}
