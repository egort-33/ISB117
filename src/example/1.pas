program casetest;
 var counter: integer;
 var resultingletter: char;
 var i : integer;
 
 begin
 counter := 100;
 for i := 0 to 3 do begin
 
 
 case (counter) of
 100 : resultingletter := 'a';
 90 : resultingletter := 'b';
 80 : resultingletter := 'c';
 70 : resultingletter := 'd';
 end;
 
 
 writeln(resultingletter);
 counter := counter - 10;
 writeln(counter);
 end;
 end.
 