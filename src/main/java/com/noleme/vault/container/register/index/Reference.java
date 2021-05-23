package com.noleme.vault.container.register.index;

/**
 * @author Pierre LECERF (pierre@noleme.com)
 * Created on 23/05/2021
 */
public final class Reference
{
    private String identifier;

    Reference(String name)
    {
        this.identifier = name;
    }

    public String getIdentifier()
    {
        return this.identifier;
    }

    /**
     * Should only be used from within an index component (most likely Services) for special situations like prefixing.
     *
     * @param identifier
     * @return
     */
    Reference setIdentifier(String identifier)
    {
        this.identifier = identifier;
        return this;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Reference reference = (Reference) o;

        return this.identifier.equals(reference.identifier);
    }

    @Override
    public int hashCode()
    {
        return this.identifier.hashCode();
    }
}
