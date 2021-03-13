setlocal
:::set OPT=--enable-preview
set CP=bin
java %OPT% -cp %CP% lambda.LambdaCalculus %1 %2 %3 %4 %5 %6 %7 %8 %9
endlocal
