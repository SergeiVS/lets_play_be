package org.lets_play_be.service;

public interface StandardService <T,S>{

    public T execute(S s);
}
