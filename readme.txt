В версии программы на C++ была частично реализована диагностическая часть, которая анализирует обработанный код на предмет
возможных синтаксических ошибок а также выводит соответствующие сообщения. При запуске программы она получает на вход файл с кодом, 
удаляет из него комментарии, вложенные блоки неограниченной глубины, что не содержат каких либо операций, декомпозирует 
обработанный код на специальные символы и их комбинации, зарезервированные и незарезервированные слова, числовые значения.
Каких либо исправлений ошибок не было реализовано, вся программа выводит лишь некоторые предусмотренные диагностические сообщения 
об исходном коде.
В выводимый файл выводится код с удаленными комментариями и ликвидированными пустыми вложенными блоками.
Ориентировочный ЯП - pascal
От 17.05 - Описанная ранее на C++ программа перенесена на Java и дополнена возможностями компилятора (папка src)
От 23.05 - Проведены контрольные тесты программы, определен синтаксис
Для запуска -
Указать путь к каталогу с проектом
cd ...src/
Скомпилировать командой javac
javac Run.java
Запустить
java Run
