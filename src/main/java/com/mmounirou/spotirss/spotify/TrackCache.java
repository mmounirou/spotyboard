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

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.time.DateUtils;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.io.Files;
import com.mmounirou.spotirss.rss.Track;

public class TrackCache implements Closeable
{
	private Map<Track, String> m_cache = Maps.newHashMap();
	private OutputStreamWriter stream;

	public TrackCache() throws IOException
	{
		File cacheDir = new File(FileUtils.getTempDirectory(), "spotify");
		cacheDir.mkdirs();
		File cacheFile = new File(cacheDir, "hrefCache");
		if (FileUtils.isFileOlder(cacheFile, DateUtils.addDays(new Date(), -1)))
		{
			cacheFile.delete();
			System.out.println("Delete cache ..." + cacheFile.getAbsolutePath());
		}

		if (cacheFile.exists())
		{
			System.out.println("load cache ..." + cacheFile.getAbsolutePath());
			List<String> strExtendedTracks = Files.readLines(cacheFile, Charsets.UTF_8);
			for (String strExtendedTrack : strExtendedTracks)
			{
				String[] extendedTrackSplitted = strExtendedTrack.split(",");
				String m_artist = extendedTrackSplitted[0];
				String m_song = extendedTrackSplitted[1];
				String m_href = extendedTrackSplitted[2];
				Track track = new Track(Sets.newHashSet(m_artist.split("&")), m_song);
				m_cache.put(track, m_href);
			}
		}

		stream = Files.newWriterSupplier(cacheFile, Charsets.UTF_8, true).getOutput();
	}

	public String get(Track track)
	{
		return m_cache.get(track);
	}

	public void put(Track track, String strHref)
	{
		m_cache.put(track, strHref);
		try
		{
			stream.write(String.format("%s,%s,%s\n", Joiner.on("&").join(track.getArtists()), track.getSong(), strHref));
		} catch (IOException e)
		{
			// just a try write
		}
	}

	@Override
	public void close() throws IOException
	{
		stream.close();
	}

}
