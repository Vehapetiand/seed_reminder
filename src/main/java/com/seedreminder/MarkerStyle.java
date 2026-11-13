package com.seedreminder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MarkerStyle
{
	ICON("Icon"),
	BADGE("Badge ('V' / 'B')"),
	OUTLINE("Outline"),
	ICON_AND_OUTLINE("Icon & Outline");

	private final String name;

	@Override
	public String toString()
	{
		return name;
	}
}
