package com.mmounirou.spotiboard.billboard;

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
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;

public class BilboardChartRss
{

	private final String m_title;
	private final ImmutableSet<Track> m_songs;

	public BilboardChartRss(String title, ImmutableSet<Track> songs)
	{
		m_title = title;
		m_songs = songs;
	}

	@SuppressWarnings("unchecked")
	public static BilboardChartRss getInstance(String strUrl) throws MalformedURLException, ChartRssException
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
					String strTitle = entry.getTitle();
					String[] rankExtendedTitle = strTitle.split(":");
					String strRank = rankExtendedTitle[0];
					String[] titleArtist = rankExtendedTitle[1].split(",");
					String strSong = titleArtist[0];
					String strArtist = titleArtist[1];

					return new Track(Integer.parseInt(strRank), strArtist, strSong);
				}
			}).toImmutableSortedSet(new Comparator<Track>()
			{

				@Override
				public int compare(Track o1, Track o2)
				{
					return Integer.valueOf(o1.getRank()).compareTo(Integer.valueOf(o2.getRank()));
				}
			});

			return new BilboardChartRss(title, songs);
		}
		catch ( IllegalArgumentException e )
		{
			throw new ChartRssException(e);
		}
		catch ( IOException e )
		{
			throw new ChartRssException(e);

		}
		catch ( FeedException e )
		{
			throw new ChartRssException(e);

		}
		catch ( FetcherException e )
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
		BilboardChartRss bilboardChartRss = BilboardChartRss.getInstance("http://www.billboard.com/rss/charts/hot-100");
		String title = bilboardChartRss.getTitle();
		System.out.println(title);
		System.out.println(Joiner.on("\n").join(bilboardChartRss.getSongs()));
	}
}
