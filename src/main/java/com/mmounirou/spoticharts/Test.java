package com.mmounirou.spoticharts;

import jahspotify.JahSpotify;
import jahspotify.media.Link;
import jahspotify.media.PlaylistContainer;
import jahspotify.media.User;
import jahspotify.services.JahSpotifyService;

import java.io.File;
import java.util.Map;

import com.google.common.base.Joiner;

public class Test
{
	public static void main(String[] args)
	{
		//System.out.println(Joiner.on("\n").join(System.getProperty("java.library.path").split(";")));
		System.setProperty("jahspotify.spotify.username", "mmounirou");
		System.setProperty("jahspotify.spotify.password", "qaynyyH7");
		
		File userDir = new File(System.getProperty("user.home"));
		File config = new File(userDir, ".libjahspotify");
		config.mkdirs();

		JahSpotifyService.initialize(config);
		JahSpotifyService instance = JahSpotifyService.getInstance();
		JahSpotify jahSpotify = instance.getJahSpotify();
//		for (int i = 0; i < 10; i++) {
//			try {
//				System.out.println("try to login ....");
//				Thread.sleep(250);
//				jahSpotify.login(config.getAbsolutePath(), "mmounirou", "qaynyyH7");
//				//jahSpotify.login("mmounirou", "qaynyyH7", false);
//				break;
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
		User user = jahSpotify.getUser();
		user.getFullName();
		
		//QueueHandler.initialize();

	}
}
