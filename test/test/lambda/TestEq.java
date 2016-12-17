package test.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import lambda.Term;

;public class TestEq {

    @Test
    public void testVariable() {
        assertTrue(Term.variable("a").eq(Term.of("a")));
        assertFalse(Term.variable("a").eq(Term.of("b")));
        assertFalse(Term.variable("a").eq(Term.of("a.b")));
        assertFalse(Term.variable("a").eq(Term.of("a b")));
    }

    @Test
    public void testLambda() {
        assertTrue(Term.variable("a").lambda(Term.variable("a")).eq(Term.of("a.a")));
        assertFalse(Term.variable("a").lambda(Term.variable("a")).eq(Term.of("a")));
        assertFalse(Term.variable("a").lambda(Term.variable("a")).eq(Term.of("a.b")));
        assertFalse(Term.variable("a").lambda(Term.variable("a")).eq(Term.of("b.a")));
    }
    
    @Test
    public void testApplication() {
        assertTrue(Term.variable("a").apply(Term.variable("a")).eq(Term.of("a a")));
        assertFalse(Term.variable("a").apply(Term.variable("a")).eq(Term.of("a")));
        assertFalse(Term.variable("a").apply(Term.variable("a")).eq(Term.of("a b")));
        assertFalse(Term.variable("a").apply(Term.variable("a")).eq(Term.of("b a")));
    }
    
    @Test
    public void testLambdaApplication() {
        assertTrue(Term.of("x.y.z.z y x").eq(Term.of("x.(y.(z.((z y) x)))")));
    }

}
