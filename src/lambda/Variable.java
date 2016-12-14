package lambda;

import java.util.HashMap;
import java.util.Map;

public class Variable extends Expression {
    
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
    
    public Lambda lambda(Expression body) {
        return new Lambda(this, body);
    }
    
    @Override
    public boolean eq(Object obj) {
        return this == obj;
    }

    @Override
    Expression evalCore(Context context) {
        Expression e = context.get(this);
        if (e != null) return e;
        return this;
    }
    
    @Override
    Expression normalize(NormalizeContext context) {
        return context.get(this);
    }

    @Override
    public String toString() {
        return name;
    }

}
