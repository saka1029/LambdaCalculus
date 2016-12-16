package test.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import lambda.Context;
import lambda.Expression;

public class TestEval {
    
    @Test
    public void testFreeVariable() {
        assertEquals(Expression.variable("a"), Expression.of("a").eval());
    }
    
    @Test
    public void testChurchNumerals() {
        Context c = Context.root()
            .put("succ", "n.f.x.f(n f x)")
            .put("plus", "m.n.f.x.m f(n f x)")
            .put("n0", "a.b.b")
            .put("n1", "a.b.a b")
            .put("n2", "a.b.a(a b)")
            .put("n3", "a.b.a(a(a b))")
            ;
        assertEquals(Expression.of("n1").eval(c), Expression.of("succ n0").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ n1").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("succ n2").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ (succ n0)").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("plus n1 n2").eval(c));
    }
    
    @Test
    public void testChurchNumerals2() {
        Context c = Context.root()
            .define("succ", "n.f.x.f(n f x)")
            .define("plus", "m.n.f.x.m f(n f x)")
            .define("n0", "a.b.b")
            .define("n1", "a.b.a b")
            .define("n2", "a.b.a(a b)")
            .define("n3", "a.b.a(a(a b))")
            ;
        assertEquals(Expression.of("n1").eval(c), Expression.of("succ n0").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ n1").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("succ n2").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ (succ n0)").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("plus n1 n2").eval(c));
    }
    
    @Test
    public void testLogic() {
        Context c = Context.root()
            .put("true", "x.y.x")
            .put("false", "x.y.y")
            .put("and", "p.q.p q false")
            .put("or", "p.q.p true q")
            .put("not", "p.p false true")
            ;
        assertEquals(Expression.of("true").eval(c), Expression.of("and true true").eval(c));
        assertEquals(Expression.of("false").eval(c), Expression.of("and true false").eval(c));
        assertEquals(Expression.of("false").eval(c), Expression.of("and false true").eval(c));
        assertEquals(Expression.of("false").eval(c), Expression.of("and false false").eval(c));
        assertEquals(Expression.of("true").eval(c), Expression.of("or true true").eval(c));
        assertEquals(Expression.of("true").eval(c), Expression.of("or true false").eval(c));
        assertEquals(Expression.of("true").eval(c), Expression.of("or false true").eval(c));
        assertEquals(Expression.of("false").eval(c), Expression.of("or false false").eval(c));
        assertEquals(Expression.of("false").eval(c), Expression.of("not true").eval(c));
        assertEquals(Expression.of("true").eval(c), Expression.of("not false").eval(c));
    }
    
}
