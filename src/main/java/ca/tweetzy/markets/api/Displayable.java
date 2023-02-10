package ca.tweetzy.markets.api;

import lombok.NonNull;

import java.util.List;

public interface Displayable {

	@NonNull String getDisplayName();

	@NonNull List<String> getDescription();

	void setDisplayName(@NonNull final String displayName);

	void setDescription(@NonNull final List<String> description);
}
