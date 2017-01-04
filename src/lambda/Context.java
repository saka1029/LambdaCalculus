package lambda;

import java.util.HashMap;
import java.util.IdentityHashMap;

public class Context {
 
    final BindMap<Lambda, Term> bound = new BindMap<>(new IdentityHashMap<>());
    final BindMap<Variable, Term> unbound = new BindMap<>(new HashMap<>());
    int indent = 0;
 
    public Term define(UnboundVariable key, Term value) {
        return unbound.define(key, value);
    }
 
    private void indent() {
        for (int i = 0; i < indent; ++i)
            System.out.print("  ");
    }

    void enterExit(Term t, Term u) {
        indent();
        System.out.println(" = " + t + " -> " + u);
    }

    void enter(String type, Term t) {
        indent();
        System.out.println(type + "> " + t + " " + bound);
        ++indent;
    }
    
    void exit(Term t) {
        --indent;
        indent();
        System.out.println(" < " + t);
    }
 
    @Override
    public String toString() {
        return String.format("{bound=%s, unbound=%s}", bound, unbound);
    }
}
