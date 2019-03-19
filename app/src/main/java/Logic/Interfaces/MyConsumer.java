package Logic.Interfaces;

// This interface was written just because in this android API we can't use the Consumer interface.
@FunctionalInterface
public interface MyConsumer<T> {
    void accept(T uploadedObject);
}
