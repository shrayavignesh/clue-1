/**
 * A Pair is a set of two types, mostly able to be used as a container or a set of coordinates.
 *
 * @param <K>
 * @param <V>
 */
public class Pair<K, V> {
    private final K one;
    private final V two;

    public Pair(K one, V two) {
        this.one = one;
        this.two = two;
    }

    public K getOne() {
        return this.one;
    }

    public V getTwo() {
        return this.two;
    }
}
