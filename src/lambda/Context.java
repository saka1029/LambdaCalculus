package lambda;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

    void enter(String type, Term t) {
        if (!LambdaCalculus.TRACE) return;
        indent();
        System.out.println(type + "> " + t + " " + boundNames());
        ++indent;
    }
    
    void exit(String type, Term t) {
        if (!LambdaCalculus.TRACE) return;
        --indent;
        indent();
        System.out.println(type + "< " + t);
    }
 
    public Map<String, Term> boundNames() {
        Map<String, Term> names = new HashMap<>();
        for (Entry<Lambda, Term> e : bound.entrySet())
            names.put(e.getKey().name, e.getValue());
        return names;
    }

    @Override
    public String toString() {
        return String.format("{bound=%s, unbound=%s}", bound, unbound);
    }
}
