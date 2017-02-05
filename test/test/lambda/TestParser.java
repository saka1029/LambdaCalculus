package test.lambda;

import static org.junit.Assert.*;
import static lambda.LambdaCalculus.*;

import org.junit.Test;

import lambda.UnboundVariable;

public class TestParser {

    @Test
    public void testVariable() {
        assertEquals(UnboundVariable.of("a"), term("a"));
        assertEquals(UnboundVariable.of("0"), term("0"));
        assertEquals(UnboundVariable.of("+"), term("+"));
        assertEquals(UnboundVariable.of("-"), term("-"));
        assertEquals(UnboundVariable.of("*"), term("*"));
        assertEquals(UnboundVariable.of("/"), term("/"));
        assertEquals(UnboundVariable.of("%"), term("%"));
        assertEquals(UnboundVariable.of("^"), term("^"));
        assertEquals(UnboundVariable.of("="), term("="));
        assertEquals(UnboundVariable.of("|"), term("|"));
        assertEquals(UnboundVariable.of("&"), term("&"));
        assertEquals(UnboundVariable.of("!"), term("!"));
        assertEquals(UnboundVariable.of("あいう"), term("あいう"));
//        assertEquals(UnboundVariable.of("𩸽"), term("𩸽"));
    }
    
    @Test
    public void testInvalidFreevariable() {
        try {
            assertEquals(term("$1"), term("$1"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("Free variable must not start with '$'", e.getMessage());
        }
        try {
            assertEquals(term("$1.$1 $2"), term("$1.$1 $2"));
            fail();
        } catch (RuntimeException e) {
            assertEquals("Free variable must not start with '$'", e.getMessage());
        }
    }
    
    @Test
    public void testLambda() {
        assertEquals(term("x.x"), term("λx.x"));
        assertEquals(term("x.x"), term("\\x.x"));
        assertEquals(term("x.y.x"), term("λx y.x"));
        assertEquals(term("x.y.y"), term("λx y.y"));
        assertEquals(term("$0.$0"), term("λ$0.$0"));
    }
    
    @Test
    public void testVairiablesMissing() {
        try {
            term("λ.x");
            fail();
        } catch (RuntimeException e) {
            assertEquals("Variables missing", e.getMessage());
        }
    }
    
    @Test
    public void testVairiableExpected() {
        try {
            term("λ");
            fail();
        } catch (RuntimeException e) {
            assertEquals("Variable expected", e.getMessage());
        }
    }

}
