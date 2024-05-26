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

    private final static int DEFAULT_JOB_DELAY_IN_SEC = 5; // increase it, if want to run for large amount of time

    @Getter
    @Setter
    private static class TtlValue<V> {
        private long expiryInMillis;
        private V value;

        public TtlValue( V value, long expiryInMillis) {
            this.expiryInMillis = expiryInMillis;
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
        cleanUpScheduler.scheduleAtFixedRate(this::cleanUp, 0, jobDelayInSec, TimeUnit.SECONDS);
    }

    public InMemoryCache() {
        this(DEFAULT_JOB_DELAY_IN_SEC);
    }

    public void set(K key, V value) {
        cache.put(key, new TtlValue<>(value));
    }

    public void setEX(K key, V value, long ttl) {
        if (ttl <= 0) {
            set(key, value);
            return;
        }

        cache.put(key, new TtlValue<>(value, Instant.now().plusSeconds(ttl).toEpochMilli()));
        ttlEnabledKeys.add(key);
    }

    public void setPX(K key, V value, long ttl) {
        if (ttl <= 0) {
            set(key, value);
            return;
        }

        cache.put(key, new TtlValue<>(value, Instant.now().plusMillis(ttl).toEpochMilli()));
        ttlEnabledKeys.add(key);
    }

    public V get(K key) {
        TtlValue<V> ttlValue = cache.get(key);

        if (ttlValue == null) {
            return null;
        }

        if (ttlValue.getExpiryInMillis() != 0 && ttlValue.getExpiryInMillis() < Instant.now().toEpochMilli()) {
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

            if (value == null || value.getExpiryInMillis() == 0 || value.getExpiryInMillis() >= Instant.now().toEpochMilli()) {
                continue;
            }

            remove(key);
        }
    }
}
