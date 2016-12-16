package lambda;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public abstract class Context {

    public static Context root() { return new MapContext(); }

    abstract MapContext rootContext();
    abstract Expression get(Variable v);
    public Context define(Variable v, Expression e) { return rootContext().define(v, e); }
    public Context define(String v, String e) { return define(Variable.of(v), Expression.of(e)); }
    public abstract Context put(Variable v, Expression e);
    public Context put(String v, String e) { return put(Variable.of(v), Expression.of(e)); }

    void enter(Expression e, Context c) { rootContext().enter(e, c); }
    void leave(Expression e) { rootContext().leave(e); }
    
    abstract void toString(StringBuilder sb);

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        toString(sb);
        int length = sb.length();
        if (length >= 2)
            sb.delete(length - 2, length);
        sb.append("}");
        return sb.toString();
    }

    static class MapContext extends Context {

        final Map<Variable, Expression> map = new HashMap<>();
        int level = 0;

        MapContext rootContext() { return this; }

        @Override
        public Expression get(Variable v) {
            return map.get(v);
        }

        @Override
        public MapContext define(Variable v, Expression e) {
            map.put(v, e); return this;
        }

        @Override
        public Context put(Variable v, Expression e) {
            return new LinkContext(this, this, v, e);
        }

        String indent() {
            return level == 0 ? "" : String.format("%" + level + "s", "");
        }

        @Override
        public void enter(Expression e, Context c) {
            System.out.println(indent() + "> " + e + " : " + c);
            ++level;
        }

        @Override
        public void leave(Expression e) {
            --level;
            System.out.println(indent() + "< " + e);
        }
        
        @Override
        void toString(StringBuilder sb) {
            for (Entry<Variable, Expression> e : map.entrySet())
                sb.append(e.getKey()).append("=").append(e.getValue()).append(", ");
        }
    }

    static class LinkContext extends Context {

        final MapContext root;
        final Context previous;
        final Variable variable;
        final Expression expression;

        private LinkContext(MapContext root, Context previous, Variable variable, Expression expression) {
            this.root = root;
            this.previous = previous;
            this.variable = variable;
            this.expression = expression;
        }
        
        MapContext rootContext() { return root; }

        @Override
        public Expression get(Variable v) {
            if (v.equals(variable))
                return expression;
            return previous.get(v);
        }


        @Override
        public Context put(Variable v, Expression e) {
            return new LinkContext(root, this, v, e);
        }
        
        @Override
        void toString(StringBuilder sb) {
            sb.append(variable).append("=").append(expression).append(", ");
            previous.toString(sb);
        }
    }
}
