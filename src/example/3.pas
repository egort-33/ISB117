program procedureProgram;
 label label1;
 var y, i, x: integer;
 var z : real;
 var c : array [0..10] of char ;
 procedure p;
 begin
 z := ( (y + 3) mod 2) + 0.1;

 end;
 begin
 z := 0.0;
 y := 2 + 2;
 y := y * 2 ;
 p;
 c[0] := 'x' ;
 c[1] := 'y' ;
 c[2] := 'z' ;
 c[3] := 'x' ;
 writeln(z);
 if c[3] = 'x' then

 writeln(c[0] , c[1], c[2]);

 else

 goto label1;

 c[0] := 'z' ;
 label1:
 writeln(c[0] , ' ' , c[3]);
 end.
