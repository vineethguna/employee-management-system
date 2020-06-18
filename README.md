# Employee Management System

This project maintains the employee data and it interacts with payroll service to store the payroll information. 
It makes sure the data is consistent across employee service and payroll service

## Supported APIS

* Create Employee API - This API adds a new employee to employee service as well as payroll service
* Search Employee API - This API is used to search the employees based on name, age or both

Swagger is integrated into this project you can test and play with the APIs at http://localhost:8080/swagger-ui.html

#### Curl Request for the APIS

Create Employee API
```
 curl --location --request POST 'http://localhost:8080/api/v1/employee/create' \
 --header 'content_type: application/json' \
 --header 'Content-Type: application/json' \
 --data-raw '{
 	"firstName": "Vineeth",
 	"lastName": "Guna",
 	"age": 24,
 	"salary": 1200000
 }'
```

Search Employee API

```
curl --location --request GET 'http://localhost:8080/api/v1/employee/search?name=vineeth&age=27' \
--header 'content_type: application/json'

curl --location --request GET 'http://localhost:8080/api/v1/employee/search?name=vineeth' \
--header 'content_type: application/json'

curl --location --request GET 'http://localhost:8080/api/v1/employee/search?age=27' \
--header 'content_type: application/json'
```

## Prerequisites
* Docker
* Java 8
* Maven

## Service Installation and startup

* Clone this git repo
* Start docker
* Run `sh build.sh` which creates a docker container
* Run the created docker container by `docker run -p 8080:8080 vineeth/ems`

## Running unit tests

Running unit tests is integrated with maven, you can use the below command to run unit tests

`mvn clean test`

## Create Employee Design

* Once the create employee request is received, the service performs validation
* After validation, the service generates a unique username atomically
* Using the above username, employee entry is created in the database with status as CREATING
* Before calling payroll service the status of the employee entry is transitioned to PAYROLL_PENDING
* System calls payroll service with the username and waits for the response
* If the response is successful, we change the employee entry status to CREATED

If any rows in the employee table have status CREATING, PAYROLL_PENDING for more than 5 minutes these entries
are considered as stale entries

In order to clear the stale entries and maintain consistency across both the services
there is a ConsistencyChecker which runs periodically and removes these 
stale entries from both the services

## Search Employee Design
* If the search request contains only name, we search for all the employee entries
with the given name pattern and with status as CREATED
* If it contains only age we search for all the employee entries with
the given age and with status as CREATED
* If it contains both name and age we search for all the employee entries
with both criteria satisfied and with status as CREATED

## Bulk Employee Creation Design
* To enable bulk employee creation we need an API on employee service which creates
a task which is processed asynchronously, this API returns a task id
to track the progress of the task
* We also need an API enhancement on payroll service where it could add
a payrolls for a batch of employee entries atomically

#### High Level Design

* Once a task for bulk employee creation is submitted, we generate a 
unique taskid and store it in a task table in our database with task status as IN_PROGRESS
* We filter all the employee creation requests submitted using the defined validations
* We create a batch of employee creation requests and assign a batch id for each of them 
* All these batches are submitted to a message queue like AWS SQS or Kafka
* Employee Service servers also act as consumers on the message queue and
process these batches parallely to speed up the process
* We ensure processing at a batch level is always atomic, and if any 
batch fails we can retry it without issues
* We update the status of each batch in the task database row associated with the task id
* Client can call the task status API to check the bulk employee creation status

## Monitoring
* Used spring boot actuator to expose metrics of the running service
* Integrated actuator metrics with spring admin to display dashboards - http://localhost:8080 

## Built using
* Spring boot
* H2
* Hibernate
* JUnit
* Spring Admin
* Swagger
* Log4j2
* Lombok