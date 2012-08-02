package com.mmounirou.spotirss.tools;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public final class StringTools
{
	private StringTools()
	{
		throw new AssertionError();
	}

	public static Set<String> split(String artist, String[] separators)
	{
		Set<String> result = Sets.newLinkedHashSet();
		result.add(artist);

		int previousSize = 0;
		do
		{
			previousSize = result.size();
			Set<String> temp = Sets.newHashSet(result);
			result.clear();
			for (String elmt : temp)
			{
				for (String separator : separators)
				{
					List<String> splitted = Arrays.asList(elmt.split(separator));
					if (splitted.size() > 1)
					{
						result.addAll(splitted);
					}
				}
			}
			if (result.isEmpty())
			{
				result = temp;
			}

		} while (previousSize != result.size());

		return Sets.newHashSet(Iterables.transform(result, new Function<String, String>()
		{

			@Override
			@Nullable
			public String apply(@Nullable String input)
			{
				return input.trim().toLowerCase();
			}
		}));
	}

}
