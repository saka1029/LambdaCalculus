package lambda;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 簡約のコンテキストです。
 * 
 * @author saka1029
 *
 */
public class Context {

    /**
     * BoundVariableのコンテキストです。
     */
    final BindMap<Lambda, Term> bound = new BindMap<>(new IdentityHashMap<>());

    /**
     * UnboundVariableのコンテキストです。
     */
    final BindMap<UnboundVariable, Term> unbound = new BindMap<>(new HashMap<>());

    /**
     * トレース出力のネストレベルです。
     */
    int indent = 0;

    public Term define(UnboundVariable key, Term value) {
        return unbound.define(key, value);
    }
    
    public void undefine(UnboundVariable key) {
        unbound.undefine(key);
    }

    /**
     * トレース出力時にインデントします。
     */
    private void indent() {
        for (int i = 0; i < indent; ++i)
            System.out.print("  ");
    }

    /**
     * 簡約前のトレースを出力します。 {@link LambdaCalculus#TRACE}がtureの場合のみ出力します。
     * 
     * @param type
     *            簡約の種類を指定します。（"β", "η"など）
     * @param t
     *            トレース出力する式を指定します。
     */
    void enter(String type, Term t) {
        if (!LambdaCalculus.TRACE)
            return;
        indent();
        System.out.println(type + "> " + t + " " + boundNames());
        ++indent;
    }

    /**
     * 簡約後のトレースを出力します。 {@link LambdaCalculus#TRACE}がtureの場合のみ出力します。
     * 
     * @param type
     *            簡約の種類を指定します。（"β", "η"など）
     * @param t
     *            トレース出力する式を指定します。
     */
    void exit(String type, Term t) {
        if (!LambdaCalculus.TRACE)
            return;
        --indent;
        indent();
        System.out.println(type + "< " + t);
    }

    /**
     * {@link BoundVariable}のコンテキストの文字列表現を返します。
     * 
     * @return {@link BoundVariable}とその値のペアの文字列表現を返します。
     */
    public Map<String, Term> boundNames() {
        Map<String, Term> names = new HashMap<>();
        for (Entry<Lambda, Term> e : bound.entrySet())
            names.put(e.getKey().name, e.getValue());
        return names;
    }

    /**
     * このコンテキストの文字列表現を返します。
     */
    @Override
    public String toString() {
        return String.format("{bound=%s, unbound=%s}", bound, unbound);
    }
}
