package com.mmounirou.spotirss.json;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

public class Playlists
{
	private List<Playlist> playlists = Lists.newArrayList();

	public List<Playlist> getPlaylists()
	{
		return playlists;
	}

	public void setPlaylists(List<Playlist> playlists)
	{
		this.playlists = playlists;
	}

	public static Playlists fromJsonStream(InputStream data) throws JsonParseException, JsonMappingException, IOException
	{
		ObjectMapper mapper = new ObjectMapper();
		return mapper.readValue(data, Playlists.class);
	}
}
