package ca.tweetzy.markets.model;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

import java.nio.ByteBuffer;
import java.util.UUID;

/*
https://www.spigotmc.org/threads/mysql-database-minecraft-uuid-too-long.586294/#post-4530423
 */
@UtilityClass
public final class UUIDHelper {

	public byte[] compress(@NonNull final UUID uuid) {
		final long mostSigBits = uuid.getMostSignificantBits();
		final long leastSigBits = uuid.getLeastSignificantBits();
		byte[] uuidBytes = new byte[16];

		ByteBuffer.wrap(uuidBytes).putLong(mostSigBits).putLong(leastSigBits);
		return uuidBytes;
	}

	public UUID unCompress(byte[] bytes) {
		final ByteBuffer bb = ByteBuffer.wrap(bytes);
		final long mostSigBits = bb.getLong();
		final long leastSigBits = bb.getLong();

		return new UUID(mostSigBits, leastSigBits);
	}
}
