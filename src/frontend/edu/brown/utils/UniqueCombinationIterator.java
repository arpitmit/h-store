package edu.brown.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.map.ListOrderedMap;
import org.apache.commons.collections15.set.ListOrderedSet;
import org.apache.log4j.Logger;

/**
 * 
 * @author pavlo
 * @param <E>
 */
public class UniqueCombinationIterator<E> implements Iterator<Set<E>> {
    private static final Logger LOG = Logger.getLogger(UniqueCombinationIterator.class);
    
    private final List<E> data;
    private final int combo_size;
    private final int num_elements;
    private final Vector<Integer> last;
    private ListOrderedSet<E> next = null;
    private Boolean finished = null;
    private int attempt_ctr = 0; 
    
    @SuppressWarnings("unchecked")
    public UniqueCombinationIterator(Collection<E> data, int size) {
        this.data = (data instanceof List ? (List<E>)data : new ArrayList<E>(data));
        this.combo_size = size;
        this.num_elements = this.data.size();
        
        // Initialize last counters
        this.last = new Vector<Integer>();
        this.last.setSize(this.combo_size);
        this.initializeLast(0);
    }
    
    private void initializeLast(int start) {
        for (int i = 0; i < this.combo_size; i++) {
            this.last.set(i, start + i);
        }
    }
    
    @Override
    public boolean hasNext() {
        if (this.finished == null) {
            try {
                this.getNext();
            } catch (RuntimeException ex) {
                LOG.info(this);
                throw ex;
            }
        }
        assert(this.finished != null);
        return (this.finished == false);
    }
    
    @Override
    public Set<E> next() {
        boolean valid = this.hasNext();
        Set<E> ret = (this.finished == false ? this.next : null);
        this.next = null;
        
        // If we're suppose to have more, then queue it up!
        if (valid) {
            this.getNext();
        }
        return (ret);
    }
    
    /**
     * Find the next unique combination
     */
    private void getNext() {
        assert(this.next == null);
        final boolean trace = LOG.isTraceEnabled();
        final boolean debug = LOG.isDebugEnabled();
        
        if (debug) LOG.debug("Finding next combination [call=" + (this.attempt_ctr++) + "]");
        
        boolean valid = false;
        Vector<Integer> buffer = null;
        for (int i = this.last.get(0); i < this.num_elements; i++) {
            if (trace) LOG.trace("\n" + this);
            
            buffer = new Vector<Integer>();
            buffer.setSize(this.combo_size);
            buffer.set(0, i);
            
            // We have a new combination!
            if (this.calculateCombinations(buffer, 1)) {
                if (trace) LOG.trace("Found new combination: " + buffer);
                valid = true;
                break;
            }
            if (trace) LOG.trace("No combination found that starts with index #" + i);
            buffer = null;
            this.initializeLast(i+1);
        } // FOR
        
        if (trace) LOG.trace("VALID = " + valid);
        if (valid) {
            assert(this.combo_size == buffer.size());
            this.next = new ListOrderedSet<E>();
            for (int i = 0; i < this.combo_size; i++) {
                this.next.add(this.data.get(buffer.get(i)));
            } // FOR
            if (trace) LOG.trace("NEXT = " + this.next);
            
            // Increase the last position's counter so that it is different next time
            this.last.set(this.combo_size-1, this.last.lastElement()+1);
            if (trace) LOG.trace("NEW LAST = " + this.last);
            
            this.finished = false;
        } else {
            this.finished = true;
        }
    }
    
    private boolean calculateCombinations(List<Integer> buffer, int position) {
        // If we're at the last position, then just return true
        if (position == this.combo_size) return (true);
        
        // Get the last element counter for this position
        int last_value = this.last.get(position);
        
        // Now if we go past the total number of elements, then we need to return false
        // Make sure that we reset ourselves to be one more than our preceding position
        for (int i = last_value; i < this.num_elements; i++) {
            this.last.set(position, i);
            buffer.set(position, i);
            if (this.calculateCombinations(buffer, position+1)) {
                return (true);
            }
        } // FOR
        // Note that we have to increase ourselves by two, because we know that the preceding position
        // is going to get increased by one, so we want to make sure we're one more than that
        // But everyone else should just be one more than the preceeding
        int base_value = this.last.get(position-1)+1;
        for (int i = position; i < this.combo_size; i++) {
            this.last.set(position, base_value + (i == position ? 1 : 0));
        }
        return (false);
    }
    
    @Override
    public void remove() {
        assert(false);
    }
    
    @Override
    public String toString() {
        Map<String, Object> m = new ListOrderedMap<String, Object>();
        m.put("DATA", this.data);
        m.put("COMBO_SIZE", this.combo_size);
        m.put("NUM_ELEMENTS", this.num_elements);
        m.put("FINISHED", this.finished);
        m.put("LAST", this.last);
        m.put("NEXT", this.next);
        
        return StringUtil.box(StringUtil.format("=", m));
    }
}
