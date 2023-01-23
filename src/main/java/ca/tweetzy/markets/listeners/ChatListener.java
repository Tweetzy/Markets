package ca.tweetzy.markets.listeners;

import ca.tweetzy.flight.utils.Common;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public final class ChatListener implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent event){
		event.setMessage(Common.colorize(event.getMessage()));
	}
}
