package com.roughlyunderscore.enchs.parsers;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.entity.Player;

@EqualsAndHashCode @ToString
public class PDCPlaceholder {
	@Getter private final Player[] players;
	@Getter private final int size;

	public PDCPlaceholder(Player... players) {
		this.players = players;
		this.size = players.length;
	}
}
