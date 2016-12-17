package lambda;

public interface Native extends Applicable {

    default Term evalCore(Context context) { return this; }
    default Term normalize(Context context) { return this; }
    default public boolean eq(Object obj) { return this == obj; }
    
    static Native of(String name, Native n) {
        return new Native() {
            String s = "#" + name + "#";
            @Override
            public Term apply(Term argument, Context context) {
                return n.apply(argument, context);
            }
            @Override
            public String toString() {
                return s;
            }
        };
    }

}
