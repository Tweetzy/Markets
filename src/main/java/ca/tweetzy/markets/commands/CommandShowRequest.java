package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.guis.requests.GUIOpenRequests;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.request.Request;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The current file has been created by Kiran Hart
 * Date Created: August 11 2021
 * Time Created: 9:49 p.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class CommandShowRequest extends AbstractCommand {

	public CommandShowRequest() {
		super(CommandType.PLAYER_ONLY, "show request");
	}

	@Override
	protected ReturnType runCommand(CommandSender sender, String... args) {
		if (args.length < 1) return ReturnType.SYNTAX_ERROR;
		Player player = (Player) sender;

		OfflinePlayer target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(offlinePlayer -> offlinePlayer.hasPlayedBefore() && offlinePlayer.getName().equalsIgnoreCase(args[0])).findFirst().orElse(null);
		if (target == null) {
			Markets.getInstance().getLocale().getMessage("player_not_found").sendPrefixedMessage(sender);
			return ReturnType.FAILURE;
		}

		Markets.newChain().asyncFirst(() -> {
			List<Request> requests = Markets.getInstance().getRequestManager().getNonFulfilledRequests().stream().filter(request -> request.getRequester().equals(target.getUniqueId())).collect(Collectors.toList());

			if (requests.size() == 0) {
				Markets.getInstance().getLocale().getMessage("player_does_not_have_requests").sendPrefixedMessage(sender);
				return new ArrayList<Request>();
			}

			if (args.length == 2 && args[1].equalsIgnoreCase("-L")) {
				return Collections.singletonList(requests.stream().sorted(Comparator.comparingLong(Request::getDate).reversed()).findFirst().orElse(null));
			}

			return requests;
		}).syncLast(requests -> {
			if (!requests.isEmpty())
				Markets.getInstance().getGuiManager().showGUI(player, new GUIOpenRequests(player, requests));
		}).execute();

		return ReturnType.SUCCESS;
	}

	@Override
	protected List<String> onTab(CommandSender sender, String... args) {
		if (args.length == 1 && Markets.getInstance().getMarketManager().getMarkets().size() != 0)
			return Markets.getInstance().getMarketManager().getMarkets().stream().map(Market::getOwnerName).collect(Collectors.toList());
		return null;
	}

	@Override
	public String getPermissionNode() {
		return "markets.cmd.showrequest";
	}

	@Override
	public String getSyntax() {
		return Markets.getInstance().getLocale().getMessage("command_syntax.showrequest").getMessage();
	}

	@Override
	public String getDescription() {
		return Markets.getInstance().getLocale().getMessage("command_description.showrequest").getMessage();
	}
}
