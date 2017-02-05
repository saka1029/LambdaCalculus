package lambda;

/**
 * 適用のクラスです。 イミュータブルなクラスです。
 * 
 * @author saka1029
 */
public class Application implements Term {

    /**
     * 適用される式です。
     */
    final Term head;

    /**
     * 適用する式です。
     */
    final Term tail;

    /**
     * コンストラクタです。
     * 
     * @param head
     *            適用される式を指定します。
     * @param tail
     *            適用する式を指定します。
     */
    Application(Term head, Term tail) {
        if (head == null)
            throw new IllegalArgumentException("head is null");
        if (tail == null)
            throw new IllegalArgumentException("tail is null");
        this.head = head;
        this.tail = tail;
    }

    /**
     * 簡約します。 headを簡約した結果が適用可能(Applicable)であれば tailを適用(apply)した結果を返します。
     * このときtailは簡約しません。 簡約するかどうかはApplicable#applyに委ねます。 適用可能でない場合はheadおよびtailを
     * 簡約した結果から新たなApplicationを作成して返します。
     */
    @Override
    public Term reduce(Context context) {
        Term function = head.reduce(context);
        if (function instanceof Applicable) {
            context.enter("β", this);
            Term reduced = ((Applicable) function).apply(tail, context);
            context.exit("β", reduced);
            return reduced;
        } else
            return new Application(function, tail.reduce(context));
    }

    /**
     * 正規化します。 headとtailをそれぞれ正規化した結果から 新たなApplicationを作成して返します。
     */
    @Override
    public Term normalize(NormalizeContext context) {
        return new Application(head.normalize(context), tail.normalize(context));
    }

    /**
     * 式がラムダ式lambdaで定義される束縛変数を含むかどうかを調べます。 headおよびtailのいずれかが含むかどうかを返します。
     */
    @Override
    public boolean containsBoundVariable(Lambda lambda) {
        return head.containsBoundVariable(lambda)
            || tail.containsBoundVariable(lambda);
    }

    /**
     * headおよびtailがともに等しい時にtrueを返します。
     */
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Application))
            return false;
        Application o = (Application) obj;
        return o.head.equals(head) && o.tail.equals(tail);
    }

    /**
     * headとtailの文字列表現をスペースで区切って出力します。 headがLambdaクラスの場合はheadを括弧で囲んで出力します。
     * tailがApplicationクラスの場合はtailを括弧で囲んで出力します。
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean headIsLambda = head instanceof Lambda;
        boolean tailIsApplication = tail instanceof Application;
        if (headIsLambda)
            sb.append("(");
        sb.append(head);
        if (headIsLambda)
            sb.append(")");
        sb.append(" ");
        if (tailIsApplication)
            sb.append("(");
        sb.append(tail);
        if (tailIsApplication)
            sb.append(")");
        return sb.toString();
    }

}
