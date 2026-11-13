package com.seedreminder;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class SeedStorageData
{
	private Map<Integer, Integer> seeds = new HashMap<>();
}
