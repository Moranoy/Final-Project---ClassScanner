package Logic.Interfaces;

import java.io.Serializable;

// This interface was written just because in this android API we can't use the Function interface.
@FunctionalInterface
public interface MyFunction<T, R> {
    R apply(T t);
}
