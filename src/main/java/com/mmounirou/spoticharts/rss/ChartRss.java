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

package com.mmounirou.spoticharts.rss;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.List;

import javax.annotation.Nonnull;

import org.apache.commons.io.FileUtils;
import org.rometools.fetcher.FeedFetcher;
import org.rometools.fetcher.FetcherException;
import org.rometools.fetcher.impl.DiskFeedInfoCache;
import org.rometools.fetcher.impl.FeedFetcherCache;
import org.rometools.fetcher.impl.HttpURLFeedFetcher;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Predicates;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.mmounirou.spoticharts.SpotiRss;
import com.mmounirou.spoticharts.provider.Billboard;
import com.mmounirou.spoticharts.provider.EntryToTrackConverter;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

public class ChartRss
{

	private final String m_title;
	private final ImmutableSet<Track> m_songs;

	public ChartRss(String title, ImmutableSet<Track> songs)
	{
		m_title = title;
		m_songs = songs;
	}

	@SuppressWarnings("unchecked")
	public static ChartRss getInstance(String strUrl, final EntryToTrackConverter converter) throws MalformedURLException, ChartRssException
	{
		File rssCache = new File(FileUtils.getTempDirectory(), "billboard-charts");
		rssCache.mkdirs();

		FeedFetcherCache feedFetcherCache = new DiskFeedInfoCache(rssCache.getAbsolutePath());
		FeedFetcher feedFetcher = new HttpURLFeedFetcher(feedFetcherCache);

		URL feedUrl = new URL(strUrl);
		try
		{
			SyndFeed retrievedFeed = feedFetcher.retrieveFeed(feedUrl);
			String title = retrievedFeed.getTitle();
			ImmutableSortedSet<Track> songs = FluentIterable.from((List<SyndEntry>) retrievedFeed.getEntries()).transform(new Function<SyndEntry, Track>()
			{

				@Override
				@Nonnull
				public Track apply(@Nonnull SyndEntry entry)
				{
					try
					{
						return converter.apply(entry.getTitle());
					} catch (Exception e)
					{
						SpotiRss.LOGGER.error(String.format("fail to parse %s ", entry.getTitle()));
						return null;
					}

				}

			}).filter(Predicates.notNull()).toImmutableSortedSet(new Comparator<Track>()
			{

				@Override
				public int compare(Track o1, Track o2)
				{
					return Integer.valueOf(o1.getRank()).compareTo(Integer.valueOf(o2.getRank()));
				}
			});

			return new ChartRss(title, songs);
		} catch (IllegalArgumentException e)
		{
			throw new ChartRssException(e);
		} catch (IOException e)
		{
			throw new ChartRssException(e);

		} catch (FeedException e)
		{
			throw new ChartRssException(e);

		} catch (FetcherException e)
		{
			throw new ChartRssException(e);
		}

	}

	public String getTitle()
	{
		return m_title;
	}

	public ImmutableSet<Track> getSongs()
	{
		return m_songs;
	}

	public static void main(String[] args) throws MalformedURLException, ChartRssException
	{
		ChartRss bilboardChartRss = ChartRss.getInstance("http://www.billboard.com/rss/charts/hot-100", new Billboard());
		ImmutableSet<Track> songs = bilboardChartRss.getSongs();
		System.out.println(Joiner.on("\n").join(songs));
	}
}
