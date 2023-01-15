package ca.tweetzy.markets.api.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public abstract class AbstractCurrency implements Chargeable {

	protected String owningPlugin;
	protected String currencyName;

	protected String displayName;

	public String getStoreableName() {
		return this.owningPlugin + "/" + this.currencyName + "/" + this.displayName;
	}
}
