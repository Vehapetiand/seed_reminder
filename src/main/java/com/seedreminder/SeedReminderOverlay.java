package com.seedreminder;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.ItemComposition;
import net.runelite.api.Point;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.api.widgets.WidgetUtil;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.WidgetItemOverlay;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;

@Slf4j
@Singleton
public class SeedReminderOverlay extends WidgetItemOverlay
{
	private final Client client;
	private final ItemManager itemManager;
	private final SeedReminderPlugin plugin;
	private final SeedReminderConfig config;
	private final TooltipManager tooltipManager;

	private final BufferedImage vaultIcon;
	private final BufferedImage bankIcon;

	@Inject
	public SeedReminderOverlay(
		Client client,
		ItemManager itemManager,
		SeedReminderPlugin plugin,
		SeedReminderConfig config,
		TooltipManager tooltipManager)
	{
		this.client = client;
		this.itemManager = itemManager;
		this.plugin = plugin;
		this.config = config;
		this.tooltipManager = tooltipManager;

		showOnInventory();
		showOnBank();
		showOnInterfaces(InterfaceID.SEED_VAULT);

		BufferedImage vIcon = null;
		BufferedImage bIcon = null;
		try
		{
			vIcon = ImageUtil.loadImageResource(SeedReminderPlugin.class, "vault_icon.png");
			bIcon = ImageUtil.loadImageResource(SeedReminderPlugin.class, "bank_icon.png");
		}
		catch (Exception e)
		{
			log.debug("Could not load icon images", e);
		}
		this.vaultIcon = vIcon;
		this.bankIcon = bIcon;
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		int canonicalId = itemManager.canonicalize(itemId);
		if (!FarmingSeeds.isSeedOrSapling(canonicalId, itemManager, plugin))
		{
			return;
		}

		if (widgetItem == null || widgetItem.getWidget() == null)
		{
			return;
		}

		int interfaceId = WidgetUtil.componentToInterface(widgetItem.getWidget().getId());
		if (interfaceId == InterfaceID.BANKMAIN)
		{
			if (!config.showOnBank())
			{
				return;
			}
		}
		else
		{
			if (!config.showOnInventory())
			{
				return;
			}
		}

		boolean isVaultSeed = plugin.isSeedForVault(canonicalId);
		boolean isBankSeed = plugin.isSeedForBank(canonicalId);
		boolean showVault = config.showVaultIcon() && isVaultSeed;
		boolean showBank = config.showBankIcon() && isBankSeed && !isVaultSeed;

		Rectangle bounds = widgetItem.getCanvasBounds();
		if (bounds == null)
		{
			return;
		}

		if (config.showTooltips())
		{
			Point mousePos = client.getMouseCanvasPosition();
			if (mousePos != null && bounds.contains(mousePos.getX(), mousePos.getY()))
			{
				renderTooltip(canonicalId, showVault, isBankSeed);
			}
		}

		if (showVault)
		{
			drawMarker(graphics, bounds, vaultIcon, config.vaultColor(), "V");
		}
		else if (showBank)
		{
			drawMarker(graphics, bounds, bankIcon, config.bankColor(), "B");
		}
	}

	private void renderTooltip(int canonicalId, boolean isVaultSeed, boolean isBankSeed)
	{
		ItemComposition comp = itemManager.getItemComposition(canonicalId);
		String name = comp != null ? comp.getName() : "Seed";
		StringBuilder sb = new StringBuilder();
		sb.append(ColorUtil.wrapWithColorTag(name, Color.WHITE)).append("</br>");

		int vaultQty = plugin.getVaultQuantity(canonicalId);
		if (plugin.isVaultScanned())
		{
			if (vaultQty > 0)
			{
				sb.append("Seed Vault: ").append(ColorUtil.wrapWithColorTag(vaultQty + " stored", Color.GREEN));
			}
			else
			{
				sb.append("Seed Vault: ").append(ColorUtil.wrapWithColorTag("0 stored", Color.GRAY));
			}
		}
		else
		{
			sb.append("Seed Vault: ").append(ColorUtil.wrapWithColorTag("Not yet opened", Color.ORANGE));
		}
		sb.append("</br>");

		int bankQty = plugin.getBankQuantity(canonicalId);
		if (plugin.isBankScanned())
		{
			if (bankQty > 0)
			{
				sb.append("Bank: ").append(ColorUtil.wrapWithColorTag(bankQty + " stored", Color.YELLOW));
			}
			else
			{
				sb.append("Bank: ").append(ColorUtil.wrapWithColorTag("0 stored", Color.GRAY));
			}
		}
		else
		{
			sb.append("Bank: ").append(ColorUtil.wrapWithColorTag("Not yet opened", Color.ORANGE));
		}
		sb.append("</br>");

		if (isVaultSeed)
		{
			sb.append("Target: ").append(ColorUtil.wrapWithColorTag("Seed Vault", Color.GREEN));
		}
		else if (isBankSeed)
		{
			sb.append("Target: ").append(ColorUtil.wrapWithColorTag("Bank", Color.YELLOW));
		}

		tooltipManager.add(new Tooltip(sb.toString()));
	}

	private void drawMarker(Graphics2D graphics, Rectangle bounds, BufferedImage icon, Color color, String badgeLetter)
	{
		MarkerStyle style = config.markerStyle();
		if (style == MarkerStyle.OUTLINE || style == MarkerStyle.ICON_AND_OUTLINE)
		{
			graphics.setColor(color);
			graphics.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
		}

		if (style == MarkerStyle.ICON || style == MarkerStyle.ICON_AND_OUTLINE)
		{
			if (icon != null)
			{
				Point loc = getIconLocation(bounds, icon.getWidth(), icon.getHeight());
				graphics.drawImage(icon, loc.getX(), loc.getY(), null);
			}
			else
			{
				drawBadge(graphics, bounds, color, badgeLetter);
			}
		}
		else if (style == MarkerStyle.BADGE)
		{
			drawBadge(graphics, bounds, color, badgeLetter);
		}
	}

	private void drawBadge(Graphics2D graphics, Rectangle bounds, Color color, String letter)
	{
		int badgeSize = 12;
		Point loc = getIconLocation(bounds, badgeSize, badgeSize);
		int x = loc.getX();
		int y = loc.getY();

		graphics.setColor(new Color(0, 0, 0, 180));
		graphics.fillRoundRect(x, y, badgeSize, badgeSize, 3, 3);

		graphics.setColor(color);
		graphics.fillRoundRect(x + 1, y + 1, badgeSize - 2, badgeSize - 2, 2, 2);

		graphics.setFont(FontManager.getRunescapeSmallFont());
		FontMetrics fm = graphics.getFontMetrics();
		int textX = x + (badgeSize - fm.stringWidth(letter)) / 2;
		int textY = y + ((badgeSize - fm.getHeight()) / 2) + fm.getAscent();

		graphics.setColor(Color.BLACK);
		graphics.drawString(letter, textX + 1, textY + 1);
		graphics.setColor(Color.WHITE);
		graphics.drawString(letter, textX, textY);
	}

	private Point getIconLocation(Rectangle bounds, int iconWidth, int iconHeight)
	{
		int x = bounds.x;
		int y = bounds.y;
		int pad = 1;

		switch (config.iconPosition())
		{
			case TOP_LEFT:
				return new Point(x + pad, y + pad);
			case TOP_RIGHT:
				return new Point(x + bounds.width - iconWidth - pad, y + pad);
			case BOTTOM_LEFT:
				return new Point(x + pad, y + bounds.height - iconHeight - pad);
			case BOTTOM_RIGHT:
				return new Point(x + bounds.width - iconWidth - pad, y + bounds.height - iconHeight - pad);
			default:
				return new Point(x + bounds.width - iconWidth - pad, y + pad);
		}
	}
}
