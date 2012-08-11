package com.mmounirou.spotirss.spotify.user;

import java.io.IOException;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mmounirou.spotirss.json.Playlist;
import com.mmounirou.spotirss.json.Playlists;
import com.mmounirou.spotirss.spotify.exceptions.SpotifyClientException;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public class SpotifyClient
{
	private WebResource resource;
	private String m_user;

	public SpotifyClient(String host, int port, String user)
	{
		this.m_user = user;
		Client client = Client.create();
		String strUrl = String.format("http://%s:%d", host, port);
		resource = client.resource(strUrl);
	}

	public Playlists getAllPlaylists() throws SpotifyClientException
	{
		ClientResponse response = resource.path("user").path(m_user).path("playlists").get(ClientResponse.class);
		try
		{
			return Playlists.fromJsonStream(response.getEntityInputStream());
		} catch (JsonParseException e)
		{
			throw new SpotifyClientException(e);
		} catch (JsonMappingException e)
		{
			throw new SpotifyClientException(e);

		} catch (IOException e)
		{
			throw new SpotifyClientException(e);
		}
	}

	public void patch(Playlist playlist)
	{
		ClientResponse response = resource.path("playlist").path(playlist.getUri()).path("patch").entity(playlist.tracksToJson()).post(ClientResponse.class);

		System.out.println(response.getClientResponseStatus());
	}

	public static void main(String[] args) throws SpotifyClientException
	{
		String host = "ec2-54-247-32-141.eu-west-1.compute.amazonaws.com";
		int port = 1337;
		String struser = "mmounirou";
		SpotifyClient client = new SpotifyClient(host, port, struser);
		Playlists allPlaylists = client.getAllPlaylists();
		for (Playlist playlist : allPlaylists.getPlaylists())
		{
			if (StringUtils.equalsIgnoreCase(playlist.getTitle(), "test"))
			{
				playlist.getTracks().remove(0);
				client.patch(playlist);
			}
		}
		System.out.println(allPlaylists);
	}
}
