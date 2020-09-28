package test.lambda;

import static lambda.LambdaCalculus.defaultContext;
import static lambda.LambdaCalculus.normalize;
import static lambda.LambdaCalculus.reduce;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

import lambda.Context;
import lambda.LambdaCalculus;

public class TestYCombinator {

    /**
     * Y combinator
     *
     * Fixed point combinators in lambda calculus
     * The Y combinator, discovered by Haskell B. Curry, is defined as:

     * Y=λ f.(λ x.f (x x)) (λ x.f (x x))
     *
     * Yコンビネータ
     * 型無しラムダ計算においてよく知られた（そしておそらく最もシンプルな）
     * 不動点コンビネータはYコンビネータと呼ばれる。
     * これはハスケル・カリーによって発見されたもので、次のように定義される。
     * Y = (λf . (λx . f (x x)) (λx . f (x x)))
     *
     * !!!! 今の強欲なeduce(簡約)の実装ではStackOverflowが発生する。
     */
    @Test(expected = StackOverflowError.class)
    public void testYCombinator() {
        // Y=λ f.(λ x.f (x x)) (λ x.f (x x))
        Context c = defaultContext();
        LambdaCalculus.TRACE = true;
        reduce("define Y (λ f.(λ x.f (x x)) (λ x.f (x x)))", c);
        assertEquals(normalize("g (Y g)", c), normalize("Y g", c));
    }

}
