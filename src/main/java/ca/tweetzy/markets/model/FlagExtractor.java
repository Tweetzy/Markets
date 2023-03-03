package ca.tweetzy.markets.model;

import lombok.experimental.UtilityClass;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class FlagExtractor {

	public Map<String, String> extract(String... args) {
		final Map<String, String> flags = new HashMap<>();
		String currentFlag = null;
		StringBuilder currentValues = new StringBuilder();

		for (String arg : args) {
			if (arg.startsWith("-")) {
				// arg is a flag
				if (currentFlag != null) {
					// store previous flag values
					flags.put(currentFlag, currentValues.toString().trim());
					currentValues.setLength(0);
				}
				currentFlag = arg;
			} else if (currentFlag != null) {
				// arg is a value for the current flag
				currentValues.append(arg).append(" ");
			}
		}

		if (currentFlag != null) {
			// store last flag values
			flags.put(currentFlag, currentValues.toString().trim());
		}

		return flags;
	}
}
