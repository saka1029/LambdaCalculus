package lambda;

import java.util.HashMap;
import java.util.Map;

public class Context {
    
    final Map<Variable, Expression> map = new HashMap<>();
    int level = 0;
    int sequence = 0;
    boolean trace = false;
    
    public Expression get(Variable variable) {
        return map.get(variable);
    }
    
    public Expression getOrDefault(Variable variable, Expression or) {
        Expression r = map.get(variable);
        if (r != null)
            return r;
        else
            return or;
    }

    public Expression define(Variable v, Expression e) {
        map.put(v, e);
        return e;
    }
    
    public Expression define(String variable, String expression) {
        return define(Variable.of(variable), Expression.of(expression));
    }
    
    public Expression define(String name, Native n) {
        return define(Variable.of(name), Native.of(name, n));
    }
    
    Variable normalizedVariable() {
        return Variable.of(sequence);
    }

    Restorable put(Variable variable, Expression expression) {
        Expression old = map.put(variable, expression);
        if (old != null)
            return () -> map.put(variable, old);
        else
            return () -> map.remove(variable);
    }
    

    Restorable put(Variable variable) {
        Expression old = map.put(variable, Variable.of(sequence++));
        if (old != null)
            return () -> { map.put(variable, old); --sequence; };
        else
            return () -> { map.remove(variable); --sequence; };
    }

    @Override
    public String toString() {
        return map.toString();
    }


    String indent() {
        return level == 0 ? "" : String.format("%" + level + "s", "");
    }

    public void enter(Expression e, Context c) {
        if (!trace) return;
        System.out.println(indent() + "> " + e + " : " + c);
        ++level;
    }

    public void leave(Expression e) {
        if (!trace) return;
        --level;
        System.out.println(indent() + "< " + e);
    }
}
