package io.eliotesta98.CustomAnvilGUI.Interfaces;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class CustomAnvilGUIHolder implements InventoryHolder {

	private Inventory inv;

	public CustomAnvilGUIHolder(int size, String title) {
		inv = Bukkit.createInventory(this, size, title);
	}

	@Override
	public Inventory getInventory() {
		return inv;
	}

}