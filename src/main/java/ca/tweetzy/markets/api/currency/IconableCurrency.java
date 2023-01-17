package ca.tweetzy.markets.api.currency;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public abstract class IconableCurrency extends AbstractCurrency {

	@Getter
	@Setter
	protected ItemStack icon;

	public IconableCurrency(String owningPlugin, String currencyName, String displayName, ItemStack icon) {
		super(owningPlugin, currencyName, displayName);
		this.icon = icon;
	}
}
