package com.lumiomedical.vault.service;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/22
 */
public interface ValueProvider <T>
{
    T provide();
}
