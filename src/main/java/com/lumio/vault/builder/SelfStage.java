package com.lumio.vault.builder;

import com.lumio.vault.Vault;
import com.lumio.vault.legacy.Key;

/**
 * @author Pierre Lecerf (plecerf@lumiomedical.com)
 * Created on 2020/05/24
 */
public class SelfStage implements BuildStage
{
    @Override
    public void build(Vault vault)
    {
        vault.register(Key.of(Vault.class), () -> vault);
    }
}
