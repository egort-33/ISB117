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
	std::ifstream in("1.pas"); // �������� ���� ��� ������
	std::ofstream out("1-thumb.pas"); // �������� ���� ��� ������
	long int i = 0;
	string h;
	long int x = 0;
	long int l = -1;
	bool flag = false;
	bool comments = false;
	int layer[9999] = { 0 };
	char b;
	std::cout << "���� �������� ��� ��������� � ������ ��������� ������ � �������. � ���� ����� �������� ��������� ����� Begin � End!\n�����������:" << endl;
	std::cout << "~WS~ - ������" << endl << "~T value T~ - �����" << endl << "~\\n~ - ������� ������" << endl;



	out << "//� ����������� ���� �������� ������ ��������� ����� Begin - End ������!" << endl;


	std::cout << "[��� ������]" << endl;



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


							if ((layer[l] == 1) && (l >= 0)) //��������� begin
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
									cout << " ~T" << y << "T~ "; //�����
								}


								else if (y != '\n')
								{
									cout << " ~WS~ "; //�������
									proga.push_back(" ");
								}


								else
								{
									cout << " ~\\n~ "; //�������� �����
									proga.push_back("\n");
									/*if ((newword.length() > 0) && (layer[l] > 0))
										out << endl;*/
								}



								if (words[i] == "Begin")
								{
									std::cout << endl << "[���� ������:" << (l + 1) << "+]" << endl; //����������� ����������� ������
									l++; // -1 -> 0
									layer[l] = 1; //0-��� ��� 1-��������, 2-� ����, 3-�������

								}
								else if ((words[i] == "End"))
								{
									std::cout << endl << "[���� ������:" << (l) << "-]" << endl; //����������� ����������� ������
									layer[l] = 3; // 0 -> -1

								}

								i++;
							}
							else
								words[i] += y;



						}
						if (l >= 0)
						{
							if (layer[l] > 0) // � ���� ������
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


						if ((layer[l] == 1) && (l >= 0)) //��������� begin
							layer[l] = 2;
						if ((y == '(') || (y == ')') || (y == '+') || (y == '-') || (y == '/') || (y == '*') || (y == '\'') || (y == ' ') || (y == '\n') || (y == ';') || (y == ':') || (y == '=') || (y == ',') || (y == '.'))
						{


							std::cout << words[i];
							if (words[i].length() > 0)
								proga.push_back(words[i]);
							if ((y != ' ') && (y != '\n'))
							{
								cout << " ~T" << y << "T~ "; //������
								h = y;
								proga.push_back(h);

							}


							else if (y != '\n')
							{
								cout << " ~WS~ "; //�������
								proga.push_back(" ");
							}

							else
							{
								cout << " ~\\n~ "; //�������� �����
								proga.push_back("\n");
								/*	if ((newword.length() > 0)&& (layer[l] > 0))
									out << endl;*/
							}


							if (words[i] == "Begin")
							{
								std::cout << endl << "[���� ������:" << (l + 1) << "+]" << endl; //����������� ����������� ������

								l++; // -1 -> 0
								layer[l] = 1; //0-��� ��� 1-��������, 2-� ����, 3-�������

							}
							else if ((words[i] == "End"))
							{
								std::cout << endl << "[���� ������:" << (l) << "-]" << endl;  //����������� ����������� ������
								layer[l] = 3; // 0 -> -1

							}

							i++;
						}
						else
							words[i] += y;



					}
					if (l >= 0)
					{
						if (layer[l] > 0) // � ���� ������
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
	in.close();     // ��������� ����
	out.flush();
	out.close();
	out.open("1-thumb.pas"); // ��������� ���� ��� �������� ������ ������ ������
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





	cout << endl << "�������� ������ ������:" << endl;
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





	cout << endl << "������ �� ������:" << endl;
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
	cout << "������ ����������������� ����" << endl;
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
			cout << "��������� � ��������! " << endl;
		}
		else if ( (p.front() == "'") && (cav == true))
		{
			cav = false;
			cout << "��������� � �������� �����������! "  << endl;
		}

		if (cav == false)
		{


			it = find(cl.begin(), cl.end(), p.front());

			if (!(std::all_of(p.front().begin(), p.front().end(), ::isdigit))&&(p.front() != " ")&& (p.front() != "\n"))
			{


				if (it != cl.end())
				{

					cout << "�����������������:" << p.front() << endl;

					if ((mainbody == true))
					{
						
						 if (p.front() == ":")
						{
							p.pop_front();
							if (p.front() == "=")
								cout << "���������� ���������� ��������" << endl;
							else
							{
								p.push_front(":");

							}

						}
						
						else if (p.front() == "writeln")
						{
							cout << "��������� ��������� ����������" << endl;
							writing = true;
						}
						else if (p.front() == "readln")
						{
							cout << "�������� ��������� ����������" << endl;
							writing = true;
						}
						else if (p.front() == "if")
						{
							cout << "�������" << endl;
							reqcond = true;
							cond = true;
						}
						else if ((reqcond == true) && (slot1 == true) && ( (p.front() == "=")|| (p.front() == ">") || (p.front() == "<") || (p.front() == "!")))
						{
							cout<<"���� ���������"<<endl;
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
							cout << "else ����" << endl;
							thenexpect = true;
							elseexpect = false;
							cond = false;
						}
						else if ((elseexpect == false) && (blockclosed == true) && (p.front() == "else"))
						{
							cout << "��� else ��� ����������� then" << endl;
							issue = true;
						}
						else if (p.front() == "for")
						{
							cout << "����������� ����" << endl;
							loopfor = true;
						}
						else if (p.front() == "while")
						{
							 cout << "����������� ����" << endl;
							 loopwhile = true;
							 reqcond = true;
							 cond = true;
						}
						else if ((loopwhile==true)&&(reqcond == true) && (slot1 == false) && (p.front() == "do"))
						{
							 cout << "���������� ������� while" << endl;
							 issue = true;
						}
						else if (p.front() == "repeat")
						 {
							 cout << "����������� ����" << endl;
							 looprepeat = true;
							 
						 }
						else if ((looprepeat == true) && (reqcond == true) && (slot1 == false) && (p.front() == ";"))
						 {
							 cout << "���������� ������� repeat - until" << endl;
							 issue = true;
						 }
						else if ((loopforto == false)&& (loopfor == true) && ((p.front() != ":") && (p.front() != "to") && (p.front() != "do") && (p.front() != "Begin")))
						{
							cout << "������������ ��������� �����" << endl;
							issue = true;
						}
						else if ((loopfor == true) && (p.front() == "to"))
						{
							cout << "���� ��" << endl;
							loopforto = true;
						}
						else if ((loopfor == true) && (p.front() == "do"))
						{
							cout << "��������� �������� ����" << endl;
							
						}
						else if ((loopfor == true) && (p.front() == "Begin"))
						{
							cout << "��������� ���� ����" << endl;
							loopfor = false;
							blockclosed = false;
						}
						else if ((blockclosed == false) && (p.front() == "End"))
						{
							cout << "����������� ���� �����������" << endl;
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
									cout << "�������� = ��� ���������� �����" << endl;
									issue = true;
								}

							
						}
						else if ( (loopwhileok == false) &&(loopwhile == true) &&(p.front() != "do") && (p.front() != "Begin"))
						{
						cout << "������������ ��������� �����" << endl;
						issue = true;
						}
						else if ((loopwhile == true) && (p.front() == "do"))
						{
						cout << "���� while ����" << endl;
						loopwhileok = true;
						}
						else if ((loopwhile == true) && (p.front() == "do"))
						{
						cout << "��������� �������� ����" << endl;

						}
						else if ((loopwhile == true) && (p.front() == "Begin"))
						{
						cout << "��������� ���� ����" << endl;
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
						cout << "����������� ���� �����������" << endl;
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
							cout << "��������� ������" << endl;
							
							issue = true;
						}
						else if ((writing == true) && (p.front() == "(") && (body == false))
						{
							cout << "������ �������" << endl;
							body = true;
						}
						else if ((body == true) && (p.front() == ")"))
						{
							cout << "������ �������" << endl;
							writing = false;
							
						}
						else if ((writing == false) && (body == true) && (p.front() != ";"))
						{
							cout << "��������� ; " << endl;
							issue = true;
						}
						else if ((writing == false) && (body == true) && (p.front() == ";"))
						{
							body = false;
						}
					}
					


					if ((p.front() == "+") || (p.front() == "-") || (p.front() == "*") || (p.front() == "/"))
					{
						cout << "�������������� ��������" << endl;
						arslot1 = true;
					}
					
					else if (p.front() == "Begin")
					{
						mainbody = true;
						varinit = false;
						cout << "����������� ����" << endl;
					}

					else if ((varinit == true) && (mainbody == false))
					{
						

						if ((perech == true)&&(p.front() != ",") && (p.front() != ":"))
						{
							cout << " ������, ��������� ��������� ���������� ��� ����������" << endl;
							issue = true;
							perech = false;
						}

						else if ((perech == false)&&(p.front() == ","))
						{
							cout << "��������� ���������� ��������� ����������"  << endl;
							perech = true;
						}
						else if ((perech == true) && (p.front() == ","))
						{
							cout << "��������� ���������� ��������� ����������" << endl;
							perech = true;
						}
						else if (p.front() == ":")
						{
							p.pop_front();
							if (p.front() == "=")
								cout << "���������� ���������� ��������"  << endl;
							else
							{
								p.push_front(":");
								cout << "����������� ����������"  << endl;
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
							cout << "��������� ����������" << p.front() << endl;
							perech = false;
							typed = false;
							declared = true;
						}
						else if ((p.front() == ";") && (declared == true))
						{
							cout << " ������, ������ ;" << endl;
							issue = true;
						}
						else if ((p.front() == "real") || (p.front() == "integer") || (p.front() == "string"))
						{
							if (typed == false)
							{
								cout << "��� ��������: " << p.front() << endl;
								typed = true;
							}
							else
							{
								cout << "���������� ���� ������� � ������������ �����" << endl;
								typed = false;
							}

						}

						else
						{
							cout << "������ - ������������ ����������������� ����� � ���������� ����������" << endl;
							issue = true;
						}

					}


					else if ((varinit == false) && (p.front() == "var")) {




						cout << "���������� ���������� ������" << endl;
						varinit = true;



					}
					else if ((varinit == true) && (p.front() == "var"))
					{
						cout << "������ - ��������� var" << endl;
						issue = true;
					}

				}
				else
				{
					cout << "�������������������:" << p.front() << endl;
					if ((varinit == true)&&(mainbody==false))
					{
						cout << "���������� " << p.front() << " �����������" << endl;
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
				cout <<"�������� �������� "<< p.front() << endl;
			}
		}
		p.pop_front();
		
	}
	cin >> x;
	return 0;
}