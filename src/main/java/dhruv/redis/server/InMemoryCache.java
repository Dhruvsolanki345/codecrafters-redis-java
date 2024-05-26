package dhruv.redis.server;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/*
 * Two diff approaches for ttl cleanup
 * 1. run a job every second to check, and delete expire key based on ttl
 * 2. run a job at a bigger interval to do same clean up thing, and to maintain consistency,
 *      we can check expiry in get operation
 *      and delete the key if expired
 */
public class InMemoryCache<K, V> {
    private final Map<K, TtlValue<V>> cache;
    private final Set<K> ttlEnabledKeys;

    private final static int DEFAULT_JOB_DELAY_IN_SEC = 60;

    @Getter
    @Setter
    private static class TtlValue<V> {
        private long expiryInSec;
        private V value;

        public TtlValue( V value, long expiryInSec) {
            this.expiryInSec = expiryInSec;
            this.value = value;
        }

        public TtlValue(V value) {
            this.value = value;
        }
    }

    public InMemoryCache(int jobDelayInSec) {
        cache = new ConcurrentHashMap<>();
        ttlEnabledKeys = ConcurrentHashMap.newKeySet();

        // Run a job to expire key with ttl
        ScheduledThreadPoolExecutor cleanUpScheduler = new ScheduledThreadPoolExecutor(1);
        cleanUpScheduler.schedule(this::cleanUp, jobDelayInSec, TimeUnit.SECONDS);
    }

    public InMemoryCache() {
        this(DEFAULT_JOB_DELAY_IN_SEC);
    }

    public void set(K key, V value) {
        cache.put(key, new TtlValue<>(value));
    }

    public void set(K key, V value, int ttl) {
        if (ttl <= 0) {
            set(key, value);
            return;
        }

        cache.put(key, new TtlValue<>(value, Instant.now().plusSeconds(ttl).getEpochSecond()));
        ttlEnabledKeys.add(key);
    }

    public V get(K key) {
        TtlValue<V> ttlValue = cache.get(key);

        if (ttlValue == null) {
            return null;
        }

        if (ttlValue.getExpiryInSec() != 0 && ttlValue.getExpiryInSec() < Instant.now().getEpochSecond()) {
            remove(key);
            return null;
        }

        return ttlValue.getValue();
    }

    public void remove(K key) {
        cache.remove(key);
        ttlEnabledKeys.remove(key);
    }

    private void cleanUp() {
        for (K key : ttlEnabledKeys) {
            TtlValue<V> value = cache.get(key);

            if (value == null || value.getExpiryInSec() == 0 || value.getExpiryInSec() >= Instant.now().getEpochSecond()) {
                continue;
            }

            remove(key);
        }
    }
}
