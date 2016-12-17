package lambda;

public interface Native extends Applicable {

    default Expression evalCore(Context context) { return this; }
    default Expression normalize(Context context) { return this; }
    default public boolean eq(Object obj) { return this == obj; }
    
    static Native of(String name, Native n) {
        return new Native() {
            String s = "#" + name + "#";
            @Override
            public Expression apply(Expression argument, Context context) {
                return n.apply(argument, context);
            }
            @Override
            public String toString() {
                return s;
            }
        };
    }

}
