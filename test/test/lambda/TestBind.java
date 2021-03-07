package test.lambda;

import static org.junit.jupiter.api.Assertions.*;

import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import lambda.Bind;

class TestBind {

    static final Logger logger = Common.getLogger(TestBind.class);

    @Test
    void testBind() {
        logger.info("*** " + Common.methodName());
        assertEquals("{}", Bind.toString(null));
        Bind<String, String> bind = new Bind<>(null, "a", "A");
        assertEquals("{a=A}", Bind.toString(bind));
        assertEquals("A", Bind.get(bind, "a"));
        assertNull(Bind.get(bind, "x"));
        Bind<String, String> bind2 = new Bind<>(bind, "b", "B");
        assertEquals("{b=B, a=A}", Bind.toString(bind2));
        assertEquals("{b=B, a=A}", bind2.toString());
    }

}
