package test.lambda;

import static org.junit.Assert.*;

import org.junit.Test;
import static lambda.Expression.*;

public class TestExpression {

    @Test
    public void testEval() {
        assertEquals(of("a.a"), of("b.b").eval());
    }

    @Test
    public void testNotEquals() {
        assertNotEquals(variable("a"), "a");
    }

    @Test(expected = RuntimeException.class)
    public void testApplyNoArguments() {
        variable("a").apply();
    }

}
