package ca.tweetzy.markets.model;

import ca.tweetzy.markets.settings.Settings;
import lombok.experimental.UtilityClass;

@UtilityClass
public final class Taxer {

	public double calculateTaxAmount(final double subtotal) {
		if (!Settings.TAX_ENABLED.getBoolean())
			return subtotal;

		return subtotal * Settings.TAX_AMOUNT.getDouble() / 100D;
	}

	public double getTaxedTotal(final double subtotal) {
		return subtotal + calculateTaxAmount(subtotal);
	}
}
