package vg.civcraft.mc.civmodcore.scoreboard.bottom;

import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import vg.civcraft.mc.civmodcore.CivModCorePlugin;

public class BottomLine implements Comparable<BottomLine>{
	
	private Map <UUID, String> texts;
	private String identifier;
	private BukkitRunnable updater;
	private int priority;
	
	BottomLine(String identifier, int priority) {
		this.identifier = identifier;
		this.priority = priority;
		this.texts = new TreeMap<>();
	}
	
	public String getIdentifier() {
		return identifier;
	}
	
	public void updatePlayer(Player player, String text) {
		texts.put(player.getUniqueId(), text);
	}
	
	public String getCurrentText(UUID uuid) {
		return texts.get(uuid);
	}
	
	public void updatePeriodically(BiFunction<Player, String, String> updateFunction, long delay) {
		if (updater != null) {
			updater.cancel();
		}
		updater = new BukkitRunnable() {

			@Override
			public void run() {
				for (Entry<UUID, String> entry : texts.entrySet()) {
					Player player = Bukkit.getPlayer(entry.getKey());
					if (player != null) {
						String newText = updateFunction.apply(player, entry.getValue());
						if (!newText.equals(entry.getValue()) ) {
							entry.setValue(newText);
							BottomLineAPI.refreshIndividually(player.getUniqueId());
						}
						
					}
				}
			}
		};
		updater.runTaskTimer(CivModCorePlugin.getInstance(), delay, delay);
	}
	
	public void removePlayer(Player player) {
		texts.remove(player.getUniqueId());
		BottomLineAPI.refreshIndividually(player.getUniqueId());
	}
	
	Map<UUID, String> getAll() {
		return texts;
	}
	
	@Override
	public boolean equals(Object o) {
		return o == this;
	}

	@Override
	public int compareTo(BottomLine o) {
		int prio = Integer.compare(priority, o.priority);
		if (prio != 0) {
			return prio;
		}
		return Integer.compare(hashCode(), o.hashCode());
	}

}
