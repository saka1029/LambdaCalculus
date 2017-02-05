package lambda;

import java.util.HashMap;
import java.util.Map;

/**
 * どのラムダ式からも束縛されていない変数です。 イミュータブルなクラスです。
 * 
 * @author saka1029
 */
public class UnboundVariable implements Variable {

    /**
     * 実行時にすべての{@link UnboundVariable}を保持するマップです。
     * 同一名の{@link UnboundVariable}を単一のインスタンスにマップするために使用します。
     */
    private static final Map<String, UnboundVariable> variables = new HashMap<>();

    /**
     * 変数の名前です。
     */
    final String name;

    /**
     * コンストラクタです。
     * 
     * @param name
     *            変数の名前を指定します。
     * @throws IllegalArgumentException
     *             {@code name}がnullの時にスローします。
     */
    private UnboundVariable(String name) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    /**
     * {@code name}で指定された{@link UnboundVariable}のインスタンスを作成します。
     * 
     * @param name
     *            変数の名前を指定します。
     * @return UnboundVariableのインスタンスを返します。
     */
    public static UnboundVariable of(String name) {
        return variables.computeIfAbsent(name, key -> new UnboundVariable(key));
    }

    /**
     * 簡約します。 {@code context#unbound}にこの変数があれば、それに対応する値を返します。 ない場合は自分自身を返します。
     */
    @Override
    public Term reduce(Context context) {
        // return context.unbound.getOrDefault(this, this);
        Term reduced = context.unbound.get(this);
        if (reduced != null) {
            if (reduced != this) {
                context.enter("@", this);
                context.exit("@", reduced);
            }
        } else
            reduced = this;
        return reduced;
    }

    /**
     * 正規化します。 この実装では常に自分自身を返します。
     */
    @Override
    public Term normalize(NormalizeContext context) {
        return this;
    }

    /**
     * ラムダ式{@code lambda}が定義する束縛変数を含むかどうかを調べます。
     * {@link UnboundVariable}は束縛変数ではないので、 この実装では常にfalseを返します。
     */
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return false;
    }

    /**
     * 文字列表現を返します。 この実装では{@code name}を返します。
     */
    @Override
    public String toString() {
        return name;
    }
}
