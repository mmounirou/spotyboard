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

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class XTracks
{
	private String m_trackName;
	private Set<String> m_artists = Sets.newHashSet();
	private Set<String> m_artistsInTrackName = Sets.newHashSet();

	private String m_availability;
	private String m_href;
	private String m_originalName;
	public static Set<String> allRemoved = Sets.newHashSet();

	public void setTrackName(String trackName)
	{
		m_originalName = trackName;
		m_trackName = cleanTrackName(trackName);
	}

	private String cleanTrackName(String trackName)
	{
		String strSong = StringUtils.remove(trackName, " - Explicit Version");
		strSong = StringUtils.remove(strSong, " - Live");

		String extendedinfo = null;
		do
		{
			extendedinfo = StringUtils.defaultString(StringUtils.substringBetween(strSong, "(", ")"));
			String[] artists = extendedinfo.split("feat.");
			if ( artists != null && artists.length > 1 )
			{
				strSong = StringUtils.replace(strSong, String.format("(%s)", extendedinfo), "");
				m_artistsInTrackName.addAll(cleanArtist(artists[1]));
			}
			else
			{
				strSong = StringUtils.replace(strSong, String.format("(%s)", extendedinfo), "x");
			}

		}
		while ( StringUtils.isNotBlank(extendedinfo) );

		String[] strSongSplitted = strSong.split("featuring");
		if ( strSongSplitted.length > 1 )
		{
			strSong = strSongSplitted[0];
			m_artistsInTrackName.add(strSongSplitted[1]);
		}

		String[] strSongWithFeaturing = strSong.split("-");
		if ( strSongWithFeaturing.length > 1 && strSongWithFeaturing[1].contains("feat.") )
		{
			strSong = strSongWithFeaturing[0];
			m_artistsInTrackName.add(StringUtils.remove(strSongWithFeaturing[1], "feat."));
		}
		return strSong.trim().toLowerCase();
	}

	public void addArtist(String artistName)
	{
		List<String> transform = cleanArtist(artistName);
		m_artists.addAll(transform);
	}

	private List<String> cleanArtist(String artistName)
	{
		Iterable<String> transform = FluentIterable.from(Lists.newArrayList(artistName.split("&"))).transform(new Function<String, String>()
		{

			@Override
			@Nullable
			public String apply(@Nullable String input)
			{
				return input.toLowerCase().trim();
			}
		});
		return Lists.newArrayList(transform);
	}

	public String getAvailability()
	{
		return m_availability;
	}

	public void setAvailability(String availability)
	{
		m_availability = availability;
	}

	public String getHref()
	{
		return m_href;
	}

	public void setHref(String href)
	{
		m_href = href;
	}

	public String getOriginalTrackName()
	{
		return m_originalName;
	}

	public String getCleanedTrackName()
	{
		return m_trackName;
	}

	public Set<String> getAllArtists()
	{
		return Sets.union(m_artists, m_artistsInTrackName);
	}
}
