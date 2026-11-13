package com.seedreminder;

import net.runelite.api.gameval.ItemID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class FarmingSeedsTest
{
	@Test
	public void testKnownSeedsRecognized()
	{
		assertTrue("Ranarr seed should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.RANARR_SEED, null, null));
		assertTrue("Torstol seed should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.TORSTOL_SEED, null, null));
		assertTrue("Magic tree seed should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.MAGIC_TREE_SEED, null, null));
		assertTrue("Yew seed should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.YEW_SEED, null, null));
		assertTrue("Snapdragon seed should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.SNAPDRAGON_SEED, null, null));
		assertTrue("Seaweed seed should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.SEAWEED_SEED, null, null));
		assertTrue("Acorn should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.ACORN, null, null));
		assertTrue("Magic sapling should be recognized", FarmingSeeds.isSeedOrSapling(ItemID.PLANTPOT_MAGIC_TREE_SAPLING, null, null));
	}

	@Test
	public void testNonSeedsRejected()
	{
		assertFalse("Null item manager without known seed should be false", FarmingSeeds.isSeedOrSapling(ItemID.ABYSSAL_WHIP, null, null));
		assertFalse("Non-seed weapon should not be recognized", FarmingSeeds.isSeedOrSapling(ItemID.DRAGON_DAGGER, null, null));
	}
}
