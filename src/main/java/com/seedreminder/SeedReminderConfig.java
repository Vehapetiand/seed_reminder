package com.seedreminder;

import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("seedreminder")
public interface SeedReminderConfig extends Config
{
	@ConfigSection(
		name = "Matching Rules",
		description = "How seeds are matched to the Seed Vault and Bank",
		position = 0
	)
	String rulesSection = "rules";

	@ConfigSection(
		name = "Display Options",
		description = "Visual appearance and placement of markers",
		position = 1
	)
	String displaySection = "display";

	@ConfigItem(
		keyName = "vaultMode",
		name = "Vault Matching Rule",
		description = "Which seeds should display the Seed Vault marker",
		position = 0,
		section = rulesSection
	)
	default VaultTrackMode vaultMode()
	{
		return VaultTrackMode.STORED_IN_VAULT;
	}

	@ConfigItem(
		keyName = "showVaultIcon",
		name = "Show Vault Marker",
		description = "Display a marker on seeds that belong in the Seed Vault",
		position = 1,
		section = displaySection
	)
	default boolean showVaultIcon()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showBankIcon",
		name = "Show Bank Marker",
		description = "Display a marker on seeds that are stored in your Bank",
		position = 2,
		section = displaySection
	)
	default boolean showBankIcon()
	{
		return false;
	}

	@ConfigItem(
		keyName = "showOnInventory",
		name = "Show in Inventory",
		description = "Display markers on seeds in your inventory",
		position = 3,
		section = displaySection
	)
	default boolean showOnInventory()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showOnBank",
		name = "Show in Bank",
		description = "Display markers on seeds inside the bank interface",
		position = 4,
		section = displaySection
	)
	default boolean showOnBank()
	{
		return true;
	}

	@ConfigItem(
		keyName = "markerStyle",
		name = "Marker Style",
		description = "Visual marker style (icon, badge letter, outline, etc.)",
		position = 5,
		section = displaySection
	)
	default MarkerStyle markerStyle()
	{
		return MarkerStyle.ICON;
	}

	@ConfigItem(
		keyName = "iconPosition",
		name = "Marker Position",
		description = "Corner of the item slot to place the marker",
		position = 6,
		section = displaySection
	)
	default IconPosition iconPosition()
	{
		return IconPosition.TOP_RIGHT;
	}

	@ConfigItem(
		keyName = "showTooltips",
		name = "Show Tooltips",
		description = "Display Seed Vault and Bank quantity details when hovering over seeds",
		position = 7,
		section = displaySection
	)
	default boolean showTooltips()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "vaultColor",
		name = "Vault Marker Color",
		description = "Color used for badge and outline markers for Seed Vault items",
		position = 8,
		section = displaySection
	)
	default Color vaultColor()
	{
		return new Color(46, 125, 50, 220);
	}

	@Alpha
	@ConfigItem(
		keyName = "bankColor",
		name = "Bank Marker Color",
		description = "Color used for badge and outline markers for Bank items",
		position = 9,
		section = displaySection
	)
	default Color bankColor()
	{
		return new Color(255, 193, 7, 220);
	}
}
