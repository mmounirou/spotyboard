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
package com.mmounirou.spoticharts;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.google.common.base.Charsets;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mmounirou.spoticharts.provider.EntryToTrackConverter;
import com.mmounirou.spoticharts.rss.ChartRss;
import com.mmounirou.spoticharts.rss.ChartRssException;
import com.mmounirou.spoticharts.rss.Track;
import com.mmounirou.spoticharts.spotify.SpotifyException;
import com.mmounirou.spoticharts.spotify.SpotifyHrefQuery;
import com.mmounirou.spoticharts.spotify.TrackCache;

public class SpotiRss
{
	public static final Logger LOGGER = Logger.getLogger(SpotiRss.class);
	public static final List<String> PROVIDERS = Arrays.asList("apple", "billboard");

	/**
	 * @param args
	 * @throws IOException 
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws ChartRssException 
	 * @throws SpotifyException 
	 */
	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException, ClassNotFoundException
	{
		if (args.length == 0)
		{
			System.err.println("usage : java -jar spotiboard.jar <charts-folder>");
			return;
		}

		final File outputDir = new File(args[0]);
		outputDir.mkdirs();
		TrackCache cache = new TrackCache();
		try
		{

			for (String strProvider : PROVIDERS)
			{
				String providerClassName = EntryToTrackConverter.class.getPackage().getName() + "." + StringUtils.capitalize(strProvider);
				final EntryToTrackConverter converter = (EntryToTrackConverter) SpotiRss.class.getClassLoader().loadClass(providerClassName).newInstance();
				Iterable<String> chartsRss = getCharts(strProvider);
				final File resultDir = new File(outputDir, strProvider);
				resultDir.mkdir();

				final SpotifyHrefQuery hrefQuery = new SpotifyHrefQuery(cache);
				Iterable<String> results = FluentIterable.from(chartsRss).transform(new Function<String, String>()
				{

					@Override
					@Nullable
					public String apply(@Nullable String chartRss)
					{

						try
						{

							long begin = System.currentTimeMillis();
							ChartRss bilboardChartRss = ChartRss.getInstance(chartRss, converter);
							Map<Track, String> trackHrefs = hrefQuery.getTrackHrefs(bilboardChartRss.getSongs());

							File resultFile = new File(resultDir, bilboardChartRss.getTitle());
							List<String> lines = Lists.newLinkedList(FluentIterable.from(trackHrefs.keySet()).transform(Functions.toStringFunction()));
							lines.addAll(trackHrefs.values());
							FileUtils.writeLines(resultFile, Charsets.UTF_8.displayName(), lines);

							LOGGER.info(String.format("%s chart exported in %s in %d s", bilboardChartRss.getTitle(), resultFile.getAbsolutePath(),
									(int) TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis() - begin)));

						} catch (Exception e)
						{
							LOGGER.error(String.format("fail to export %s charts", chartRss), e);
						}

						return "";
					}
				});

				// consume iterables
				Iterables.size(results);

			}

		} finally
		{
			cache.close();
		}

	}

	private static Iterable<String> getCharts(String strProvider) throws IOException
	{
		InputStream chartsStreams = SpotiRss.class.getResourceAsStream("/" + strProvider + ".charts");
		try
		{
			List<String> readLines = IOUtils.readLines(chartsStreams, Charsets.UTF_8);
			return Iterables.filter(readLines, new Predicate<String>()
			{

				@Override
				public boolean apply(@Nullable String input)
				{
					return !StringUtils.startsWith(input, "#");
				}
			});

		} finally
		{
			IOUtils.closeQuietly(chartsStreams);
		}
	}

}
