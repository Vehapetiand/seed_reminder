package com.seedreminder;

import com.google.gson.Gson;
import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.RuneScapeProfileChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "Seed Reminder",
	description = "Tracks seeds in your bank and seed vault, and shows which seeds go where",
	tags = {"seed", "vault", "bank", "farming", "reminder", "storage"}
)
public class SeedReminderPlugin extends Plugin
{
	static final String CONFIG_GROUP = "seedreminder";
	private static final String KEY_VAULT_SEEDS = "vaultSeeds";
	private static final String KEY_BANK_SEEDS = "bankSeeds";
	private static final String KEY_VAULT_SCANNED = "vaultScanned";
	private static final String KEY_BANK_SCANNED = "bankScanned";

	@Inject
	private Client client;

	@Inject
	private ItemManager itemManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ConfigManager configManager;

	@Inject
	private Gson gson;

	@Inject
	private SeedReminderConfig config;

	@Inject
	private SeedReminderOverlay overlay;

	private final Map<Integer, Integer> vaultSeeds = new ConcurrentHashMap<>();
	private final Map<Integer, Integer> bankSeeds = new ConcurrentHashMap<>();

	@Getter
	private boolean vaultScanned = false;

	@Getter
	private boolean bankScanned = false;

	@Override
	protected void startUp() throws Exception
	{
		overlayManager.add(overlay);
		loadStoredData();
		log.debug("Seed Reminder started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		overlayManager.remove(overlay);
		vaultSeeds.clear();
		bankSeeds.clear();
		vaultScanned = false;
		bankScanned = false;
		log.debug("Seed Reminder stopped!");
	}

	@Provides
	SeedReminderConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(SeedReminderConfig.class);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		if (event.getGameState() == GameState.LOGGED_IN)
		{
			loadStoredData();
		}
	}

	@Subscribe
	public void onRuneScapeProfileChanged(RuneScapeProfileChanged event)
	{
		loadStoredData();
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		int containerId = event.getContainerId();
		ItemContainer container = event.getItemContainer();
		if (container == null)
		{
			return;
		}

		if (containerId == InventoryID.SEED_VAULT)
		{
			updateVaultSeeds(container);
		}
		else if (containerId == InventoryID.BANK)
		{
			updateBankSeeds(container);
		}
	}

	private void updateVaultSeeds(ItemContainer container)
	{
		vaultSeeds.clear();
		Item[] items = container.getItems();
		if (items != null)
		{
			for (Item item : items)
			{
				if (item == null || item.getId() <= 0)
				{
					continue;
				}

				int canonicalId = itemManager.canonicalize(item.getId());
				vaultSeeds.merge(canonicalId, item.getQuantity(), Integer::sum);
			}
		}

		vaultScanned = true;
		saveVaultData();
		log.debug("Updated Seed Vault container: {} unique seeds tracked", vaultSeeds.size());
	}

	private void updateBankSeeds(ItemContainer container)
	{
		bankSeeds.clear();
		Item[] items = container.getItems();
		if (items != null)
		{
			for (Item item : items)
			{
				if (item == null || item.getId() <= 0)
				{
					continue;
				}

				int canonicalId = itemManager.canonicalize(item.getId());
				if (FarmingSeeds.isSeedOrSapling(canonicalId, itemManager, this))
				{
					bankSeeds.merge(canonicalId, item.getQuantity(), Integer::sum);
				}
			}
		}

		bankScanned = true;
		saveBankData();
		log.debug("Updated Bank container: {} unique seeds tracked", bankSeeds.size());
	}

	public boolean isSeedInVault(int canonicalId)
	{
		Integer count = vaultSeeds.get(canonicalId);
		return count != null && count > 0;
	}

	public int getVaultQuantity(int canonicalId)
	{
		return vaultSeeds.getOrDefault(canonicalId, 0);
	}

	public boolean isSeedInBank(int canonicalId)
	{
		Integer count = bankSeeds.get(canonicalId);
		return count != null && count > 0;
	}

	public int getBankQuantity(int canonicalId)
	{
		return bankSeeds.getOrDefault(canonicalId, 0);
	}

	public boolean isSeedForVault(int canonicalId)
	{
		if (!FarmingSeeds.isSeedOrSapling(canonicalId, itemManager, this))
		{
			return false;
		}

		switch (config.vaultMode())
		{
			case STORED_IN_VAULT:
				if (vaultScanned)
				{
					return isSeedInVault(canonicalId);
				}
				// If the vault has not been opened yet on this profile, fall back to marking seeds not in bank
				return !isSeedInBank(canonicalId);
			case NOT_IN_BANK:
				return !isSeedInBank(canonicalId);
			case VAULT_OR_NOT_IN_BANK:
				return isSeedInVault(canonicalId) || !isSeedInBank(canonicalId);
			case ALL_VAULT_ELIGIBLE:
				return true;
			default:
				return isSeedInVault(canonicalId);
		}
	}

	public boolean isSeedForBank(int canonicalId)
	{
		if (!FarmingSeeds.isSeedOrSapling(canonicalId, itemManager, this))
		{
			return false;
		}

		return isSeedInBank(canonicalId);
	}

	private void saveVaultData()
	{
		try
		{
			SeedStorageData data = new SeedStorageData();
			data.setSeeds(new HashMap<>(vaultSeeds));
			configManager.setRSProfileConfiguration(CONFIG_GROUP, KEY_VAULT_SEEDS, gson.toJson(data));
			configManager.setRSProfileConfiguration(CONFIG_GROUP, KEY_VAULT_SCANNED, Boolean.toString(vaultScanned));
		}
		catch (Exception e)
		{
			log.debug("Error saving vault data", e);
		}
	}

	private void saveBankData()
	{
		try
		{
			SeedStorageData data = new SeedStorageData();
			data.setSeeds(new HashMap<>(bankSeeds));
			configManager.setRSProfileConfiguration(CONFIG_GROUP, KEY_BANK_SEEDS, gson.toJson(data));
			configManager.setRSProfileConfiguration(CONFIG_GROUP, KEY_BANK_SCANNED, Boolean.toString(bankScanned));
		}
		catch (Exception e)
		{
			log.debug("Error saving bank data", e);
		}
	}

	private void loadStoredData()
	{
		vaultSeeds.clear();
		bankSeeds.clear();

		try
		{
			String vaultJson = configManager.getRSProfileConfiguration(CONFIG_GROUP, KEY_VAULT_SEEDS);
			if (vaultJson != null && !vaultJson.isEmpty())
			{
				SeedStorageData data = gson.fromJson(vaultJson, SeedStorageData.class);
				if (data != null && data.getSeeds() != null)
				{
					vaultSeeds.putAll(data.getSeeds());
				}
			}

			String vaultScannedStr = configManager.getRSProfileConfiguration(CONFIG_GROUP, KEY_VAULT_SCANNED);
			vaultScanned = Boolean.parseBoolean(vaultScannedStr) || !vaultSeeds.isEmpty();

			String bankJson = configManager.getRSProfileConfiguration(CONFIG_GROUP, KEY_BANK_SEEDS);
			if (bankJson != null && !bankJson.isEmpty())
			{
				SeedStorageData data = gson.fromJson(bankJson, SeedStorageData.class);
				if (data != null && data.getSeeds() != null)
				{
					bankSeeds.putAll(data.getSeeds());
				}
			}

			String bankScannedStr = configManager.getRSProfileConfiguration(CONFIG_GROUP, KEY_BANK_SCANNED);
			bankScanned = Boolean.parseBoolean(bankScannedStr) || !bankSeeds.isEmpty();
		}
		catch (Exception e)
		{
			log.debug("Error loading stored seed data", e);
		}
	}
}
