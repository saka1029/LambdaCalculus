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
        Context c = new Context();
        c.define("succ", "n.f.x.f(n f x)");
        c.define("plus", "m.n.f.x.m f(n f x)");
        c.define("n0", "a.b.b");
        c.define("n1", "a.b.a b");
        c.define("n2", "a.b.a(a b)");
        c.define("n3", "a.b.a(a(a b))");
        assertEquals(Expression.of("n1").eval(c), Expression.of("succ n0").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ n1").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("succ n2").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ (succ n0)").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("plus n1 n2").eval(c));
    }
    
    @Test
    public void testChurchNumerals2() {
        Context c = new Context();
        c.define("succ", "n.f.x.f(n f x)");
        c.define("plus", "m.n.f.x.m f(n f x)");
        c.define("n0", "a.b.b");
        c.define("n1", "a.b.a b");
        c.define("n2", "a.b.a(a b)");
        c.define("n3", "a.b.a(a(a b))");
        assertEquals(Expression.of("n1").eval(c), Expression.of("succ n0").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ n1").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("succ n2").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ (succ n0)").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("plus n1 n2").eval(c));
    }
    
    @Test
    public void testLogic() {
        Context c = new Context();
        c.define("true", "x.y.x");
        c.define("false", "x.y.y");
        c.define("and", "p.q.p q false");
        c.define("or", "p.q.p true q");
        c.define("not", "p.p false true");
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
    
    @Test
    public void testDefine() {
        Context c = Expression.defaultContext();
        Expression.of("define succ n.f.x.f(n f x)").eval(c);
        Expression.of("define plus m.n.f.x.m f(n f x)").eval(c);
        Expression.of("define n0 a.b.b").eval(c);
        Expression.of("define n1 a.b.a b").eval(c);
        Expression.of("define n2 a.b.a(a b)").eval(c);
        Expression.of("define n3 a.b.a(a(a b))").eval(c);
        assertEquals(Expression.of("n1").eval(c), Expression.of("succ n0").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ n1").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("succ n2").eval(c));
        assertEquals(Expression.of("n2").eval(c), Expression.of("succ (succ n0)").eval(c));
        assertEquals(Expression.of("n3").eval(c), Expression.of("plus n1 n2").eval(c));
    }
    
}
