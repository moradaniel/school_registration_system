# School registration system web-app

#### Design and implement simple school registration system
- Assuming you already have a list of students
- Assuming you already have a list of courses
- A student can register to multiple courses
- A course can have multiple students enrolled in it.
- A course has 50 students maximum
- A student can register to 5 course maximum

Provide the following REST API:
- Create students CRUD
- Create courses CRUD
- Create API for students to register to courses
- Create abilities for user to view all relationships between students and courses
+ Filter all students with a specific course
+ Filter all courses for a specific student
+ Filter all courses without any students
+ Filter all students without any courses

### Technology stack:
   + Java
   + Maven
   + Spring Boot
   + Docker (docker-compose)
   + JUnit
   + Postgres
   + OpenAPI/Swagger

### To build the project run the following commands:
  1) Run an in-memory Postgres database for running the integration tests: 
```
$docker-compose -f ./docker-compose.yml up -d dbpostgres_test
```

  2) Build and install the project. This step will execute the integration tests:
```
$./mvnw clean install

```

### To build the web app Docker image:

  3) Build the web app Docker image:

```
./mvnw install -DskipTests dockerfile:build
 ```

### To run the web app  Docker image:

  4) Run the Postgres database for the webapp:
```
$docker-compose -f ./docker-compose.yml up -d dbpostgres
```

  5) Run the webapp in a docker container:
```
$docker-compose -f ./docker-compose.yml up -d reg-registration-webapp

```


### OpenAPI/Swagger api documentation

  6) Browse the endpoints documentation:

```
http://localhost:8080/registration/swagger-ui/index.html
http://localhost:8080/registration/v3/api-docs
```

### Health check endpoints

  7) Browse the actuator health endpoint:

```
http://localhost:8080/registration/actuator/health
```

### Endpoints:

7) Try endpoints:


#### Create student

```
    curl --location --request POST 'http://localhost:8080/registration/students' \
    --header 'Content-Type: application/json' \
    --data-raw '{
        "firstName": "John",
        "lastName": "Locke",
        "studentId": "0f70b905-50cd-4539-a1b6-1006125078e8"
    }'

```

#### Find student by StudentId

```
    curl --location --request GET 'http://localhost:8080/registration/students/123e4567-e89b-42d3-a456-556642440000'

```

#### Create course

```

curl --location --request POST 'http://localhost:8080/registration/courses' \
--header 'Content-Type: application/json' \
--data-raw '{
    "name": "System Design",
    "courseId": "0e4c2a05-21b4-4034-8756-527434b9a1ae"
}'

```

#### Find course by CourseId

```
curl --location --request GET 'http://localhost:8080/registration/courses/321e4567-e89b-42d3-a456-556642440000'    

```

#### Student registers to courses

```
curl --location --request POST 'http://localhost:8080/registration/enrollments' \
--header 'Content-Type: application/json' \
--data-raw '{
"courseId": "321e4567-e89b-42d3-a456-556642440001",
"studentId": "123e4567-e89b-42d3-a456-556642440002"
}'
```

#### Filter all students with a specific course

```
curl --location --request GET 'http://localhost:8080/registration/enrollments/course/321e4567-e89b-42d3-a456-556642440000'
```

#### Filter all courses for a specific student

```
curl --location --request GET 'http://localhost:8080/registration/enrollments/student/123e4567-e89b-42d3-a456-556642440001'
```

#### Filter all courses without any students

```
curl --location --request GET 'http://localhost:8080/registration/enrollments/courses/withoutStudents'
```


#### Filter all students without any courses

```
curl --location --request GET 'http://localhost:8080/registration/enrollments/students/withoutCourses'
```
