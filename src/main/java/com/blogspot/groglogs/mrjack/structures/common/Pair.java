package com.blogspot.groglogs.mrjack.structures.common;

import java.util.Objects;

/**
 * Represents a Pair of objects.
 * null values are not allowed
 * @param <T> type of first object
 * @param <E> type of second object
 */
public class Pair<T,E> {
    private T first;
    private E second;

    /**
     * Returns the Pair(first, second).
     * Items cannot be null.
     * @param first the first item in the pair
     * @param second the second item in the pair
     */
    public Pair(T first, E second){
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    public T getFirst(){
        return this.first;
    }

    public E getSecond(){
        return this.second;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof Pair)) return false;
        Pair other = (Pair)o;
        return this.first.equals(other.first) && this.second.equals(other.second);
    }

    @Override
    public String toString(){
        return "(" + this.first.toString() + "," + this.second.toString() + ")";
    }
}
