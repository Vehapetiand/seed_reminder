package com.seedreminder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum IconPosition
{
	TOP_LEFT("Top Left"),
	TOP_RIGHT("Top Right"),
	BOTTOM_LEFT("Bottom Left"),
	BOTTOM_RIGHT("Bottom Right");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
