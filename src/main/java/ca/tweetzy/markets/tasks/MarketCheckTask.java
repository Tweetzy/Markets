package ca.tweetzy.markets.tasks;

import ca.tweetzy.markets.Markets;
import ca.tweetzy.markets.market.Market;
import ca.tweetzy.markets.settings.Settings;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Iterator;

/**
 * The current file has been created by Kiran Hart
 * Date Created: July 18 2021
 * Time Created: 1:36 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
public class MarketCheckTask extends BukkitRunnable {

    private static MarketCheckTask instance;

    public static MarketCheckTask startTask() {
        if (instance == null) {
            instance = new MarketCheckTask();
            // give a little bit more time if loading from a database
            instance.runTaskTimerAsynchronously(Markets.getInstance(), Settings.DATABASE_USE.getBoolean() ? 20 * 25 : 20 * 10, (long) 20 * Settings.MARKET_CHECK_DELAY.getInt());
        }
        return instance;
    }

    @Override
    public void run() {
        if (Settings.UPKEEP_FEE_USE.getBoolean()) {
            long feeLastPaidOn = Markets.getInstance().getMarketManager().getFeeLastChargedOn();

            if (feeLastPaidOn == -1 || feeLastPaidOn + (1000L * Settings.UPKEEP_FEE_CHARGE_EVERY.getInt()) < System.currentTimeMillis()) {

                for (Market market : Markets.getInstance().getMarketManager().getMarkets()) {
                    if (market.isUnpaid()) continue;
                    OfflinePlayer player = Bukkit.getOfflinePlayer(market.getOwner());

                    if (!Markets.getInstance().getEconomyManager().has(player, Settings.UPKEEP_FEE_FEE.getDouble())) {
                        market.setUnpaid(true);
                        if (player.isOnline()) {
                            Markets.getInstance().getLocale().getMessage("upkeep_fee_not_paid").sendPrefixedMessage(player.getPlayer());
                        }
                    } else {
                        Markets.getInstance().getEconomyManager().withdrawPlayer(player, Settings.UPKEEP_FEE_FEE.getDouble());
                        Markets.getInstance().getLocale().getMessage("upkeep_fee_paid").processPlaceholder("upkeep_fee", String.format("%,.2f", Settings.UPKEEP_FEE_FEE.getDouble())).sendPrefixedMessage(player.getPlayer());
                    }
                }

                Markets.getInstance().getMarketManager().setFeeLastChargedOn(System.currentTimeMillis());
            }
        }
    }
}
