package edu.brown.profilers;

import java.util.ArrayList;
import java.util.List;

import org.voltdb.utils.Pair;

public class AntiCacheManagerProfiler extends AbstractProfiler {
    
    /**
     * The number of transactions that attempted to access evicted data.
     */
    public int restarted_txns = 0;
    
    /**
     * Timestamps for when this partition was actually performing an eviction.
     */
    public List<Pair<Long, Long>> eviction_timestamps = new ArrayList<Pair<Long,Long>>();
    
    /**
     * The amount of time it takes for the AntiCacheManager to evict a block
     * of tuples from this partition
     */
    public ProfileMeasurement eviction_time = new ProfileMeasurement("EVICTION");
    
    /**
     * The amount of time it takes for the AntiCacheManager to retrieve
     * an evicted block from disk.
     */
    public ProfileMeasurement retrieval_time = new ProfileMeasurement("RETRIEVAL");
    
    /**
     * The amount of time it takes for the AntiCacheManager to merge an evicted 
     * block down in the EE.
     */
    public ProfileMeasurement merge_time = new ProfileMeasurement("MERGE");
    
    public void reset() {
        super.reset();
        this.restarted_txns = 0;
    }
}
