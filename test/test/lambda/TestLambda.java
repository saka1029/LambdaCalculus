package test.lambda;

import static org.junit.Assert.*;
import static lambda.LambdaCalculus.*;

import org.junit.Test;

import lambda.Lambda;

public class TestLambda {

    @Test
    public void testEquals() {
        assertEquals(term("x.x"), term("x.x"));
        assertNotEquals(term("x.y"), term("x.x"));
        assertNotEquals(term("y.y"), term("x.x"));
        assertNotEquals(term("x.x"), term("x"));
    }

    static void testToStringDot(String expected, String term) {
        assertEquals(expected, ((Lambda)term(term)).toStringDot());
    }

    @Test
    public void testToStringDot() {
        testToStringDot("x.x", "x.x");
        testToStringDot("x.x", "λx.x");
        testToStringDot("x.λy.x", "x.y.x");
        testToStringDot("x.λy.x", "λx.λy.x");
        testToStringDot("x.λy.x", "λx y.x");
    }

    static void testToStringLambda(String expected, String term) {
        assertEquals(expected, ((Lambda)term(term)).toStringLambda());
    }

    @Test
    public void testToStringLambda() {
        testToStringLambda("λx.x", "x.x");
        testToStringLambda("λx.x", "λx.x");
        testToStringLambda("λx y.x", "x.y.x");
        testToStringLambda("λx y.x", "λx.λy.x");
        testToStringLambda("λx y.x", "λx y.x");
    }

}
