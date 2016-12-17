package lambda;

import java.util.HashMap;
import java.util.Map;

public class Variable implements Term {
    
    static final Map<String, Variable> map = new HashMap<>();
    
    final String name;
    
    private Variable(String name) {
        this.name = name;
    }
    
    public static Variable of(String name) {
        return map.computeIfAbsent(name, key -> new Variable(key));
    }

    public static Variable of(int number) {
        return of(Integer.toString(number));
    }
    
    public Lambda lambda(Term body) {
        return new Lambda(this, body);
    }
    
    @Override
    public boolean eq(Object obj) {
        return this == obj;
    }

    @Override
    public Term evalCore(Context context) {
        return context.get(this, this);
    }
    
    @Override
    public Term normalize(Context context) {
        return context.get(this, this);
    }

    @Override
    public String toString() {
        return name;
    }

}
