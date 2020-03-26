//comment 1
var s:string;
    r:real;
{ block comments
asdas 
dsa
das
d
asd
as
} i,j,n:integer;

r:=r+1;///comment 2

r:=r+1;//comment 3

r:=r+1/n;

Begin //comment 4
    r:=0;
    readln(s); // comment 5
{s
sad
dds
asd}
for i:=1 to length(s) do Begin //comment 6
       n:=0;
for j:=1 to length(s) do Begin //comment 7
          if s[i]=s[j] then inc(n);
Begin
Begin
End;
End;
Begin
End;
       End; //comment 8
       r:=r+1/n;
for u:=1 to length(s) do Begin //comment 9
          if s[i]=s[j] then inc(n);
       End; //comment 10
    End; //comment 11
    writeln('количество различных букв = ', r:1:0);
End. //comment 12
