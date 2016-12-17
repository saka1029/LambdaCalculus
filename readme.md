# Lambda calculus in Java

This is an implementation of [lambda calculus](https://en.wikipedia.org/wiki/Lambda_calculus) processor in Java.

## Syntax

```
Term   = Factor
       | Term Factor

Factor = Variable
       | Lambda
       | '(' Term ')'

Lambda = Variable '.' Term
```

### Variable

A variable is an identifier consisting of one or more alphanumeric characters.
You can also use characters `+`,  `-`,  `*`,  `/`, `|`,  `&`,  `!`, `^` and  `%` as a part of variable.

### Lambda

Normally, the lambda expression is expressed as `λ variables . body`,
but in this program it is expressed as `variable . body`.
Since only one variable is permitted,
if there are two or more variables, it must be with currying.
So you must express the lambda term `λxy.x` as `x.y.x`.
The dot operator (`.`) is right associative.
So the lambda term `x.y.x` is equivalent to `x.(y.x)`.


### Application

An application `t s` represents the application of a function `t` to an input `s`,
that is, it represents the act of calling function `t` on input `s` to produce `t(s)`.
The application operator (` `) is left associative.
So the lambda term `x y z` is equivalent to `(x y) z`.

## Command line

You can start REPL (Read Evaluation Print Loop) like this.

<pre><code>C:\projects\LambdaCalculus> <b>java -cp bin lambda.Term</b>
% <b>(x.x C) V</b>
V C
% <b>exit</b>

C:\projects\LambdaCalculus></code></pre>

`% ` is the prompt.
You can type a lambda term (for example `(x.x C) V`).
And the program prints the result of evaluation (for example `V C`).

### Special commands

* **exit** -- Terminate REPL.
* **quit** -- Terminate REPL.

### Built-in variables

* **true** -- The true value as lambda term.  That is `x.y.x`
* **false** -- The false value as lambda term.  That is `x.y.y`

### Built-in functions.

* **define V E** -- Define the variable V holds the value E.
* **trace B** -- `trace true` starts trace mode.  and `trace false` ends trace mode.
* **and B C** -- Returns a boolean value B and C.  That is `p.q.p q p`.
* **or B C** -- Returns a boolean value B or C.  That is `p.q.p p q`.
* **not B** -- Returns a boolean value not B.  That is `p.p true false`.


## Church numerals

Church numerals represents natural numbers in lambda term like this.

```
0   = a.b.b
1   = a.b.a b
2   = a.b.a(a b)
3   = a.b.a(a(a b))
```

`n`を受け取って`n + 1`を返す関数`succ`は以下のように表現します。

```
succ = n.f.x.f(n f x)
```

`succ n0`は以下のように評価されます。

```
> succ n0 : {n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
 > succ : {n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
 < n.f.x.f(n f x)
 > n0 : {n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
 < a.b.b
 > f.x.f(n f x) : {n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
  > x.f(n f x) : {f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
   > f(n f x) : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
    > f : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
    < f
    > n f x : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
     > n f : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
      > n : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
      < a.b.b
      > f : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
      < f
      > b.b : {a=f, x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
       > b : {b=b, a=f, x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
       < b
      < b.b
     < b.b
     > x : {x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
     < x
     > b : {b=x, x=x, f=f, n=a.b.b, n3=a.b.a(a(a b)), n2=a.b.a(a b), n1=a.b.a b, n0=a.b.b, plus=m.n.f.x.m f(n f x), succ=n.f.x.f(n f x)}
     < x
    < x
   < f x
  < x.f x
 < f.x.f x
< f.x.f x
```

結果は`f.x.f x`なので前述の`n1`つまり`a.b.a b`に一致することがわかります。
