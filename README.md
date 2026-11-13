# Seed Reminder

A RuneLite plugin that tracks which seeds and saplings you have in your **Bank** and which ones you have in the **Seed Vault** (at the Farming Guild).

It displays a clean marker icon on each seed that belongs in the Seed Vault, saving you from guessing where your seeds should go and preventing your bank slots from being consumed by duplicate seed types!

---

## Features

- **Automated Seed Vault & Bank Tracking:** Automatically synchronizes and remembers what seeds and saplings you have stored in both your Bank and your Seed Vault as soon as you open either interface.
- **Account / Profile Persistence:** Remembers your seeds across client restarts and world hops per character profile.
- **Visual Vault Markers:** Displays a crisp custom vault icon (or letter badge/colored outline) on seeds in your inventory and bank that belong in your Seed Vault.
- **Optional Bank Markers:** Can optionally display a bank marker on seeds kept in your bank (e.g. active herb run seeds).
- **Informative Tooltips:** Hover over any seed in your inventory, bank, or vault to see the exact quantity stored in both your Seed Vault and your Bank, along with its destination.
- **Flexible Matching Rules:**
  - **Stored in Vault (Default):** Highlights seeds that currently exist in your Seed Vault. If the vault has not been opened yet, automatically falls back to highlighting seeds not currently in your bank.
  - **Not in Bank:** Highlights any seed not currently found in your regular bank.
  - **Vault or Not in Bank:** Highlights seeds present in your vault plus any newly acquired seed not in your bank.
  - **All Seeds:** Highlights all seeds and saplings that can be stored in the Seed Vault.
- **Full Customization:** Choose marker style (Icon, Badge, Outline, Icon & Outline), corner position (Top-Right, Top-Left, Bottom-Right, Bottom-Left), and custom marker colors.

---

## Usage

1. Open your **Bank** and **Seed Vault** once in-game to allow the plugin to scan and cache your stored seeds.
2. When gathering seeds from Farming contracts, Birdhouse runs, Slayer tasks, or Master Farmers, check your inventory:
   - Seeds marked with the **Seed Vault** icon belong in your Seed Vault at the Farming Guild.
   - Unmarked seeds (or bank-marked seeds) belong in your regular Bank.
3. Hover over any seed to view a tooltip displaying the exact amounts stored in both your Seed Vault and Bank.

---

## License

This project is licensed under the BSD 2-Clause License. See [LICENSE](LICENSE) for details.
