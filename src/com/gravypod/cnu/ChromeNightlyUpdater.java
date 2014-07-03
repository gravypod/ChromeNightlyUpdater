package com.gravypod.cnu;

import java.awt.EventQueue;


public class ChromeNightlyUpdater {
	
	
	public static void main(String[] args) {
		
		
		
		EventQueue.invokeLater(new Runnable() {
			
			public void run() {
			
				try {
					final Downloader downloader = new Downloader();	
					downloader.getFrame().setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	
	
}
