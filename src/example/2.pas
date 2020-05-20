program mid;
var x, sum, count: integer;
var average: real;
var a: array[0..4] of integer;

begin
    sum := 0;
    count := 5;

    a[0] := 20;
    a[1] := 32;
    a[2] := 50;
    a[3] := 89;
    a[4] := 90;


    for x := 0 to 4 do
    begin
       sum := sum + a[x];
    end;

    average := sum/count;
    writeln(average);
end.