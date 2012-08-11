package com.mmounirou.spotirss.json;

import java.util.List;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

public class Playlist
{
	private String creator;
	private String subscriberCount;
	private String uri;
	private String title;
	private String description;
	private boolean collaborative;
	private List<String> tracks = Lists.newArrayList();

	public String getUri()
	{
		return uri;
	}

	public void setUri(String uri)
	{
		this.uri = uri;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
	}

	public boolean isCollaborative()
	{
		return collaborative;
	}

	public void setCollaborative(boolean collaborative)
	{
		this.collaborative = collaborative;
	}

	public String getCreator()
	{
		return creator;
	}

	public void setCreator(String creator)
	{
		this.creator = creator;
	}

	public String getSubscriberCount()
	{
		return subscriberCount;
	}

	public void setSubscriberCount(String subscriberCount)
	{
		this.subscriberCount = subscriberCount;
	}

	public List<String> getTracks()
	{
		return tracks;
	}

	public void setTracks(List<String> tracks)
	{
		this.tracks = tracks;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}

	public String tracksToJson()
	{
		return String.format("[%s]", Joiner.on(",").join(Iterables.transform(getTracks(), new Function<String, String>()
		{

			@Override
			@Nullable
			public String apply(@Nullable String input)
			{
				return String.format("\"%s\"", input);
			}
		})));
	}
}
