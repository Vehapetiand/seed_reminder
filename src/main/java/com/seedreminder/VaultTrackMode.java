package com.seedreminder;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VaultTrackMode
{
	STORED_IN_VAULT("Stored in Vault", "Only marks seeds that currently exist in your Seed Vault"),
	NOT_IN_BANK("Not in Bank", "Marks any seed not currently in your bank as belonging in the vault"),
	VAULT_OR_NOT_IN_BANK("Vault or Not in Bank", "Marks seeds in your vault, plus any seed not in your bank"),
	ALL_VAULT_ELIGIBLE("All Seeds", "Marks all seeds and saplings that can be stored in the Seed Vault");

	private final String name;
	private final String description;

	@Override
	public String toString()
	{
		return name;
	}
}
