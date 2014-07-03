package com.gravypod.cnu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JComboBox;

public class Downloader {
	
	private JFrame frame;
	
	private JComboBox<Platform> avaliblePlatforms;
	
	// private JProgressBar progressBar;
	
	private JButton downloadButton;
	
	private JLabel currentBuild;
	
	private JLabel latestBuild;
	
	private Platform platform;
	
	private File track;
	
	private JProgressBar progressBar;
	
	private JCheckBox openDownload, prefferInstaller;
	
	private final File configFile = new File("./config.cfg");
	
	private final Config config;
	
	/**
	 * Create the application.
	 */
	public Downloader() {
	
		initialize();
		
		config = Config.loadConfig(configFile);
		avaliblePlatforms.setSelectedItem(config.getPlatform());
		setPlatform(config.getPlatform());
		prefferInstaller.setSelected(config.shouldPreferInstaller());
		openDownload.setSelected(config.shouldOpenDownload());
	}
	
	/**
	 * Initialize the contents of the frame.
	 * 
	 * @param latest
	 * @param current
	 */
	private void initialize() {

		
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblCurrentBuild = new JLabel("Current Build:");
		lblCurrentBuild.setBounds(34, 90, 94, 14);
		frame.getContentPane().add(lblCurrentBuild);
		
		currentBuild = new JLabel("cBuild");
		currentBuild.setBounds(138, 90, 286, 14);
		frame.getContentPane().add(currentBuild);
		
		JLabel lblLastDownloaded = new JLabel("Last Downloaded:");
		lblLastDownloaded.setBounds(14, 115, 114, 14);
		frame.getContentPane().add(lblLastDownloaded);
		
		latestBuild = new JLabel("lBuild");
		latestBuild.setBounds(138, 115, 286, 14);
		frame.getContentPane().add(latestBuild);
		
		prefferInstaller = new JCheckBox("Installer Preferred", true);
		prefferInstaller.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent e) {
			
				if (config != null) {
					config.setPreferInstaller(prefferInstaller.isSelected());
				}
			}
		});
		prefferInstaller.setBounds(19, 232, 176, 23);
		frame.getContentPane().add(prefferInstaller);
		
		downloadButton = new JButton("Download and Update");
		downloadButton.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
			
				downloadButton.setEnabled(false);
				final boolean useInstallers = prefferInstaller.isSelected();
				final boolean openDownloaded = openDownload.isSelected();
				new Thread() {
					
					public void run() {
					
						platform.downloadBuild(track, useInstallers, openDownloaded, progressBar);
					}
					
				}.start();
			}
		});
		
		downloadButton.setBounds(19, 174, 176, 23);
		frame.getContentPane().add(downloadButton);
		
		progressBar = new JProgressBar();
		progressBar.setBounds(54, 140, 315, 23);
		frame.getContentPane().add(progressBar);
		
		openDownload = new JCheckBox("Open Download");
		openDownload.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
			
				if (config != null) {
					config.setOpenDownload(openDownload.isSelected());
				}
			}
		});
		openDownload.setBounds(19, 204, 176, 23);
		frame.getContentPane().add(openDownload);
		
		avaliblePlatforms = new JComboBox<Platform>();
		avaliblePlatforms.addActionListener(new ActionListener() {
			
			public void actionPerformed(ActionEvent arg0) {
			
				Platform platform = (Platform) avaliblePlatforms.getSelectedItem();
				setPlatform(platform);
			}
		});

		for (Platform p : Platform.values()) {
			avaliblePlatforms.addItem(p);
		}
		avaliblePlatforms.setBounds(138, 56, 99, 23);
		frame.getContentPane().add(avaliblePlatforms);
		
		JLabel lblBuildTrack = new JLabel("Platform:");
		lblBuildTrack.setBounds(56, 65, 65, 14);
		frame.getContentPane().add(lblBuildTrack);
		
		/*
		 * progressBar = new JProgressBar(); progressBar.setBounds(34, 147, 359, 14); frame.getContentPane().add(progressBar);
		 */
	}
	
	public JFrame getFrame() {
	
		return frame;
	}
	
	public Platform getPlatform() {
	
		return platform;
	}
	
	public void setPlatform(Platform platform) {
	
		if (config != null) {
			config.setPlatform(platform);
		}
		
		this.platform = platform;
		track = new File("./" + platform.name());
		
		if (!track.exists()) {
			track.mkdirs();
		}
		String cBuild = platform.getLastDownloaded(track);
		String lBuild = platform.getLatestBuild();
		this.currentBuild.setText(cBuild == null ? "n/a" : cBuild);
		this.latestBuild.setText(lBuild == null ? "n/a" : lBuild);
		
		downloadButton.setEnabled(!currentBuild.getText().equals(latestBuild.getText()));
		
	}
}
