package ca.tweetzy.markets.api.currency;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@AllArgsConstructor
public abstract class AbstractCurrency implements Chargeable {

	protected String owningPlugin;
	protected String currencyName;

	@Setter
	protected String displayName;

	@Setter
	@Getter
	protected boolean isVault;

	public String getStoreableName() {
		return this.owningPlugin + "/" + this.currencyName + "/" + this.displayName;
	}
}
