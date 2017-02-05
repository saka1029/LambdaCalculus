package lambda;

/**
 * 組み込み関数を実装するためのインタフェースです。
 * 
 * @author saka1029
 *
 */
public interface Primitive extends Term, Applicable {

    /**
     * 引数を適用します。 このインタフェースを実装するクラスはこのメソッドだけを実装する必要があります。
     */
    Term apply(Term argument, Context context);

    /**
     * 簡約します。 このデフォルト実装では単に自分自身(this)を返します。
     */
    @Override
    default Term reduce(Context context) {
        return this;
    }

    /**
     * 正規化します。 このデフォルト実装では自分自身(this)を返します。
     */
    @Override
    default Term normalize(NormalizeContext context) {
        return this;
    }

    /**
     * ラムダ式lambdaで定義される束縛変数を含むかどうかを調べます。 このデフォルト実装では常にfalseを返します。
     */
    @Override
    default boolean containsBoundVariable(Lambda lambda) {
        return false;
    }
}
