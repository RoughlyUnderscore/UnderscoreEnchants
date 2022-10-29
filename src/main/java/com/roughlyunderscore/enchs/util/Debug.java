package com.roughlyunderscore.enchs.util;

import com.roughlyunderscore.enchs.UnderscoreEnchants;
import lombok.*;
import org.bukkit.Bukkit;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Date;

@Data
public class Debug {
	private final boolean log;
	private final BufferedWriter writer;
	private final UnderscoreEnchants plugin;

	public void log(String message) {
		if (log) {
			Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
				try {
					writer.write("[" + Date.from(Instant.now()) + "] " + message + "\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}
	}
}
