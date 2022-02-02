package ca.tweetzy.markets.commands;

import ca.tweetzy.core.commands.AbstractCommand;
import ca.tweetzy.markets.settings.Settings;
import lombok.experimental.UtilityClass;

/**
 * The current file has been created by Kiran Hart
 * Date Created: February 02 2022
 * Time Created: 11:55 a.m.
 * Usage of any code found within this class is prohibited unless given explicit permission otherwise
 */
@UtilityClass
public final class CommandMiddleware {

	public boolean handle() {
		return !Settings.REQUEST_ONLY_MODE.getBoolean();
	}
}
