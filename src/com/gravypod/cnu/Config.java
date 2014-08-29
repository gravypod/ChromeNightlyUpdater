package com.gravypod.cnu;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

public class Config {
	
	private Platform platform;
	
	private boolean preferInstaller, openDownload;
	private File config;
	private Config(File config, Platform platform, boolean preferInstaller, boolean openDownload) {
		this.config = config;
		this.platform = platform;
		this.preferInstaller = preferInstaller;
		this.openDownload = openDownload;
	}
	
	public static Config loadConfig(File config) {
		Config conf = new Config(config, Platform.WIN, true, false);
		if (!config.exists()) {
			conf.saveConfig();
		} else {
			Scanner sc = null;
			try {
				sc = new Scanner(config);
				String platform = Platform.WIN.name(), openDownload = "false", preferInstaller = "true";
				if (sc.hasNextLine()) {
					platform = sc.nextLine();
				}
				if (sc.hasNextLine()) {
					preferInstaller = sc.nextLine();
				}
				if (sc.hasNextLine()) {
					openDownload = sc.nextLine();
				}
				System.out.println("Config: " + platform + ", " + openDownload + ", " + preferInstaller);
				
				conf.setPlatform(Platform.valueOf(platform));
				conf.setOpenDownload(Boolean.valueOf(openDownload));
				conf.setPreferInstaller(Boolean.valueOf(preferInstaller));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				conf.saveConfig();
			} finally {
				if (sc != null) {
					sc.close();
				}
			}
		}
		
		return conf;
	}
	
	private void saveConfig() {
	
		try {
			PrintWriter pw = new PrintWriter(config);
			pw.println(platform.name());
			pw.println(preferInstaller);
			pw.println(openDownload);
			pw.close();
			

			System.out.println("Saving: " + platform + ", " + openDownload + ", " + preferInstaller);
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public void setPlatform(Platform platform) {
		boolean changed = this.platform != platform;
		this.platform = platform;
		if (changed) {
			saveConfig();
		}
	}
	
	public Platform getPlatform() {
		return platform;
	}
	
	public void setOpenDownload(boolean openDownload) {
		boolean changed = openDownload != this.openDownload;
		this.openDownload = openDownload;
		if (changed) {
			saveConfig();
		}
	}
	
	public boolean shouldOpenDownload() {
	
		return openDownload;
	}
	
	public void setPreferInstaller(boolean preferInstaller) {
		boolean changed = preferInstaller != this.preferInstaller;
		this.preferInstaller = preferInstaller;
		if (changed) {
			saveConfig();
		}
	}
	
	public boolean shouldPreferInstaller() {
	
		return preferInstaller;
	}
}
