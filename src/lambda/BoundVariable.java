package lambda;

/**
 * 束縛変数のクラスです。 イミュータブルなクラスです。
 * 
 * @author saka1029
 */
public class BoundVariable implements Variable {

    /**
     * 束縛変数を定義しているラムダ式です。
     */
    final Lambda lambda;

    /**
     * コンストラクタです。
     * 
     * @param lambda
     *            この束縛変数を定義しているラムダ式を指定します。
     */
    BoundVariable(Lambda lambda) {
        if (lambda == null)
            throw new IllegalArgumentException("lambda is null");
        this.lambda = lambda;
    }

    /**
     * 簡約します。 contextの束縛変数環境に自分自身があれば 関連付けられた値を返します。 なければ自分自身を返します。
     */
    @Override
    public Term reduce(Context context) {
        return context.bound.getOrDefault(lambda, this);
    }

    /**
     * 正規化します。 contextの束縛変数環境にlambdaがあれば (自分自身を定義するラムダ式があれば) 関連付けられた値を返します。
     * なければ自分自身を返します。
     */
    @Override
    public Term normalize(NormalizeContext context) {
        return context.getOrDefault(lambda, this);
    }

    /**
     * 引数lambdaで定義された束縛変数であるかどうかを返します。
     */
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return lambda == this.lambda;
    }

    /**
     * 引数の名前が同じであるかどうかを返します。 つまり自分自身を定義するラムダ式のnameが同じであるかどうかを返します。
     * 自分自身を定義するラムダ式がことなっていてもnameが同じであればtrueを返します。
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BoundVariable))
            return false;
        BoundVariable o = (BoundVariable) obj;
        return o.lambda.name.equals(lambda.name);
    }

    /**
     * 文字列表現を返します。 自分自身を定義するラムダ式のnameを返します。
     */
    @Override
    public String toString() {
        return lambda.name;
    }
}
