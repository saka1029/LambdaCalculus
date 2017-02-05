package lambda;

/**
 * カリー化された単一の束縛変数を持つラムダ式です。 基本的にイミュータブルなクラスです。
 * 
 * @author saka1029
 */
public class Lambda implements Term, Applicable {

    /**
     * 束縛変数の名前名前です。
     */
    final String name;

    /**
     * 関数の本体です。
     */
    private Term body;

    /**
     * bodyが自分自身の束縛変数を含んでいるかどうかを保持します。 falseの場合はapplyする時に引数の評価および束縛を省略できます。
     */
    private boolean containsBoundVariable;

    /**
     * コンストラクタ。
     * 
     * @param name
     *            束縛変数の名前を指定します。
     * @throws IllegalArgumentException
     *             引数nameがnullです。
     */
    Lambda(String name) {
        if (name == null)
            throw new IllegalArgumentException("name is null");
        this.name = name;
    }

    /**
     * bodyの値を設定します。 コンストラクタを呼び出した後、一回だけ呼び出すことができます。
     * 二回以上呼び出すとIllegalStateExceptionをスローします。
     * 
     * @param body
     *            bodyに設定する値を指定します。
     * @throws IllegalArgumentException
     *             引数bodyがnullです。
     * @throws IllegalStateException
     *             このメソッドが二回以上呼び出されています。
     */
    void setBody(Term body) {
        if (body == null)
            throw new IllegalArgumentException("body is null");
        if (this.body != null)
            throw new IllegalStateException("body cannot be assigned");
        this.body = body;
        this.containsBoundVariable = body.containsBoundVariable(this);
    }

    /**
     * η-変換可能な場合はη-変換後の式を返します。 変換可能でない場合はnullを返します。 η-変換はラムダ式が{@code λx.E x}の形式で
     * {@code E}が変数{@code x}を含まない場合 {@code E}に変換します。
     * 
     * @return η変換後の式を返します。変換できない場合はnullを返します。
     */
    private Term etaConversion() {
        if (!(body instanceof Application))
            return null;
        Application app = (Application) body;
        if (!(app.tail instanceof BoundVariable))
            return null;
        BoundVariable v = (BoundVariable) app.tail;
        if (v.lambda != this)
            return null;
        if (app.head.containsBoundVariable(this))
            return null;
        return app.head;
    }

    /**
     * 簡約します。 最初にbodyを簡約して新しいラムダ式を作成します。 次に、η-変換可能であればその結果を返します。
     */
    @Override
    public Term reduce(Context context) {
        // bodyを簡約して自分自身を新しいLambdaに置換する。
        Lambda lambda = new Lambda(name);
        try (Restorable r = context.bound.put(this, new BoundVariable(lambda))) {
            lambda.setBody(body.reduce(context));
        }
        // η-変換可能であれば変換する。
        Term e = lambda.etaConversion();
        if (e == null)
            return lambda;
        context.enter("η", lambda);
        Term reduced = e.reduce(context);
        context.exit("η", reduced);
        return reduced;
    }

    /**
     * 引数argumentを関数適用します。 bodyがこのラムダ式の束縛変数を含まない場合は 引数を簡約せず引数の束縛も行わずに、
     * 単にbodyを簡約した結果を返します。 それ以外の場合は引数を簡約して変数束縛後にbodyを簡約します。
     */
    @Override
    public Term apply(Term argument, Context context) {
        // bodyがこのラムダで定義されている束縛変数を含まないのであれば、
        // 引数の簡約を行わず、束縛自身も行いません。
        // この2行があることで以下の再帰呼び出しが実行できるようになります。
        // reduce("define fact (n.ifthenelse (iszero n) 1 (* n (fact (pred
        // n))))", c);
        if (!containsBoundVariable) {
            context.enter("β", this);
            Term result = body.reduce(context);
            context.exit("β", result);
            return result;
        }
        Term result = null;
        // 引数を簡約して束縛し、bodyを評価する。
        try (Restorable r = context.bound.put(this, argument.reduce(context))) {
            context.enter("β", this);
            return result = body.reduce(context);
        } finally {
            context.exit("β", result);
        }
    }

    /**
     * 束縛変数の名前を {@code "$0", "$1", "$2", ...} に置換したラムダ式を返します。 例えば
     * {@code λx.λy.x} の場合、 {@code λ$0.λ$1.$0} を返します。
     */
    @Override
    public Term normalize(NormalizeContext context) {
        String name = "$" + context.size();
        Lambda lambda = new Lambda(name);
        BoundVariable variable = new BoundVariable(lambda);
        try (Restorable r = context.put(this, variable)) {
            lambda.body = body.normalize(context);
            return lambda;
        }
    }

    /**
     * bodyがラムダ式lamdaで定義される束縛変数を含むかどうかを返します。
     */
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return body.containsBoundVariable(lambda);
    }

    /**
     * 束縛変数名nameが等しく、bodyが等しい時にtrueを返します。
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Lambda))
            return false;
        Lambda o = (Lambda) obj;
        return o.name.equals(name) && o.body.equals(body);
    }

    private void toStringBody(StringBuilder sb) {
        sb.append(name);
        if (body instanceof Lambda) {
            sb.append(" ");
            ((Lambda) body).toStringBody(sb);
        } else
            sb.append(".").append(body);
    }

    /**
     * ラムダ形式のラムダ式（例えば{@code "λx y.x"}）を返します。
     */
    public String toStringLambda() {
        StringBuilder sb = new StringBuilder();
        sb.append("λ");
        toStringBody(sb);
        return sb.toString();
    }

    /**
     * ドット形式のラムダ式（例えば{@code "x.y.x"}）を返します。
     */
    public String toStringDot() {
        return name + "." + body;
    }

    /**
     * 文字列表現を返します。 例えばラムダ式 {@code λx.λy.x} に対して
     * {@code LambdaCalculus.TO_STRING_DOT} がtrueの時は {@code "x.y.x"}を、 falseの時は
     * {@code "λx y.x"}を返します。
     */
    @Override
    public String toString() {
        return LambdaCalculus.TO_STRING_DOT ? toStringDot() : toStringLambda();
    }

}
