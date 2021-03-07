package test.lambda;

import static lambda.LambdaCalculus.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.junit.jupiter.api.Test;

import lambda.Expression;

class TestLambdaCalculus {

    static final Logger logger = Common.getLogger(TestLambdaCalculus.class);

    @Test
    void testParseError() {
        logger.info("*** " + Common.methodName());
        try {
            parse("λx y");
            fail();
        } catch (RuntimeException e) {
            assertEquals("variable expected", e.getMessage());
        }
        try {
            parse("λ.y");
            fail();
        } catch (RuntimeException e) {
            assertEquals("variable expected", e.getMessage());
        }
        try {
            parse("(y");
            fail();
        } catch (RuntimeException e) {
            assertEquals("')' expected", e.getMessage());
        }
        try {
            parse("y)");
            fail();
        } catch (RuntimeException e) {
            assertEquals("extra string ')'", e.getMessage());
        }
        try {
            parse("");
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected end of string", e.getMessage());
        }
        try {
            parse(")");
            fail();
        } catch (RuntimeException e) {
            assertEquals("unexpected char ')'", e.getMessage());
        }
    }

    Map<String, Expression> context = new HashMap<>();

    void define(String name, String expression) {
        context.put(name, parse(expression));
    }

    void assertEquivalent(String expected, String actual) {
        assertEquals(parse(expected).reduce(context).toNormalizedString(),
            parse(actual).reduce(context).toNormalizedString());
    }

    /**
     * @see <a href=
     *      "https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E8%87%AA%E7%84%B6%E6%95%B0%E3%81%A8%E7%AE%97%E8%A1%93">ラムダ計算#自然数と算術
     *      - Wikipedia</a>
     */
    @Test
    void testChurchNumerals() {
        logger.info("*** " + Common.methodName());
        define("0", "λf x.x");
        define("1", "λf x.f x");
        define("2", "λf x.f(f x)");
        define("3", "λf x.f(f(f x))");
        define("succ", "λn f x.f(n f x)");
        assertEquivalent("1", "succ 0");
        assertEquivalent("2", "succ 1");
        assertEquivalent("3", "succ 2");
        define("+", "λm n f x.m f(n f x)");
        assertEquivalent("1", "+ 0 1");
        assertEquivalent("1", "+ 1 0");
        assertEquivalent("2", "+ 1 1");
        assertEquivalent("3", "+ 1 2");
        assertEquivalent("3", "+ 2 1");
        define("*", "λm n f.m(n f)");
        assertEquivalent("0", "* 0 1");
        assertEquivalent("0", "* 1 0");
        assertEquivalent("2", "* 1 2");
        assertEquivalent("2", "* 2 1");
        assertEquivalent("3", "* 1 3");
        assertEquivalent("3", "* 3 1");
        define("pred", "λn f x.n(λg h.h(g f)) (λu.x) (λu.u)");
        assertEquivalent("0", "pred 1");
        assertEquivalent("1", "pred 2");
        assertEquivalent("2", "pred (succ 2)");
        define("pred2", "λn.n(λg k.(g 1)(λu.+(g k)1)k)(λv.0)0");
        assertEquivalent("0", "pred2 1");
        assertEquivalent("2", "pred2 3");
    }


    /**
     * @see <a href=
     *      "https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E8%AB%96%E7%90%86%E8%A8%98%E5%8F%B7%E3%81%A8%E8%BF%B0%E8%AA%9E">ラムダ計算#論理記号と述語
     *      - Wikipedia</a>
     */
    @Test
    void testCharchBooleans() {
        logger.info("*** " + Common.methodName());
        define("true", "λt f.t");
        define("false", "λt f.f");
        define("test", "λp t f.p t f");
        define("and", "λp q.p q false");
        define("or", "λp q.p true q");
        define("not", "λp.p false true");
        assertEquivalent("true", "and true true");
        assertEquivalent("false", "and true false");
        assertEquivalent("false", "and false true");
        assertEquivalent("false", "and false false");
        assertEquivalent("true", "or true true");
        assertEquivalent("true", "or true false");
        assertEquivalent("true", "or false true");
        assertEquivalent("false", "or false false");
        assertEquivalent("false", "not true");
        assertEquivalent("true", "not false");
        assertEquivalent("v", "test (and true true) v w");
        assertEquivalent("w", "test (and true false) v w");
        assertEquivalent("w", "test (and false true) v w");
        assertEquivalent("w", "test (and false false) v w");
        assertEquivalent("v", "test (or true true) v w");
        assertEquivalent("v", "test (or true false) v w");
        assertEquivalent("v", "test (or false true) v w");
        assertEquivalent("w", "test (or false false) v w");
        assertEquivalent("w", "test (not true) v w");
        assertEquivalent("v", "test (not false) v w");
    }

    /**
     * @see <a href=
     *      "https://ja.wikipedia.org/wiki/%E3%83%A9%E3%83%A0%E3%83%80%E8%A8%88%E7%AE%97#%E5%AF%BE">ラムダ計算#対
     *      - Wikipedia</a>
     */
    @Test
    void testChurchPairs() {
        logger.info("*** " + Common.methodName());
        define("true", "λt f.t");
        define("false", "λt f.f");
        define("cons", "λs b f.f s b");
        define("car", "λp.p true");
        define("cdr", "λp.p false");
        define("[]", "false");
        define("[A]", "cons A []");
        define("[AB]", "cons A (cons B [])");
        define("[ABC]", "cons A (cons B (cons C []))");
        assertEquivalent("λx.x A false", "[A]");
        assertEquivalent("λx.x A (λx.x B false)", "[AB]");
        assertEquivalent("λx.x A (λx.x B (λx.x C false))", "[ABC]");
        assertEquivalent("A", "car [A]");
        assertEquivalent("[]", "cdr [A]");
        assertEquivalent("A", "car [AB]");
        assertEquivalent("B", "car(cdr [AB])");
        assertEquivalent("[]", "cdr(cdr [AB])");
        assertEquivalent("λx.x", "car(cdr(cdr [AB]))"); // 空リストのcarはidになる。
        assertEquivalent("λx y.x", "car(cdr(cdr(cdr [AB])))"); // 空リストのcdrのcarはtrueになる。
    }

    /**
     * @see <a href=
     *      "https://en.wikipedia.org/wiki/Church_encoding#List_encodings">Church
     *      encoding#List encodings - Wikipedia</a>
     */
    @Test
    void testListEncodings() {
        logger.info("*** " + Common.methodName());
        define("true", "λt f.t");
        define("false", "λt f.f");
        define("nil", "false");
        define("isnil", "λl.l (λh.λt.false) true");
        define("cons", "λh.λt.λc.λn.c h (t c n)");
        define("head", "λl.l (λh.λt.h) false");
        define("tail", "λl.λc.λn.l (λh.λt.λg.g h (t c)) (λt.n) (λh.λt.t)");
        assertEquivalent("λc.λn.c A n", "cons A nil");
        assertEquivalent("λc.λn.c A (c B n)", "cons A (cons B nil)");
        assertEquivalent("λc.λn.c A (c B (c C n))", "cons A (cons B (cons C nil))");
        define("[ABC]", "cons A (cons B (cons C nil))");
        assertEquivalent("A", "head [ABC]");
        assertEquivalent("B", "head(tail [ABC])");
        assertEquivalent("C", "head(tail(tail [ABC]))");
        assertEquivalent("nil", "tail(tail(tail [ABC]))");
        assertEquivalent("true", "isnil nil");
        assertEquivalent("false", "isnil [ABC]");
        assertEquivalent("true", "isnil(tail(tail(tail [ABC])))");
        assertEquivalent("nil", "head(tail(tail(tail [ABC])))"); // nilのheadはnilになる。
        assertEquivalent("nil", "head(tail(tail(tail(tail [ABC]))))");
    }

    /**
     * @see <a href=
     *      "https://ja.wikipedia.org/wiki/SKI%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8D%E3%83%BC%E3%82%BF%E8%A8%88%E7%AE%97">SKIコンビネータ計算
     *      - Wikipedia</a>
     */
    @Test
    void testSKICombinator() {
        logger.info("*** " + Common.methodName());
        define("S", "λx y z.x z (y z)");
        define("K", "λx y.x");
        define("I", "λx.x");
        assertEquivalent("I", "S K K"); // IはS K Kで表現できる。
        assertEquivalent("λx.x", "S K K"); // IはS K Kで表現できる。
        assertEquivalent("λx.x", "S K S"); // 3番目はなんでもよい。
        assertEquivalent("a a", "(S I I) a"); // 自己適用
        assertEquivalent("b a", "(S (K (S I)) K) a b"); // 式の逆転
        define("T", "K"); // true
        define("F", "S K"); // false
        assertEquivalent("λt f.f", "F");
        define("NOT", "S (S I (K F)) (K T)");
        assertEquivalent("F", "NOT T");
        assertEquivalent("T", "NOT F");
        define("OR", "S I (K T)");
        assertEquivalent("T", "OR T T");
        assertEquivalent("T", "OR T F");
        assertEquivalent("T", "OR F T");
        assertEquivalent("F", "OR F F");
        define("AND", "S S (K (K F))");
        assertEquivalent("T", "AND T T");
        assertEquivalent("F", "AND T F");
        assertEquivalent("F", "AND F T");
        assertEquivalent("F", "AND F F");
    }

    /**
     * Iota and Jot#Universal iota - Wikipedia
     * https://en.wikipedia.org/wiki/Iota_and_Jot#Universal_iota
     */
    @Test
    void testUniversalIotaCombinator() {
        logger.info("*** " + Common.methodName());
        define("S", "λx y z.x z (y z)");
        define("K", "λx y.x");
        define("I", "λx.x");
        define("ι", "λf.f S K");
        assertEquivalent("I", "ι ι");
        assertEquivalent("K", "ι (ι (ι ι))");
        assertEquivalent("S", "ι (ι (ι (ι ι)))");
    }

    /**
     * iota = "1" | "0" iota iota
     */
    static String parseIota(String s) {
        return new Object() {
            int index = 0;
            int ch = get();

            int get() {
                return ch = index < s.length() ? s.charAt(index++) : -1;
            }

            String parse() {
                switch (ch) {
                case '0':
                    get();
                    return "(" + parse() + " " + parse() + ")";
                case '1':
                    get();
                    return "ι";
                default:
                    return "";
                }
            }
        }.parse();
    }

    @Test
    void testIotaParser() {
        logger.info("*** " + Common.methodName());
        assertEquals("((ι ι) (ι ι))", parseIota("0011011"));
        assertEquals("(ι (ι (ι ι)))", parseIota("0101011"));
        assertEquals("(((ι ι) ι) ι)", parseIota("0001111"));
    }

    /**
     * Iota and Jot#Iota - Wikipedia
     * https://en.wikipedia.org/wiki/Iota_and_Jot#Iota
     */
    @Test
    void testIotaCombinator() {
        logger.info("*** " + Common.methodName());
        define("S", "λx y z.x z (y z)");
        define("K", "λx y.x");
        define("I", "λx.x");
        define("ι", "λf.f S K");
        assertEquivalent("I", parseIota("011"));
        assertEquivalent("K", parseIota("0101011"));
        assertEquivalent("S", parseIota("010101011"));
    }

    /**
     * 不動点コンビネータ#Yコンビネータ - Wikipedia
     * https://ja.wikipedia.org/wiki/%E4%B8%8D%E5%8B%95%E7%82%B9%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8D%E3%83%BC%E3%82%BF#Y%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8D%E3%83%BC%E3%82%BF
     */
    @Test
    void testFixedPointCombinatorY() {
        logger.info("*** " + Common.methodName());
        try {
            define("Y", "(λf.(λx.f (x x)) (λx.f (x x)))");
            assertEquivalent("g (Y g)", "Y g");
            fail();
        } catch (StackOverflowError e) {
        }
    }

    /**
     * 不動点コンビネータ#Zコンビネータ - Wikipedia
     * https://ja.wikipedia.org/wiki/%E4%B8%8D%E5%8B%95%E7%82%B9%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8D%E3%83%BC%E3%82%BF#Z%E3%82%B3%E3%83%B3%E3%83%93%E3%83%8D%E3%83%BC%E3%82%BF
     */
    @Test
    void testFixedPointCombinatorZ() {
        logger.info("*** " + Common.methodName());
        try {
            define("Z", "λf.(λx.f (λy.x x y)) (λx.f (λy.x x y))");
            // globals.put("Z", parse("λf.(λx.f (λy. x x y)) (λx.f (λy.x x y))"));
            assertEquivalent("g (Z g)", "Z g");
            fail();
        } catch (StackOverflowError e) {
        }
    }


}
