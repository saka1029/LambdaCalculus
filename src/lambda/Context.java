package lambda;

import java.util.HashMap;
import java.util.Map;

public class Context {
    
    final Map<Variable, Term> map = new HashMap<>();
    int level = 0;
    int sequence = 0;
    boolean trace = false;
    
    public Term get(Variable variable) {
        return map.get(variable);
    }
    
    public Term get(Variable variable, Term defaultValue) {
        Term r = map.get(variable);
        return r != null ? r : defaultValue;
    }

    public Term define(Variable v, Term e) {
        map.put(v, e);
        return e;
    }
    
    public Term define(String variable, String expression) {
        return define(Variable.of(variable), Term.of(expression));
    }
    
    public Term define(String name, Native n) {
        return define(Variable.of(name), Native.of(name, n));
    }
    
    Variable normalizedVariable() {
        return Variable.of(sequence);
    }

    Restorable put(Variable variable, Term expression) {
        Term old = map.put(variable, expression);
        if (old != null)
            return () -> map.put(variable, old);
        else
            return () -> map.remove(variable);
    }
    

    Restorable put(Variable variable) {
        Term old = map.put(variable, Variable.of(sequence++));
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

    public void enter(Term e, Context c) {
        if (!trace) return;
        System.out.println(indent() + "> " + e + " : " + c);
        ++level;
    }

    public void leave(Term e) {
        if (!trace) return;
        --level;
        System.out.println(indent() + "< " + e);
    }
}
