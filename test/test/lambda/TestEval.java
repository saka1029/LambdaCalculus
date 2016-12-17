package test.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import lambda.Context;
import lambda.Term;

public class TestEval {
    
    @Test
    public void testFreeVariable() {
        assertEquals(Term.variable("a"), Term.of("a").eval());
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
        assertEquals(Term.of("n1").eval(c), Term.of("succ n0").eval(c));
        assertEquals(Term.of("n2").eval(c), Term.of("succ n1").eval(c));
        assertEquals(Term.of("n3").eval(c), Term.of("succ n2").eval(c));
        assertEquals(Term.of("n2").eval(c), Term.of("succ (succ n0)").eval(c));
        assertEquals(Term.of("n3").eval(c), Term.of("plus n1 n2").eval(c));
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
        assertEquals(Term.of("n1").eval(c), Term.of("succ n0").eval(c));
        assertEquals(Term.of("n2").eval(c), Term.of("succ n1").eval(c));
        assertEquals(Term.of("n3").eval(c), Term.of("succ n2").eval(c));
        assertEquals(Term.of("n2").eval(c), Term.of("succ (succ n0)").eval(c));
        assertEquals(Term.of("n3").eval(c), Term.of("plus n1 n2").eval(c));
    }
    
    @Test
    public void testLogic() {
        Context c = new Context();
        c.define("true", "x.y.x");
        c.define("false", "x.y.y");
        c.define("and", "p.q.p q false");
        c.define("or", "p.q.p true q");
        c.define("not", "p.p false true");
        assertEquals(Term.of("true").eval(c), Term.of("and true true").eval(c));
        assertEquals(Term.of("false").eval(c), Term.of("and true false").eval(c));
        assertEquals(Term.of("false").eval(c), Term.of("and false true").eval(c));
        assertEquals(Term.of("false").eval(c), Term.of("and false false").eval(c));
        assertEquals(Term.of("true").eval(c), Term.of("or true true").eval(c));
        assertEquals(Term.of("true").eval(c), Term.of("or true false").eval(c));
        assertEquals(Term.of("true").eval(c), Term.of("or false true").eval(c));
        assertEquals(Term.of("false").eval(c), Term.of("or false false").eval(c));
        assertEquals(Term.of("false").eval(c), Term.of("not true").eval(c));
        assertEquals(Term.of("true").eval(c), Term.of("not false").eval(c));
    }
    
    @Test
    public void testDefine() {
        Context c = Term.defaultContext();
        Term.of("define succ n.f.x.f(n f x)").eval(c);
        Term.of("define plus m.n.f.x.m f(n f x)").eval(c);
        Term.of("define n0 a.b.b").eval(c);
        Term.of("define n1 a.b.a b").eval(c);
        Term.of("define n2 a.b.a(a b)").eval(c);
        Term.of("define n3 a.b.a(a(a b))").eval(c);
        assertEquals(Term.of("n1").eval(c), Term.of("succ n0").eval(c));
        assertEquals(Term.of("n2").eval(c), Term.of("succ n1").eval(c));
        assertEquals(Term.of("n3").eval(c), Term.of("succ n2").eval(c));
        assertEquals(Term.of("n2").eval(c), Term.of("succ (succ n0)").eval(c));
        assertEquals(Term.of("n3").eval(c), Term.of("plus n1 n2").eval(c));
    }
    
}
