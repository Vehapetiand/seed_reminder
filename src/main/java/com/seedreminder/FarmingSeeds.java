package com.seedreminder;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import net.runelite.api.ItemComposition;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.game.ItemManager;

public final class FarmingSeeds
{
	private FarmingSeeds()
	{
	}

	private static final Set<Integer> KNOWN_SEEDS = ImmutableSet.of(
		// Herbs
		ItemID.GUAM_SEED,
		ItemID.MARRENTILL_SEED,
		ItemID.TARROMIN_SEED,
		ItemID.HARRALANDER_SEED,
		ItemID.RANARR_SEED,
		ItemID.TOADFLAX_SEED,
		ItemID.IRIT_SEED,
		ItemID.AVANTOE_SEED,
		ItemID.KWUARM_SEED,
		ItemID.SNAPDRAGON_SEED,
		ItemID.CADANTINE_SEED,
		ItemID.LANTADYME_SEED,
		ItemID.DWARF_WEED_SEED,
		ItemID.TORSTOL_SEED,
		ItemID.HUASCA_SEED,

		// Allotment
		ItemID.POTATO_SEED,
		ItemID.ONION_SEED,
		ItemID.CABBAGE_SEED,
		ItemID.TOMATO_SEED,
		ItemID.SWEETCORN_SEED,
		ItemID.STRAWBERRY_SEED,
		ItemID.WATERMELON_SEED,
		ItemID.SNAPE_GRASS_SEED,

		// Flowers
		ItemID.MARIGOLD_SEED,
		ItemID.ROSEMARY_SEED,
		ItemID.NASTURTIUM_SEED,
		ItemID.WOAD_SEED,
		ItemID.LIMPWURT_SEED,
		ItemID.WHITE_LILY_SEED,

		// Hops
		ItemID.BARLEY_SEED,
		ItemID.HAMMERSTONE_HOP_SEED,
		ItemID.ASGARNIAN_HOP_SEED,
		ItemID.JUTE_SEED,
		ItemID.YANILLIAN_HOP_SEED,
		ItemID.KRANDORIAN_HOP_SEED,
		ItemID.WILDBLOOD_HOP_SEED,
		ItemID.FLAX_SEED,
		ItemID.HEMP_SEED,
		ItemID.COTTON_SEED,
		ItemID.KELDA_HOP_SEED,

		// Bushes
		ItemID.REDBERRY_BUSH_SEED,
		ItemID.CADAVABERRY_BUSH_SEED,
		ItemID.DWELLBERRY_BUSH_SEED,
		ItemID.JANGERBERRY_BUSH_SEED,
		ItemID.WHITEBERRY_BUSH_SEED,
		ItemID.POISONIVY_BUSH_SEED,

		// Trees
		ItemID.ACORN,
		ItemID.WILLOW_SEED,
		ItemID.MAPLE_SEED,
		ItemID.YEW_SEED,
		ItemID.MAGIC_TREE_SEED,
		ItemID.REDWOOD_TREE_SEED,
		ItemID.TEAK_SEED,
		ItemID.MAHOGANY_SEED,
		ItemID.CAMPHOR_SEED,
		ItemID.IRONWOOD_SEED,
		ItemID.ROSEWOOD_SEED,
		ItemID.CRYSTAL_TREE_SEED,

		// Fruit Trees
		ItemID.APPLE_TREE_SEED,
		ItemID.BANANA_TREE_SEED,
		ItemID.ORANGE_TREE_SEED,
		ItemID.CURRY_TREE_SEED,
		ItemID.PINEAPPLE_TREE_SEED,
		ItemID.PAPAYA_TREE_SEED,
		ItemID.PALM_TREE_SEED,
		ItemID.DRAGONFRUIT_TREE_SEED,

		// Special
		ItemID.SEAWEED_SEED,
		ItemID.MUSHROOM_SEED,
		ItemID.BELLADONNA_SEED,
		ItemID.CACTUS_SEED,
		ItemID.POTATO_CACTUS_SEED,
		ItemID.CALQUAT_TREE_SEED,
		ItemID.SPIRIT_TREE_SEED,
		ItemID.HESPORI_SEED,
		ItemID.GRAPE_SEED,
		ItemID.CELASTRUS_TREE_SEED,
		ItemID.ATTAS_SEED,
		ItemID.IASOR_SEED,
		ItemID.KRONOS_SEED,

		// Saplings
		ItemID.PLANTPOT_OAK_SAPLING,
		ItemID.PLANTPOT_WILLOW_SAPLING,
		ItemID.PLANTPOT_MAPLE_SAPLING,
		ItemID.PLANTPOT_YEW_SAPLING,
		ItemID.PLANTPOT_MAGIC_TREE_SAPLING,
		ItemID.PLANTPOT_SPIRIT_TREE_SAPLING,
		ItemID.PLANTPOT_APPLE_SAPLING,
		ItemID.PLANTPOT_BANANA_SAPLING,
		ItemID.PLANTPOT_ORANGE_SAPLING,
		ItemID.PLANTPOT_CURRY_SAPLING,
		ItemID.PLANTPOT_PINEAPPLE_SAPLING,
		ItemID.PLANTPOT_PAPAYA_SAPLING,
		ItemID.PLANTPOT_PALM_SAPLING,
		ItemID.PLANTPOT_CALQUAT_SAPLING,
		ItemID.PLANTPOT_TEAK_SAPLING,
		ItemID.PLANTPOT_MAHOGANY_SAPLING,
		ItemID.PLANTPOT_CELASTRUS_TREE_SAPLING,
		ItemID.PLANTPOT_REDWOOD_TREE_SAPLING,
		ItemID.PLANTPOT_DRAGONFRUIT_SAPLING,
		ItemID.PLANTPOT_CRYSTAL_TREE_SAPLING,
		ItemID.PLANTPOT_CAMPHOR_SAPLING,
		ItemID.PLANTPOT_IRONWOOD_SAPLING,
		ItemID.PLANTPOT_ROSEWOOD_SAPLING
	);

	public static boolean isSeedOrSapling(int canonicalId, ItemManager itemManager, SeedReminderPlugin plugin)
	{
		if (KNOWN_SEEDS.contains(canonicalId))
		{
			return true;
		}

		if (plugin != null && plugin.isSeedInVault(canonicalId))
		{
			return true;
		}

		if (itemManager == null)
		{
			return false;
		}

		ItemComposition comp = itemManager.getItemComposition(canonicalId);
		if (comp == null)
		{
			return false;
		}

		String name = comp.getName().toLowerCase();
		if (isNonFarmingItem(name))
		{
			return false;
		}

		return name.endsWith(" seed") || name.endsWith(" seeds")
			|| name.endsWith(" sapling") || name.endsWith(" seedling")
			|| name.endsWith(" spore") || name.endsWith(" spores")
			|| name.equals("acorn") || name.equals("crystal acorn");
	}

	private static boolean isNonFarmingItem(String lowerName)
	{
		return lowerName.contains("mithril seed")
			|| lowerName.contains("crystal weapon seed")
			|| lowerName.contains("crystal armour seed")
			|| lowerName.contains("crystal tool seed")
			|| lowerName.contains("teleport seed")
			|| lowerName.contains("consecration seed")
			|| lowerName.contains("white pearl seed")
			|| lowerName.contains("seed pod")
			|| lowerName.contains("seed box")
			|| lowerName.contains("seed packet")
			|| lowerName.contains("seed sack")
			|| lowerName.contains("seedsack")
			|| lowerName.contains("bird nest")
			|| lowerName.contains("crate")
			|| lowerName.contains("casket")
			|| lowerName.contains("skillguide")
			|| lowerName.contains("mulch")
			|| lowerName.contains("calcified acorn")
			|| lowerName.contains("dark acorn");
	}
}
