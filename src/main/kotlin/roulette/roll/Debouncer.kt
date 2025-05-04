package moe.best.kimoneri.roulette.roll

import com.google.common.cache.CacheBuilder
import org.slf4j.LoggerFactory
import java.time.LocalTime
import java.util.concurrent.TimeUnit

object Debouncer {

    private val cache = CacheBuilder.newBuilder().apply {
        expireAfterWrite(15, TimeUnit.SECONDS)
    }.build<Long, LocalTime>()

    private val logger = LoggerFactory.getLogger(Debouncer::class.java)

    // TODO: Use a LinkedHashMap with automatic size limits instead of a expiring cache.
    // private val cache2 = object : LinkedHashMap<Long, LocalTime>(50) {
    //    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<Long, LocalTime>?) = this.size > 50
    // }

    /**
     * Returns whether the message author recently rolled.
     *
     * Note: This debouncer assumes a minimum period of 10 seconds.
     * Rolls should have an effect with a minimum of 1 minute.
     */
    fun shouldDebounce(messageAuthorId: Long): Boolean {
        val now = LocalTime.now()

        cache.cleanUp()

        // Check if a user is already in the debounce cache.
        val debounceExpiryTime: LocalTime? = cache.getIfPresent(messageAuthorId)
        if (debounceExpiryTime == null) {
            logger.debug("$messageAuthorId not found in debounce cache, adding.")
            cache.put(messageAuthorId, now.plusSeconds(10))
            return false
        }

        // There can be older values in the cache, so check if the current time is already after that value.
        if (now.isAfter(debounceExpiryTime)) {
            logger.debug("$messageAuthorId has an expired debounce time, refreshing.")
            cache.put(messageAuthorId, now.plusSeconds(10))
            return false
        }

        logger.debug("Should debounce latest roll from $messageAuthorId.")
        return true

    }
}