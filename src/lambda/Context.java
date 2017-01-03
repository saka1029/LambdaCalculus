package lambda;

import java.util.HashMap;
import java.util.IdentityHashMap;

public class Context {
    
    final BindMap<Lambda, Term> bound = new BindMap<>(new IdentityHashMap<>());
    final BindMap<Variable, Term> unbound = new BindMap<>(new HashMap<>());
    
    public Term define(UnboundVariable key, Term value) {
        return unbound.define(key, value);
    }
    
    @Override
    public String toString() {
        return String.format("{bound=%s, unbound=%s}", bound, unbound);
    }
}
