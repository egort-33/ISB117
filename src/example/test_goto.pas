program gototest;
 label label1;
 var x: integer;
 
 
 begin
 x := 5;
 if x = 5 then
 goto label1;
 
 x := 0;
 
 label1: writeln(x);
 
 end.
 