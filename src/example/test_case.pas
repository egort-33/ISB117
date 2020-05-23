program casetest;
 var Number: integer;
 var Letter: char;
 
 
 begin
 Number := 80;
 
 case (Number) of
 110 : Letter := 'a';
 93 : Letter := 'b';
 80 : Letter := 'c';
 60 : Letter := 'd';
 end;
 
 writeln(Letter);
 end.
 