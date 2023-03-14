package contractgen.util;

/**
 * A pair of two values.
 *
 * @param left  The left part of the pair.
 * @param right The right part of the pair.
 * @param <X>   The type of the left object.
 * @param <Y>   The type of the right object.
 */
public record Pair<X, Y>(X left, Y right) {

}
