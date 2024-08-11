Spring Security App - Docker Development Environment.
Этот проект представляет собой Spring Boot приложение с использованием PostgreSQL в качестве базы данных. В этом README описаны шаги для настройки и запуска dev среды с использованием Docker и Docker Compose.

Требования:
Docker установлен на вашей машине.
Docker Compose установлен.

Для того чтобы запустить это приложение у себя на компьютере нужно клонировать его.

Запускаем командную строку и вводим:

git clone https://github.com/AngelIgor777/EffectiveMobile.git и после того как всё скачалось вводим :
cd C:\Users\user\IdeaProjects\Effective-Mobile-Test - у вас может отличаться, поэтому копируем путь до проекта и меняем.

Далее вводим:

docker-compose up --build 
и
ждём пока jar собёрется, а после этого вводим: 

docker compose up -d  

и теперь
Готово!
Теперь можешь перейти по ссылке :  http://localhost:8080/swagger-ui/index.html и посмотреть функции сервиса.
