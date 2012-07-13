package com.mmounirou.spotiboard.spotify;

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
