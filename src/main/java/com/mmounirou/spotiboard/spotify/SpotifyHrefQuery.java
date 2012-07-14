package com.mmounirou.spotiboard.spotify;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.digester.Digester;
import org.apache.commons.io.IOUtils;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mmounirou.spotiboard.SpotiBoard;
import com.mmounirou.spotiboard.billboard.Track;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class SpotifyHrefQuery
{
	private static final int QUERY_LIMIT_BY_SECONDS = 100;
	private TrackCache m_trackCache;

	public SpotifyHrefQuery(TrackCache trackCache)
	{
		this.m_trackCache = trackCache;
	}

	public Map<Track, String> getTrackHrefs(Set<Track> tracks) throws SpotifyException
	{
		Map<Track, String> result = Maps.newLinkedHashMap();
		int queryCount = 0;

		for (Track track : tracks)
		{
			String strHref = m_trackCache.get(track);
			if (strHref == null)
			{
				if ((queryCount % QUERY_LIMIT_BY_SECONDS) == 0)
				{
					try
					{
						Thread.sleep(TimeUnit.SECONDS.toMillis(1));
					} catch (InterruptedException e)
					{
						// DO nothing
					}
				}

				try
				{
					Client client = Client.create();
					WebResource resource = client.resource("http://ws.spotify.com");
					String strXmlResult = resource.path("search/1/track").queryParam("q", track.getSong()).get(String.class);

					List<XTracks> xtracks = parseResult(strXmlResult);
					if (xtracks.isEmpty())
					{
						SpotiBoard.LOGGER.warn(String.format("no spotify song for %s:%s", track.getArtist(), track.getSong()));
					} else
					{
						strHref = xtracks.get(0).getHref();
						m_trackCache.put(track, strHref);
						queryCount++;
					}
				} catch (IOException e)
				{
					throw new SpotifyException(e);
				} catch (SAXException e)
				{
					throw new SpotifyException(e);
				}
			}
			if (strHref != null)
			{
				result.put(track, strHref);
			}
		}
		return ImmutableMap.copyOf(result);
	}

	private static List<XTracks> parseResult(String strResult) throws IOException, SAXException
	{
		Digester digester = new Digester();
		List<XTracks> result = Lists.newArrayList();
		digester.push(result);
		addRules(digester);
		digester.parse(IOUtils.toInputStream(strResult, Charsets.UTF_8));
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

}
