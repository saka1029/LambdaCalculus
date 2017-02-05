package lambda;

/**
 * 関数適用可能な式であることを表すインタフェースです。
 * 
 * @author saka1029
 *
 */
public interface Applicable {

    /**
     * 引数{@code argument}を変数束縛環境{@code context}の元で適用します。
     * 引数{@code argument}は簡約されることなく渡されます。 簡約が必要な場合は{@code apply()}の実装で行う必要があります。
     * 
     * @param argument
     *            引数の式を指定します。
     * @param context
     *            変数束縛の環境を指定します。
     * @return 適用結果を返します。
     */
    Term apply(Term argument, Context context);

}
