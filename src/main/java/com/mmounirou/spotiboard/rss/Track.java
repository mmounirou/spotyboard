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
package com.mmounirou.spotiboard.rss;

import javax.annotation.Nonnull;

import com.google.common.base.Objects;

public class Track
{

	protected static final Track NULL = new Track("", "");
	private final int m_rank;
	private final String m_artist;
	private final String m_song;

	public Track(@Nonnull int rank, @Nonnull String strArtist, @Nonnull String strSong)
	{
		m_rank = rank;
		m_artist = strArtist;
		m_song = strSong;
	}

	public Track(@Nonnull String strArtist, @Nonnull String strSong)
	{
		this(-1, strArtist, strSong);
	}

	public int getRank()
	{
		return m_rank;
	}

	public String getArtist()
	{
		return m_artist;
	}

	public String getSong()
	{
		return m_song;
	}

	@Override
	public int hashCode()
	{
		return Objects.hashCode(m_artist, m_song);
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
		return Objects.equal(m_artist, other.m_artist) && Objects.equal(m_song, other.m_song);
	}

	@Override
	public String toString()
	{
		return String.format("%d : %s , %s", m_rank, m_artist, m_song);
	}

}
