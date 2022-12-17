/*
 * Flight
 * Copyright 2022 Kiran Hart
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ca.tweetzy.markets.utils.input;

import ca.tweetzy.markets.utils.ActionBar;
import ca.tweetzy.markets.utils.Titles;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * The current file has been created by Kiran Hart
 * Date Created: November 08 2021
 * Time Created: 4:44 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public abstract class Input implements Listener, Runnable {

    private final Player player;
    private String title;
    private String subtitle;

    private final BukkitTask task;

    public Input(@NonNull final JavaPlugin plugin, @NonNull final Player player) {
        this.player = player;
        Bukkit.getServer().getScheduler().runTaskLater(plugin, player::closeInventory, 1L);
        this.task = Bukkit.getServer().getScheduler().runTaskTimer(plugin, this, 1L, 1L);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void onExit(final Player player) {
    }

    public abstract boolean onInput(final String input);

    public abstract String getTitle();

    public abstract String getSubtitle();

    public abstract String getActionBar();

    @Override
    public void run() {
        final String title = this.getTitle();
        final String subTitle = this.getSubtitle();
        final String actionBar = this.getActionBar();

        if (this.title == null || this.subtitle == null || !this.title.equals(title) || !this.subtitle.equals(subTitle)) {
            Titles.sendTitle(this.player, 10, 6000, 0, title, subTitle);
            this.title = title;
            this.subtitle = subTitle;
        }

        if (actionBar != null)
            ActionBar.sendActionBar(this.player, actionBar);
    }

    @EventHandler(
            priority = EventPriority.LOWEST
    )
    public void onChat(AsyncPlayerChatEvent e) {
        if (e.getPlayer().equals(this.player)) {
            this.onInput(e.getMessage());
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void close(PlayerQuitEvent e) {
        if (e.getPlayer().equals(this.player)) {
            this.close(false);
        }
    }

    @EventHandler
    public void close(InventoryOpenEvent e) {
        if (e.getPlayer().equals(this.player)) {
            this.close(false);
        }
    }

    public void close(boolean completed) {
        HandlerList.unregisterAll(this);
        this.task.cancel();
        if (!completed) {
            this.onExit(this.player);
        }

        Titles.clearTitle(this.player);
        ActionBar.clearActionBar(this.player);
    }
}
