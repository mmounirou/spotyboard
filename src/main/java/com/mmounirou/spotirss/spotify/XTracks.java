/*
 * Copyright (C) 2011 Mohamed MOUNIROU
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package com.mmounirou.spotirss.spotify;

import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

public class XTracks
{
	private String m_trackName;
	private Set<String> m_artists = Sets.newHashSet();
	private String m_availability;
	private String m_href;

	public String getTrackName()
	{
		return m_trackName;
	}

	public void setTrackName(String trackName)
	{
		String strSong = StringUtils.replace(trackName, String.format("(%s)", StringUtils.substringBetween(trackName, "(", ")")), "x");
		String[] strSongSplitted = strSong.split("featuring");

		if(strSongSplitted.length > 1)
		{
			strSong = strSongSplitted[0];
			addArtist(strSongSplitted[1]);
		}
		
		String[] strSongWithFeaturing = strSong.split("-");
		if (strSongWithFeaturing.length > 1 && strSongWithFeaturing[1].contains("feat."))
		{
			strSong = strSongWithFeaturing[0];
			addArtist(StringUtils.remove(strSongWithFeaturing[1], "feat."));
		}
		m_trackName = strSong.trim().toLowerCase();
	}

	public Set<String> getArtists()
	{
		return m_artists;
	}

	public void addArtist(String artistName)
	{
		String[] strArtists = artistName.split("&");
		for (String strArtist : strArtists)
		{
			m_artists.add(strArtist.trim().toLowerCase());
		}
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

	@Override
	public String toString()
	{
		return "XTracks [m_trackName=" + m_trackName + ", m_artists=" + Joiner.on(" & ").join(m_artists) + ", m_href=" + m_href + ", m_availability=" + m_availability + "]";
	}

}
