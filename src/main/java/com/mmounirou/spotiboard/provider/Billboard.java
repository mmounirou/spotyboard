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
package com.mmounirou.spotiboard.provider;

import javax.annotation.Nullable;

import org.apache.commons.lang.StringUtils;

import com.mmounirou.spotiboard.rss.Track;

public class Billboard implements EntryToTrackConverter
{

	@Override
	@Nullable
	public Track apply(@Nullable String strTitle)
	{
		int rankPos = strTitle.indexOf(":");
		String strRank = strTitle.substring(0, rankPos);
		String[] titleArtist = strTitle.substring(rankPos + 1).split(",");
		String strSong =StringUtils.remove(titleArtist[0],String.format("(%s)", StringUtils.substringBetween(titleArtist[0], "(", ")")));
		String strArtist = titleArtist[1];

		return new Track(Integer.parseInt(strRank), strArtist, strSong);

	}
	
	public static void main(String[] args)
	{
		System.out.println(new Billboard().apply("1:Drake Featuring Lil Wayne, HYFR (Hell Yeah F*****g Right)"));
	}

}
