/*
 * Copyright (C) 2011 Mohamed MOUNIROU
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.mmounirou.spotirss.spotify;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import org.apache.commons.digester.Digester;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mmounirou.spotirss.SpotiRss;
import com.mmounirou.spotirss.rss.Track;
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

		for ( Track track : tracks )
		{
			String strHref = getFromCache(track);
			if ( strHref == null )
			{
				if ( queryCount != 0 && (queryCount % QUERY_LIMIT_BY_SECONDS) == 0 )
				{
					try
					{
						Thread.sleep(TimeUnit.SECONDS.toMillis(1));
					}
					catch ( InterruptedException e )
					{
						// DO nothing
					}
				}

				try
				{
					Client client = Client.create();
					WebResource resource = client.resource("http://ws.spotify.com");
					String strXmlResult = resource.path("search/1/track").queryParam("q",URIUtil.encodePathQuery(track.getSong())).get(String.class);
					// System.out.println(strXmlResult);
					List<XTracks> xtracks = parseResult(strXmlResult);
					if ( xtracks.isEmpty() )
					{
						SpotiRss.LOGGER.warn(String.format("no spotify song for %s:%s", Joiner.on("&").join(track.getArtists()), track.getSong()));
					}
					else
					{
						strHref = findBestMatchingTrack(xtracks, track).getHref();
						putInCache(track, strHref);
						queryCount++;
					}
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
			if ( strHref != null )
			{
				result.put(track, strHref);
			}
		}
		return ImmutableMap.copyOf(result);
	}

	private void putInCache(Track track, String strHref)
	{
		if ( m_trackCache != null )
		{
			m_trackCache.put(track, strHref);
		}
	}

	private String getFromCache(Track track)
	{
		if ( m_trackCache != null )
		{
			return m_trackCache.get(track);
		}
		return null;
	}

	private XTracks findBestMatchingTrack(List<XTracks> xtracks, final Track track)
	{
		if ( xtracks.size() == 1 )
		{
			return xtracks.get(0);
		}

		TreeMap<Integer, XTracks> sortedTrack = Maps.newTreeMap();
		for ( XTracks xTrack : xtracks )
		{
			sortedTrack.put(getLevenshteinDistance(xTrack, track), xTrack);
		}

		Integer minDistance = Iterables.get(sortedTrack.keySet(), 0);
		XTracks choosedTrack = sortedTrack.get(minDistance);

		if ( minDistance > 1 )
		{
			SpotiRss.LOGGER.info(String.format("(%s:%s) choosed for (%s:%s) with distance %d", choosedTrack.getOriginalTrackName(),
					Joiner.on(",").join(choosedTrack.getAllArtists()), track.getSong(), Joiner.on(",").join(track.getArtists()), minDistance));
		}
		else
		{
			SpotiRss.LOGGER.debug(String.format("(%s:%s) choosed for (%s:%s) with distance %d", choosedTrack.getOriginalTrackName(),
					Joiner.on(",").join(choosedTrack.getAllArtists()), track.getSong(), Joiner.on(",").join(track.getArtists()), minDistance));
		}

		return choosedTrack;
	}

	private int getLevenshteinDistance(XTracks xtrack, Track track)
	{
		int trackDistance = StringUtils.getLevenshteinDistance(xtrack.getCleanedTrackName(), track.getSong());
		int artistDistance = getArtistDistance(track, xtrack);

	//	 System.out.println(" trackDistance = " + trackDistance + " artistDistance = " + artistDistance + " track = " + xtrack);

		return (trackDistance + 1) * (artistDistance + 1) + artistDistance;
	}

	private int getArtistDistance(final Track track, XTracks xtrack)
	{
		int artistDistance = 0;
		for ( String strArtist : xtrack.getAllArtists() )
		{
			Integer minDistance = getMinDistance(strArtist, track.getArtists());
			artistDistance += (minDistance == 0) ? 5 * (-xtrack.getAllArtists().size()) : minDistance;
		}
		artistDistance = artistDistance / xtrack.getAllArtists().size();
		return (artistDistance < 0) ? 0 : artistDistance;
	}

	private Integer getMinDistance(String strArtist, Set<String> artists)
	{
		Set<Integer> treeSet = new TreeSet<Integer>();
		for ( String artist : artists )
		{
			treeSet.add(StringUtils.getLevenshteinDistance(strArtist, artist));
		}
		return treeSet.iterator().next();
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
		digester.addCallMethod("tracks/track/artist/name", "addArtist", 0);
		digester.addBeanPropertySetter("tracks/track/album/availability/territories", "availability");
	}

	public static void main(String[] args) throws IOException, SpotifyException
	{
		//@formatter:off
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("p!nk)"),"blow me")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("sia","david guetta"),"titanium")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("janelle mon�e","fun."),"we are young ")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("kanye west","big sean", "pusha t", "2 chainz"),"mercy")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("selena gomez","the scene"),"love you like a love song")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("che'nelle"),"believe")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("dj khaled"),"take it to the head")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("jason mraz"),"i won't give up")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("adele"),"set fire to the rain")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("pitbull","shakira"),"get it started")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("lil wayne","big sean"),"my homies still")));
		//Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("kenny chesney"),"feel like a rock star")));
		Map<Track, String> trackHrefs = new SpotifyHrefQuery(null).getTrackHrefs(Sets.newHashSet(new Track(Sets.newHashSet("ne-yo","calvin harris"),"let's go")));
		//@formatter:on , 
		System.out.println(trackHrefs);
	}

}
