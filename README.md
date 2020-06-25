#LibMan
Library Manager - helps with reservations and borrowing books.

## 1.General
In system there are three main user roles
1. User
2. Librarian
3. Administrator

Each role has the same permissions as role above them - so, librarian is also regular user and admin is also librarian and regular user.

**User**

User can search thorough books and select some of them for reservation.  
User can reserve max.3 books. If he wants more he should borrow the ones that are reserved already.  
User also can view his borrowed and reserved books - tab "My books".  
User can edit his information, for example change phone number, city, etc.

![alt text](https://github.com/wiokru/libman/blob/master/images/user_view.png)

**Librarian**

Librarian can do the same things as user, but moreover he has "Manage Books" tab.
It allows him to add new book, add new author, add new book category, mark books as borrowed (accept reservations), reject reservations and mark books as returned.
He also can edit and delete existing books.

![alt text](https://github.com/wiokru/libman/blob/master/images/librarian_view.png)


**Administrator**

Administrator has the same permissions as librarian and user. He also has ability to delete and edit exising users. For example he can add new roles to users.

![alt text](https://github.com/wiokru/libman/blob/master/images/admin_view.png)

## What was used and how to run
1. Java 11
2. Maven 3.6.3
3. PostreSQL ver. 12.3
4. Docker - to hold local database
5. Spring boot
6. Hibernate
6. Thymeleaf

**Running project**

1. Clone the project
```
git clone https://github.com/wiokru/libman.git
```
2. Create the database

I used docker to create and run local database. You can also use local postgreSQL server. 
To set up database on docker use following commands (make sure docker service is running).
```
docker volume create pgdata
docker run --name postgres -e POSTGRES_PASSWORD=<your-desired-password> -d -p 54320:5432 -v pgdata:/var/lib/postgresql/data postgres:12.3
docker exec -i postgres psql -U postgres -c "CREATE DATABASE library_umcs WITH ENCODING='UTF8' OWNER=postgres;"
```
   2.1. Don't forget to change username and password in application.properties:		
   Open `src/main/resources/application.properties` and change `spring.datasource.username`
   and `spring.datasource.password` for the ones you used. Check if `spring.datasource.url` matches your url.
   
3. Build and run the app using maven
```
mvn clean package
java -jar target/libman-0.0.1-SNAPSHOT.jar
```
You can also run the app without packaging it. Use:
```
mvn spring-boot:run
```
You can also run the app directly from the IDE you're using.

The app will start running at http://localhost:8080.

After running the app you will see the login page.
![alt text](https://github.com/wiokru/libman/blob/master/images/login.png)

From there you can register a new account - click on "Sign up now" and you will see sign up page
![alt text](https://github.com/wiokru/libman/blob/master/images/signup.png)


## Use case diagram
There are use cases for each role. Please keep in mind that permissions are inherited (as described erlier), so even tho there is no use case diagram for book reservation by admin he still can do this, cause he is also a user.
![alt text](https://github.com/wiokru/libman/blob/master/images/usecase1.jpg)

