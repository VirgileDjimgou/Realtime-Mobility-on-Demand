package com.bee.dtbs.mobile.android.dtbsandroidclient.dtbsandroidclient.cache;

import java.util.Collection;

/**
 * Abstract class to represent a collection cache.
 *
 * @author djimgou patrick virgile
 */
public interface Cache<K,V> {

    /**
     * Add an item to the cache.
     *
     * @param item the item to add.
     */
    public void addItem(V item);

    /**
     * Remove an item from the cache.
     *
     * @param id item to remove.
     */
    public void removeItem(K id);

    /**
     * Find an item by id.
     *
     * @param id/index of item.
     */
    public V findItem(K id);

    /**
     * Return a collection of all objects in the cache.
     *
     * @return a collection of all objects in the cache.
     */
    public Collection<V> findAll();

    /**
     * Return true if has object else false.
     *
     * @param id id of object.
     * @return true id has object else false.
     */
    public boolean hasItem(K id);
}
