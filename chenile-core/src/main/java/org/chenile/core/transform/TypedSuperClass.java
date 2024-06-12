package org.chenile.core.transform;

/**
 * Any class that implements this interface will typically be subclassed and used in the {@link SubclassRegistry}.
 */
public interface TypedSuperClass {
    /**
     *
     * @return the string that represents the subclass in the transformation registry.
     */
    public String getType();
}
