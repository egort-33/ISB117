#include <iostream>
#include <fstream>
#include <string>
#include <algorithm>
#include <conio.h>
#include <list>
using namespace std;
list <string> p, r;

list <string> colle = { "Begin","End" , "string","." , "," , "+" , "-" , "*" , "/","//" , "=" , "Procedure" ,"Function", "(" , ")" , "writeln" ,"for","to","length","do"," ","if","then","inc","readln",":","integer","real","for",";","var", "\'", "\n", ":=" };
bool cleared = false;
void clearsp()
{
	p.push_back(r.front());
	r.pop_front();
	if ((r.front() == " ") || (r.front() == "\n"))
	{
		p.push_back(r.front());
		r.pop_front();
	}
	if ((r.front() == " ") || (r.front() == "\n"))
	{
		p.push_back(r.front());
		r.pop_front();
	}
	if (r.front() == "Begin")
		clearsp();
	else if (r.front() == "End") //reached end with just spaces
	{
		r.pop_front();
		r.pop_front();
		if ((p.back() == " ") || (p.back() == "\n"))
			p.pop_back();
		if ((p.back() == " ") || (p.back() == "\n"))
			p.pop_back();
		p.pop_back();

		cleared = false;
	}

	else if (cleared == true)
	{
		p.push_back(r.front());
		r.pop_front();
	}
}


int main()
{
	setlocale(LC_ALL, "Russian");
	std::string line;
	std::string words[9999];
	std::string newword;

	list <string> proga;
	std::ifstream in("1.pas"); // окрываем файл для чтения
	std::ofstream out("1-thumb.pas"); // окрываем файл для чтения
	long int i = 0;
	string h;
	long int x = 0;
	long int l = -1;
	bool flag = false;
	bool comments = false;
	int layer[9999] = { 0 };
	char b;
	std::cout << "НИЖЕ ПРИВЕДЕН ВИД ПРОГРАММЫ С УЧЕТОМ ВЫДЕЛЕНИЯ ЛЕКСЕМ И ТОКЕНОВ. В ФАЙЛ БУДУТ ВЫНЕСЕНЫ ФРАГМЕНТЫ МЕЖДУ Begin и End!\nОБОЗНАЧЕНИЯ:" << endl;
	std::cout << "~WS~ - Пробел" << endl << "~T value T~ - Токен" << endl << "~\\n~ - Перенос строки" << endl;



	out << "//В приведенном коде выделены только фрагменты между Begin - End парами!" << endl;


	std::cout << "[Нет скобок]" << endl;



	if (in.is_open())
	{

		while (getline(in, line))
		{

			for (char c : line)
			{
				b = c;
				if ((c == '/') && (flag == false))
				{

					flag = true;
				}


				else if ((c != '/') && (flag == true) && (comments == false))
				{
					flag = false;

					newword += '/';
				}

				else if ((c == '/') && (flag == true))
					comments = true;



				if ((c == '{') && (comments == false))
				{
					comments = true;

				}



				if ((comments == false) && (flag == false))
				{


					if (c != ' ')
						newword += c;



					if ((c == ' ') && ((newword.length() >= 1) && (newword.at(0) != ' '))) // "a " "abc "
					{
						newword += c;

						for (char y : newword)
						{


							if ((layer[l] == 1) && (l >= 0)) //пересекли begin
								layer[l] = 2;
							if ((y == '(') || (y == ')') || (y == '+') || (y == '-') || (y == '/') || (y == '*') || (y == ' ') || (y == '\'') || (y == '\n') || (y == ';') || (y == ':') || (y == '=') || (y == ',') || (y == '.'))
							{


								std::cout << words[i];
								if (words[i].length() > 0)
									proga.push_back(words[i]);
								if ((y != ' ') && (y != '\n'))
								{
									h = y;
									proga.push_back(h);
									cout << " ~T" << y << "T~ "; //токен
								}


								else if (y != '\n')
								{
									cout << " ~WS~ "; //пробелы
									proga.push_back(" ");
								}


								else
								{
									cout << " ~\\n~ "; //переносы строк
									proga.push_back("\n");
									/*if ((newword.length() > 0) && (layer[l] > 0))
										out << endl;*/
								}



								if (words[i] == "Begin")
								{
									std::cout << endl << "[Слой скобок:" << (l + 1) << "+]" << endl; //открывающая операторная скобка
									l++; // -1 -> 0
									layer[l] = 1; //0-еще нет 1-открытие, 2-в теле, 3-закрыли

								}
								else if ((words[i] == "End"))
								{
									std::cout << endl << "[Слой скобок:" << (l) << "-]" << endl; //закрывающая операторная скобка
									layer[l] = 3; // 0 -> -1

								}

								i++;
							}
							else
								words[i] += y;



						}
						if (l >= 0)
						{
							if (layer[l] > 0) // в теле скобки
							{
								if (newword.length() > 0)
									out << newword;
							}

							if (layer[l] == 3)
								l--;
						}

						newword = "";

						x = 0;
					}
				}
				if ((c == '}') && (comments == true))
				{
					comments = false;
					flag = false;
				}

			}
			if (comments == false)
			{
				if (b != ' ')
				{
					newword += '\n';

					for (char y : newword)
					{


						if ((layer[l] == 1) && (l >= 0)) //пересекли begin
							layer[l] = 2;
						if ((y == '(') || (y == ')') || (y == '+') || (y == '-') || (y == '/') || (y == '*') || (y == '\'') || (y == ' ') || (y == '\n') || (y == ';') || (y == ':') || (y == '=') || (y == ',') || (y == '.'))
						{


							std::cout << words[i];
							if (words[i].length() > 0)
								proga.push_back(words[i]);
							if ((y != ' ') && (y != '\n'))
							{
								cout << " ~T" << y << "T~ "; //токены
								h = y;
								proga.push_back(h);

							}


							else if (y != '\n')
							{
								cout << " ~WS~ "; //пробелы
								proga.push_back(" ");
							}

							else
							{
								cout << " ~\\n~ "; //переносы строк
								proga.push_back("\n");
								/*	if ((newword.length() > 0)&& (layer[l] > 0))
									out << endl;*/
							}


							if (words[i] == "Begin")
							{
								std::cout << endl << "[Слой скобок:" << (l + 1) << "+]" << endl; //открывающая операторная скобка

								l++; // -1 -> 0
								layer[l] = 1; //0-еще нет 1-открытие, 2-в теле, 3-закрыли

							}
							else if ((words[i] == "End"))
							{
								std::cout << endl << "[Слой скобок:" << (l) << "-]" << endl;  //закрывающая операторная скобка
								layer[l] = 3; // 0 -> -1

							}

							i++;
						}
						else
							words[i] += y;



					}
					if (l >= 0)
					{
						if (layer[l] > 0) // в теле скобки
						{

							if (newword.length() > 0)
								out << newword;
						}

						if (layer[l] == 3)
							l--;
					}

					newword = "";

					x = 0;







					/*	std::cout << newword;

						words[i] = newword;

						out << words[i];
						i++;
						newword = "";

						x = 0;*/
				}






			}

			if ((flag == true) && (comments == true))
			{
				comments = false;
				flag = false;
			}




		}
	}
	in.close();     // закрываем файл
	out.flush();
	out.close();
	out.open("1-thumb.pas"); // временная мера для проверки чистки пустых блоков
	cout << endl;
	r = proga;
	while (!proga.empty())
	{
		if ((proga.front() != " ") && (proga.front() != "\n"))
			cout << "," << proga.front();
		else if (proga.front() == " ")
			cout << "," << "~WS~";
		else if (proga.front() == "\n")
			cout << "," << "\\n";
		proga.pop_front();
	}





	cout << endl << "Удаление пустых блоков:" << endl;
	int levels = 0;
	cleared = false;
	while (cleared == false)
	{
		cleared = true;

		while (!r.empty())
		{
			if (r.front() == "Begin")
				clearsp();




			else if ((r.front() == "End"))
			{
				p.push_back(r.front());
				r.pop_front();
			}
			else
			{
				p.push_back(r.front());
				r.pop_front();
			}
		}
		if (cleared == false)
		{


			r = p;

			p.clear();
		}
	}
	r = p;
	p.clear();

	while (!r.empty())
	{


		if (r.front() == "Begin")
		{
			p.push_back(r.front());

			r.pop_front();
			p.push_back(r.front());
			r.pop_front(); //ws for sure

			if (r.front() == "End") //reached end with just spaces
			{
				r.pop_front();
				r.pop_front(); //;
				p.pop_back();
				p.pop_back();
			}

			else
			{
				p.push_back(r.front());
				r.pop_front();
			}


		}
		else if (r.front() == "End")
		{
			p.push_back(r.front());
			r.pop_front();
		}
		else
		{
			p.push_back(r.front());
			r.pop_front();
		}
	}





	cout << endl << "Анализ на списке:" << endl;
	int lvl = -1;
	r = p;
	while (!p.empty())
	{
		cout << p.front();
		out << p.front();

		if (p.front() == "Begin")
		{
			lvl = lvl + 1;

			cout << endl << "		+ level: " << lvl << endl;

		}
		else if (p.front() == "End")
		{
			p.pop_front();
			cout << p.front();
			out << p.front();
			cout << endl << "		- level: " << lvl << endl;
			lvl = lvl - 1;
			if (lvl > 0)
				cout << "		= level: " << lvl << endl;
		}
		p.pop_front();

	}
	out.close();
	p = r;
	cout << "Анализ зарезервированных слов" << endl;
	list<string>cl;
	std::list<std::string>::iterator it;
	cl = colle;
	bool cav = false;
	bool varinit = false;
	bool mainbody = false;
	bool issue = false;
	bool perech = false;
	bool typed = true;
	bool declared = false;
	bool oper = false;
	bool writing = false;
	bool body = false;
	bool loopfor = false;
	bool loopforto = false;
	bool reqcond = false;
	bool slot1 = false;
	bool slot2 = false;
	bool arslot1 = false;
	bool arslot2 = false;
	bool compr = false;
	bool thenexpect = true;
	bool elseexpect = false;
	bool cond = false;
	bool loopwhile = false;
	bool loopwhileok = false;
	bool blockclosed = true;
	bool looprepeat = true;
	while (!p.empty())
	{

		if ((p.front() == "'") && (cav == false))
		{
			cav = true;
			cout << "Выражение в кавычках! " << endl;
		}
		else if ( (p.front() == "'") && (cav == true))
		{
			cav = false;
			cout << "Выражение в кавычках закончилось! "  << endl;
		}

		if (cav == false)
		{


			it = find(cl.begin(), cl.end(), p.front());

			if (!(std::all_of(p.front().begin(), p.front().end(), ::isdigit))&&(p.front() != " ")&& (p.front() != "\n"))
			{


				if (it != cl.end())
				{

					cout << "Зарезервированное:" << p.front() << endl;

					if ((mainbody == true))
					{
						
						 if (p.front() == ":")
						{
							p.pop_front();
							if (p.front() == "=")
								cout << "Переменной присвается значение" << endl;
							else
							{
								p.push_front(":");

							}

						}
						
						else if (p.front() == "writeln")
						{
							cout << "Выводится следующая информация" << endl;
							writing = true;
						}
						else if (p.front() == "readln")
						{
							cout << "Вводится следующая информация" << endl;
							writing = true;
						}
						else if (p.front() == "if")
						{
							cout << "условие" << endl;
							reqcond = true;
							cond = true;
						}
						else if ((reqcond == true) && (slot1 == true) && ( (p.front() == "=")|| (p.front() == ">") || (p.front() == "<") || (p.front() == "!")))
						{
							cout<<"знак сравнения"<<endl;
								compr = true;
						}
						else if ((reqcond == true) && (slot2 == true)&&(p.front()=="then"))
						{
							reqcond = false;
							slot1 = false;
							slot2 = false;

						}
						else if ((reqcond == true) && (slot2 == true) && (p.front() == "do"))
						 {
							 reqcond = false;
							 slot1 = false;
							 slot2 = false;

						 }
						else if ((elseexpect==true) && (blockclosed == true)&& (p.front() == "else"))
						{
							cout << "else блок" << endl;
							thenexpect = true;
							elseexpect = false;
							cond = false;
						}
						else if ((elseexpect == false) && (blockclosed == true) && (p.front() == "else"))
						{
							cout << "для else нет подходящего then" << endl;
							issue = true;
						}
						else if (p.front() == "for")
						{
							cout << "Объявляется цикл" << endl;
							loopfor = true;
						}
						else if (p.front() == "while")
						{
							 cout << "Объявляется цикл" << endl;
							 loopwhile = true;
							 reqcond = true;
							 cond = true;
						}
						else if ((loopwhile==true)&&(reqcond == true) && (slot1 == false) && (p.front() == "do"))
						{
							 cout << "Отсутсвует условие while" << endl;
							 issue = true;
						}
						else if (p.front() == "repeat")
						 {
							 cout << "Объявляется цикл" << endl;
							 looprepeat = true;
							 
						 }
						else if ((looprepeat == true) && (reqcond == true) && (slot1 == false) && (p.front() == ";"))
						 {
							 cout << "Отсутсвует условие repeat - until" << endl;
							 issue = true;
						 }
						else if ((loopforto == false)&& (loopfor == true) && ((p.front() != ":") && (p.front() != "to") && (p.front() != "do") && (p.front() != "Begin")))
						{
							cout << "Неправильный синтаксис цикла" << endl;
							issue = true;
						}
						else if ((loopfor == true) && (p.front() == "to"))
						{
							cout << "Цикл до" << endl;
							loopforto = true;
						}
						else if ((loopfor == true) && (p.front() == "do"))
						{
							cout << "Выполнять действие ниже" << endl;
							
						}
						else if ((loopfor == true) && (p.front() == "Begin"))
						{
							cout << "Выполнять блок ниже" << endl;
							loopfor = false;
							blockclosed = false;
						}
						else if ((blockclosed == false) && (p.front() == "End"))
						{
							cout << "Выполняемый блок закрывается" << endl;
							blockclosed = true;
							elseexpect = true;
						}
						else if (loopfor == true)
						{
							loopfor = false;
							blockclosed = true;
							elseexpect = true;
						}
						else if ((loopfor == true) && (p.front() == ":"))
						{
							
								p.pop_front();
								if (p.front() == "=")
									issue = issue;
								else
								{
									p.push_front(":");
									cout << "Пропущен = при объявлении цикла" << endl;
									issue = true;
								}

							
						}
						else if ( (loopwhileok == false) &&(loopwhile == true) &&(p.front() != "do") && (p.front() != "Begin"))
						{
						cout << "Неправильный синтаксис цикла" << endl;
						issue = true;
						}
						else if ((loopwhile == true) && (p.front() == "do"))
						{
						cout << "Цикл while пока" << endl;
						loopwhileok = true;
						}
						else if ((loopwhile == true) && (p.front() == "do"))
						{
						cout << "Выполнять действие ниже" << endl;

						}
						else if ((loopwhile == true) && (p.front() == "Begin"))
						{
						cout << "Выполнять блок ниже" << endl;
						loopwhile = false;
						blockclosed = false;
						}




						else if ((looprepeat == true)  && (p.front() == "until"))
						{
						reqcond = true;
						cond = true;
						
						}
						
						
						



						else if ((blockclosed == false) && (p.front() == "End"))
						{
						cout << "Выполняемый блок закрывается" << endl;
						blockclosed = true;
						if (cond==true)
						elseexpect = true;
						}
						else if (loopwhile == true)
						{

						loopwhile = false;
						blockclosed = true;
						if (cond == true)
						elseexpect = true;
						}
						
						else if ((writing == true) && (p.front() != "(")&&(body==false))
						{
							cout << "Пропущена скобка" << endl;
							
							issue = true;
						}
						else if ((writing == true) && (p.front() == "(") && (body == false))
						{
							cout << "Скобка открыта" << endl;
							body = true;
						}
						else if ((body == true) && (p.front() == ")"))
						{
							cout << "Скобка закрыта" << endl;
							writing = false;
							
						}
						else if ((writing == false) && (body == true) && (p.front() != ";"))
						{
							cout << "Пропущена ; " << endl;
							issue = true;
						}
						else if ((writing == false) && (body == true) && (p.front() == ";"))
						{
							body = false;
						}
					}
					


					if ((p.front() == "+") || (p.front() == "-") || (p.front() == "*") || (p.front() == "/"))
					{
						cout << "Арифметические операции" << endl;
						arslot1 = true;
					}
					
					else if (p.front() == "Begin")
					{
						mainbody = true;
						varinit = false;
						cout << "открывается блок" << endl;
					}

					else if ((varinit == true) && (mainbody == false))
					{
						

						if ((perech == true)&&(p.front() != ",") && (p.front() != ":"))
						{
							cout << " Ошибка, пропущена очередная переменная для объявления" << endl;
							issue = true;
							perech = false;
						}

						else if ((perech == false)&&(p.front() == ","))
						{
							cout << "Ожидается объявление следующей переменной"  << endl;
							perech = true;
						}
						else if ((perech == true) && (p.front() == ","))
						{
							cout << "Ожидается объявление следующей переменной" << endl;
							perech = true;
						}
						else if (p.front() == ":")
						{
							p.pop_front();
							if (p.front() == "=")
								cout << "Переменной присвается значение"  << endl;
							else
							{
								p.push_front(":");
								cout << "Объявляются переменные"  << endl;
								perech = false;
								typed = false;
								declared = false;
							}
							
						}
						
						else if ((arslot2 == true))
						{
							arslot1 = false;
							arslot2 = false;
						}
						else if ((typed==true)&&(p.front() == ";") && (declared==false))
						{
							cout << "Объявлены переменные" << p.front() << endl;
							perech = false;
							typed = false;
							declared = true;
						}
						else if ((p.front() == ";") && (declared == true))
						{
							cout << " Ошибка, лишняя ;" << endl;
							issue = true;
						}
						else if ((p.front() == "real") || (p.front() == "integer") || (p.front() == "string"))
						{
							if (typed == false)
							{
								cout << "Тип объявлен: " << p.front() << endl;
								typed = true;
							}
							else
							{
								cout << "Объявление типа указано в неправильном месте" << endl;
								typed = false;
							}

						}

						else
						{
							cout << "ОШИБКА - недопустимые зарезервированные слова в объявлении переменных" << endl;
							issue = true;
						}

					}


					else if ((varinit == false) && (p.front() == "var")) {




						cout << "Объявление переменных начато" << endl;
						varinit = true;



					}
					else if ((varinit == true) && (p.front() == "var"))
					{
						cout << "ОШИБКА - Повторное var" << endl;
						issue = true;
					}

				}
				else
				{
					cout << "Незарезервированное:" << p.front() << endl;
					if ((varinit == true)&&(mainbody==false))
					{
						cout << "Переменная " << p.front() << " объявляется" << endl;
					}
					if (loopforto == true)
					{
						loopforto = false;
					}
					if (loopwhileok == true)
					{
						loopwhileok = false;
					}
					if ((reqcond == true) && (slot1 == false))
					{
						slot1 = true;
					}
					else if ((reqcond == true) && (slot2 == false))
					{
						slot2 = true;
					}
					else if (arslot1 == true)
						arslot2 = true;
				}
			
			}
			if ((std::all_of(p.front().begin(), p.front().end(), ::isdigit)))
			{
				cout <<"Числовое значение "<< p.front() << endl;
			}
		}
		p.pop_front();
		
	}
	cin >> x;
	return 0;
}