program procedureProgram;
 var y, i, x: integer;
 var z : real;
 procedure p;
 begin
 //комментарий
 { комментарий - блок
 while z = 0 do begin
 }
while z = 0 do begin //комментарий в одной строке с кодом
end; //цикл  ниже пуст, его можно удалять, чтобы не вызвать бесконечное зацикливание
repeat
while z = 0 do begin
end;
until z = true;
 z := ( (y + 3) div 2) + 0.1;



 end;
 begin
 z := 0.0;
 y :=  2 + 2;
 y := y * 2 ;
 p;
 writeln(z);
 end.