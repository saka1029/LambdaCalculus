package lambda;

/**
 * ラムダ計算の式です。
 * 
 * @author saka1029
 */
public interface Term {

    /**
     * 簡約します。
     * 
     * @param context 簡約用の変数束縛環境を指定します。
     * @return 簡約した結果を返します。
     */
    Term reduce(Context context);

    /**
     * 正規化します。
     * 例えば式{@code λx y.x}は正規化によって{@code λ$0 $1.$0}となります。
     * 正規化することによって式{@code λx y.x}と{@code λa b.a}は等しく（equals）なります。
     * 変換後の束縛変数は'$'で始まるので再度式に
     * 変換することができない点に注意します。
     * 
     * @param context 正規化用の変数束縛環境を指定します。
     * @return 正規化の結果を返します。
     */
    Term normalize(NormalizeContext context);

    /**
     * 式がラムダ式lambdaで定義される束縛変数を含むかどうかを調べます。
     * 
     * @param lambda 調べる束縛変数を定義しているラムダ式を指定します。
     * @return 指定された束縛変数を含む場合true、それ以外の場合はfalseを返します。
     */
    boolean containsBoundVariable(Lambda lambda);
    
    /**
     * 正規化します。
     * 
     * @return 正規化した結果を返します。
     */
    default Term normalize() {
        return normalize(new NormalizeContext());
    }
    
}
