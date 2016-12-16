package lambda;

public abstract class Native extends Applicable {

    @Override Expression evalCore(Context context) { return this; }
    @Override Expression normalize(NormalizeContext context) { return this; }
    @Override public boolean eq(Object obj) { return this == obj; }

}
