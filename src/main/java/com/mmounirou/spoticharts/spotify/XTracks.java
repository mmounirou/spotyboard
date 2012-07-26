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
package com.mmounirou.spoticharts.spotify;

public class XTracks
{
	private String m_trackName;
	private String m_artistName;
	private String m_availability;
	private String m_href;

	public String getTrackName()
	{
		return m_trackName;
	}

	public void setTrackName(String trackName)
	{
		m_trackName = trackName;
	}

	public String getArtistName()
	{
		return m_artistName;
	}

	public void setArtistName(String artistName)
	{
		m_artistName = artistName;
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
		return "XTracks [m_trackName=" + m_trackName + ", m_artistName=" + m_artistName + ", m_href=" + m_href + ", m_availability=" + m_availability + "]";
	}

}
