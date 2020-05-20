program ifelsetest;

var x, y: integer;

begin
    x := 10;
    y := 100;

    if x < y then
        writeln(x)
    else
        writeln(y);
end.