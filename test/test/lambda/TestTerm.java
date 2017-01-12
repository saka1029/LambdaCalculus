package test.lambda;

import static org.junit.Assert.*;

import org.junit.Test;

import lambda.Term;

import static lambda.LambdaCalculus.*;

public class TestTerm {

    @Test
    public void testNormalize() {
        Term xx = term("x.x");
        Term yy = term("y.y");
        assertNotEquals(yy, xx);
        assertEquals(yy.normalize(), xx.normalize());
    }

}
