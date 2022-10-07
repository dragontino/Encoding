# Encoding
Простое приложение для кодирования информации по методу Шеннона – Фано

Метод Шеннона – Фано позволяет закодировать каждый символ текста кодами разной длины так, чтобы наиболее частым символам сопоставить коды меньшей длины.
Коды Шеннона – Фано префиксные, то есть никакое кодовое слово не является началом другого, что позволяет одназначно декодировать любую последовательность слов.
Такой метод позволяет эффективно передавать большие объемы информации, используя при этом минимальный объем памяти.

## Ввод значений
Для того, чтобы сгенерировать коды, нужно ввести символы и их вероятности. Это можно сделать двумя способами: 
1) Напрямую, введя символ и его вероятность в соответствующие поля. Количество символов неограничено, но не меньше 2.
В качестве имени может быть ***любая строка***, в качестве вероятности - ***десятичная дробь***. Сумма вероятностей должна равнятся 1.

### Начальный экран по умолчанию

  В светлой теме           |  В темной теме
:-------------------------:|:-------------------------:
<img src="https://user-images.githubusercontent.com/89968445/194556482-8ba45ca6-ec93-4757-ac65-e4df113e7147.jpg" width=50% height=50%> | <img src="https://user-images.githubusercontent.com/89968445/194556474-cf2a2212-3ff2-4834-9f4a-4755f3adfad9.jpg" width=50% height=50%>

2) Ввести текст, который надо закодировать. Для этого в настройках нужно включить соответсвующий пункт. 
Можно вводить текст любой длины. Знаки препинания и переходы на новую строку не учитываются при кодировании. Можно настроить, будут ли учитываться пробелы (по умолчанию учитываются)


 Экран с текстовым полем   |  Экран с результатом
:-------------------------:|:-------------------------:
<img src="https://user-images.githubusercontent.com/89968445/194556470-2c1eb589-a62d-47aa-9a59-3a268f7f909e.jpg" width=50% height=50%> | <img src="https://user-images.githubusercontent.com/89968445/194556282-864a7c06-32a2-4a2c-abe8-0df0b321eecc.jpg" width=50% height=50%>


Чтобы вывести результат, нажмите на кнопку ***«Вычислить коды символов по методу Фано»***

<p align="center">
  <h2>
    Экран настроек
  </h2>
  <img src="https://user-images.githubusercontent.com/89968445/194556459-3efa85bc-e3d9-479a-961f-99da4b2b82c3.jpg" width=35% height=35% />
</p>

В настройках можно переключить режим ввода символов, а также включить динамические цвета – т. е. цветовая палитра будет такой же, как на устройстве.

## Темы
В приложении поддерживается тёмная и светлая темы. Чтобы переключить их, нажмите на значок в правом верхнем углу.
