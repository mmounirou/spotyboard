package com.mmounirou.spotiboard.spotify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.digester.Digester;
import org.xml.sax.SAXException;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmounirou.spotiboard.billboard.Track;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class SpotifyUtils
{
	private static Map<Track, String> m_cache = Maps.newHashMap();

	public static String getTrackUrl(Track track) throws SpotifyException
	{
		String result = m_cache.get(track);
		if ( result == null )
		{
			try
			{
				Client client = Client.create();
				WebResource resource = client.resource("http://ws.spotify.com");
				String strXmlResult = resource.path("search/1/track").queryParam("q", track.getSong()).get(String.class);

				List<XTracks> xtracks = parseResult(strXmlResult);
				String href = xtracks.get(0).getHref();
				m_cache.put(track, href);
			}
			catch ( IOException e )
			{
				throw new SpotifyException(e);
			}
			catch ( SAXException e )
			{
				throw new SpotifyException(e);
			}
		}

		return m_cache.get(track);
	}

	public static List<XTracks> parseResult(String strResult) throws IOException, SAXException
	{
		Digester digester = new Digester();
		List<XTracks> result = Lists.newArrayList();
		digester.push(result);
		addRules(digester);
		digester.parse(new ByteArrayInputStream(strResult.getBytes()));
		return result;
	}

	private static void addRules(Digester digester)
	{
		digester.addObjectCreate("tracks/track", XTracks.class);
		digester.addSetNext("tracks/track", "add");
		digester.addSetProperties("tracks/track");
		digester.addBeanPropertySetter("tracks/track/name", "trackName");
		digester.addBeanPropertySetter("tracks/track/artist/name", "artistName");
		digester.addBeanPropertySetter("tracks/track/album/availability/territories", "availability");
	}

	public static void main(String[] args) throws IOException, SAXException, SpotifyException
	{
		Track track = new Track(1, "Carly Rae Jepsen", "Call Me Maybe");
		String trackUrl = getTrackUrl(track);
		System.out.println(trackUrl);
		List<XTracks> parseResult = parseResult(trackUrl);
		System.out.println(Joiner.on("\n").join(parseResult));
	}

}
