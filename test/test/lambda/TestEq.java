package test.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import lambda.Expression;

;public class TestEq {

    @Test
    public void testVariable() {
        assertTrue(Expression.variable("a").eq(Expression.of("a")));
        assertFalse(Expression.variable("a").eq(Expression.of("b")));
        assertFalse(Expression.variable("a").eq(Expression.of("a.b")));
        assertFalse(Expression.variable("a").eq(Expression.of("a b")));
    }

    @Test
    public void testLambda() {
        assertTrue(Expression.variable("a").lambda(Expression.variable("a")).eq(Expression.of("a.a")));
        assertFalse(Expression.variable("a").lambda(Expression.variable("a")).eq(Expression.of("a")));
        assertFalse(Expression.variable("a").lambda(Expression.variable("a")).eq(Expression.of("a.b")));
        assertFalse(Expression.variable("a").lambda(Expression.variable("a")).eq(Expression.of("b.a")));
    }
    
    @Test
    public void testApplication() {
        assertTrue(Expression.variable("a").apply(Expression.variable("a")).eq(Expression.of("a a")));
        assertFalse(Expression.variable("a").apply(Expression.variable("a")).eq(Expression.of("a")));
        assertFalse(Expression.variable("a").apply(Expression.variable("a")).eq(Expression.of("a b")));
        assertFalse(Expression.variable("a").apply(Expression.variable("a")).eq(Expression.of("b a")));
    }
    
    @Test
    public void testLambdaApplication() {
        assertTrue(Expression.of("x.y.z.z y x").eq(Expression.of("x.(y.(z.((z y) x)))")));
    }

}
