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
package com.mmounirou.spotirss.rss;

import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.collect.Sets;

public class Track
{

	protected static final Track NULL = new Track(Sets.<String>newTreeSet(), "");
	private final int m_rank;
	private final Set<String> m_artists = Sets.newHashSet();
	private final String m_song;

	public Track(@Nonnull int rank, @Nonnull Set<String> strArtists, @Nonnull String strSong)
	{
		m_rank = rank;
		m_artists.addAll(strArtists);
		m_song = strSong.trim().toLowerCase();
	}

	public Track(@Nonnull Set<String> strArtists, @Nonnull String strSong)
	{
		this(-1, strArtists, strSong);
	}

	public int getRank()
	{
		return m_rank;
	}

	public Set<String> getArtists()
	{
		return m_artists;
	}

	public String getSong()
	{
		return m_song;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(m_artists, m_song);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
		{
			return true;
		}
		if (obj == null)
		{
			return false;
		}
		if (!(obj instanceof Track))
		{
			return false;
		}
		Track other = (Track) obj;
		return Objects.equal(m_song, other.m_song) && m_artists.containsAll(other.m_artists) && other.m_artists.containsAll(m_artists);
	}

	@Override
	public String toString()
	{
		return String.format("%d : %s , %s", m_rank, Joiner.on(" & ").join(m_artists), m_song);
	}

}
