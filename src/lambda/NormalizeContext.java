package lambda;

class NormalizeContext {

    static final NormalizeContext EMPTY = new NormalizeContext(null, 0, null, null);
    static NormalizeContext root() { return EMPTY; }

    final NormalizeContext previous;
    final int size;
    final Variable origin;
    final Variable normalized;

    private NormalizeContext(NormalizeContext previous, int size, Variable origin, Variable normalized) {
        this.previous = previous;
        this.origin = origin;
        this.normalized = normalized;
        this.size = size;
    }

    NormalizeContext put(Variable original) {
        return new NormalizeContext(this, size + 1, original, Variable.of(size));
    }

    Variable get(Variable v) {
        if (size == 0) return v;
        if (origin == v) return normalized;
        return previous.get(v);
    }
}
