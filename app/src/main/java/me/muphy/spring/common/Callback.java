package me.muphy.spring.common;

public interface Callback<K, T> {
    K call(T t);
}
