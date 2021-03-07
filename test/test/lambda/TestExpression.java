package test.lambda;

import static lambda.LambdaCalculus.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

class TestExpression {

    static final Logger logger = Common.getLogger(TestExpression.class);

    @Test
    void testToString() {
        logger.info("*** " + Common.methodName());
        assertEquals("λx.x", parse("λ x . x").toString());
        assertEquals("λx.x x", parse("λ x . x x").toString());
        assertEquals("λx.λx.x x", parse("λx x.x x").toString());
        assertEquals("(λx.x) x", parse("(λx.x) x").toString());
        assertEquals("x x", parse("x x").toString());
        assertEquals("x (λx.x)", parse("x λx.x").toString());
    }

    static void testToNormalizedString(String expected, String actual) {
        assertEquals(parse(expected).toNormalizedString(), parse(actual).toNormalizedString());
    }

    @Test
    void testToNormalizedString() {
        logger.info("*** " + Common.methodName());
        assertEquals("λ%0.(λ%1.%1) %0", parse("λx.(λx.x) x").toNormalizedString());
        assertEquals("(λ%0.%0) a", parse("(λx.x) a").toNormalizedString());
        testToNormalizedString("λ a . a a", "λx.x x");
        testToNormalizedString("(λa.a)a", "(λx.x)a");
        testToNormalizedString("(λa.λb.λc.λd.d d d d) x", "(λx.λx.λx.λx.x x x x) x");
        testToNormalizedString("(λa.(λb.(λc.(λd.d) c) b) a) x", "(λx.(λx.(λx.(λx.x) x) x) x) x");
        testToNormalizedString("a (λx.x)", "a λx.x");
    }

    @Test
    void testReduce() {
        assertEquals("a", parse("(λx.x) a").reduce().toNormalizedString());
    }
}
