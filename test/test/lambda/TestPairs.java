package test.lambda;

import static org.junit.Assert.*;
import lambda.Context;
import static lambda.LambdaCalculus.*;

import org.junit.Test;

public class TestPairs {
    
    /**
     * 対[編集]
     * （2つ組の）順序対のデータ型は、 TRUE および FALSE を用いて定義することができる。
     * これらはチャーチ対（英語版）（英: Church pairs）とよばれている。
     * CONS := λs b f. f s b
     * CAR := λp. p TRUE
     * CDR := λp. p FALSE
     * リンク型のリスト構造は、空リストのために特定の予約された値（例えば FALSE ）を用い、
     * リストをその先頭要素と後続リストの CONS 対として表現することによって実現できる。
     */
    @Test
    public void testPairs() {
        Context c = defaultContext();
        reduce("define true (x.y.x)", c);
        reduce("define false (x.y.y)", c);
        reduce("define nil (x.true)", c);
        reduce("define cons (s.b.f.f s b)", c);
        reduce("define car (p.p true)", c);
        reduce("define cdr (p.p false)", c);
        assertEquals(normalize("true", c), normalize("car (cons true false)", c));
        assertEquals(normalize("false", c), normalize("cdr (cons true false)", c));
        reduce("define ABC (cons A (cons B (cons C nil)))", c);
        assertEquals(normalize("A", c), normalize("car ABC", c));
        assertEquals(normalize("B", c), normalize("car (cdr ABC)", c));
        assertEquals(normalize("C", c), normalize("car (cdr (cdr ABC))", c));
        assertEquals(normalize("nil", c), normalize("cdr (cdr (cdr ABC))", c));
        assertEquals(normalize("cons B (cons C nil)", c), normalize("cdr ABC", c));
        System.out.println(reduce("ABC", c));
    }

}
