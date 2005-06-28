package com.idega.data.query.output;

/**
 * Any object that can output itself as part of the final query should implement this interface.
 *
 * @author <a href="mailto:joe@truemesh.com">Joe Walnes</a>
 */
public interface Outputable extends Cloneable {
    void write(Output out);
}
