package lambda;

/**
 * 処理前の状態に復帰するための関数型インタフェースです。 以下のような使い方を想定しています。
 * 
 * <pre>
 * <code>
 * try (Restorable r = changeState()) {
 *     doSomething();
 * }
 * </code>
 * </pre>
 * 
 * try-with-resouceブロックを抜けるとき、
 * {@code r.close()}が呼び出され{@code changeState()}実行前の状態に 復帰します。
 * 
 * @author saka1029
 *
 */
@FunctionalInterface
public interface Restorable extends AutoCloseable {

    /**
     * 処理前の状態に復帰します。
     */
    void close();

}
