# Kogito Travel Agency


## Description

**Kogito Travel Agency** and **Kogito Visas** communicate completely via events. First **Kogito Travel Agency** sends visa applications 
for those travellers thar require visas to visit given country, next based on visa application evaluation, **Kogito Visas**  sends back 
the response with the visa processing outcome - approved or rejected.
Adding security to **Kogito Travel Agency** and data-index service  connection using Open Id Connection security provider.


## Activities to perform

* Create project using Quarkus Maven plugin with following extensions
	* Kogito
	* OpenApi
* Import project into Eclipse IDE - requires BPMN modeller plugin installed
* Create data model
	* Traveller
	* Hotel
	* Flight
	* Address
	* Trip
	* VisaApplication
* Create service classes
	* HotelBookingService
	* FlightBookingService
* Create decision logic
	* Visa check
* Create business logic
	* Public business process to deal with complete travel request
	* Private business process to deal with hotel booking
	* Private business process to deal with flight booking
* Create a test case that makes use of processes and decisions
* Configure messaging and events
* Create or import UI components using **Kogito Data Index Service** with security enabled
* Add metrics support for processes and decisions
* Create dashboard based on metrics
* Add security check to different rest endpoints
* Create frontend that performs login in keycloak and generate the authenticated connections with
 the the different kogito-travel-agency-service rest endpoints   
   


## Data model

Kogito Travel Agency booking system will be based on following data model

**Traveller**

A person who requests a new travel

**Trip**

Place/Location where the traveller wants to go and dates

**Flight**

Flight that has been booked for the traveller to take him/her to the destination

**Hotel**

Place/Location where the traveller will stay during his/her travel

**Address**

Location that is associated with either traveller or hotel

**Visa Application**

Details requires to apply for visa to travel to particular country

<p align="center"><img width=75% height=75% src="docs/images/datamodel.png"></p>


## Decision logic

The decision logic will be implemented as a decision table. The logic will be responsible for verifying whether a given traveller requires a visa to enter a given country or not. The decision logic reason over the following data/facts

* Destination that the traveller wants to go - country
* Nationality of the traveller
* Length of the stay

The result will be “yes” or “no”.

<p align="center"><img width=75% height=50% src="docs/images/decisiontable.png"></p>


## Business logic

Business logic will be based on business processes

Public process that will be responsible for orchestrating complete travel request

<p align="center"><img width=75% height=50% src="docs/images/travels-process.png"></p>

Private process that will be responsible for booking a hotel.

<p align="center"><img width=75% height=50% src="docs/images/book-hotel-process.png"></p>

Private process that will be responsible for booking a flight.

<p align="center"><img width=75% height=50% src="docs/images/book-flight-process.png"></p>

## Services

There will be services implemented to carry on the hotel and flight booking. Implementation will be a CDI beans that will have hard coded logic to return a booked flight or hotel.

* org.acme.travels.service.HotelBookingService
* org.acme.travels.service.FlightBookingService



# Try out the complete service

## Installing and Running

### Prerequisites

You will need:
  - Java 1.8.0+ installed
  - Environment variable JAVA_HOME set accordingly
  - Maven 3.5.4+ installed
  - Docker

To try the kogito-travel-service using command line passing the security token;  
  - [jq](https://stedolan.github.io/jq) tool installed. You can download it from [here](https://stedolan.github.io/jq/download)
 

When using native image compilation, you will also need:
  - GraalVM installed
  - Environment variable GRAALVM_HOME set accordingly
  - Note that GraalVM native image compilation typically requires other packages (glibc-devel, zlib-devel and gcc) to be installed too, please refer to GraalVM installation documentation for more details.

### Architecture

The different component will interact following this architecture:

<p align="center"><img width=75% height=75% src="docs/images/kogito-travel-agency_auth.png"></p>


### Infrastructure requirements 

#### Infinispan

This application requires an Infinispan server to be available and by default expects it to be on default port and localhost.

You can install Infinispan server by downloading it from [official website](https://infinispan.org/download) version to be used in 10.0.0.CR1
Here  [https://github.com/kiegroup/kogito-runtimes/wiki/Persistence](https://github.com/kiegroup/kogito-runtimes/wiki/Persistence) the required 
Infinispan configuration is explained in more detail.

Alternatively, you can use the Docker Compose template, instructions on how to use it are available in the [README](../docker-compose/README.md) file.

#### Apache Kafka

This application requires a [Apache Kafka](https://kafka.apache.org/) installed and following topics created

* `visaapplications` - used to send visa application that are consumed and processed by Kogito Visas service
* `visasapproved` - used to send visa applications that were approved
* `visasrejected` - used to send visa applications that were rejected
* `kogito-processinstances-events` - used to emit events by kogito that can be consumed by data index service and other services
* `kogito-usertaskinstances-events` -used to emit events by kogito that can be consumed by data index service

Alternatively, you can use the Docker Compose template, instructions on how to use it are available in the [README](../docker-compose/README.md) file.


#### Starting and Configuring the Keycloak Server

To start a Keycloak Server you can use Docker and just run the following command:

```
docker run -e KEYCLOAK_USER=admin -e KEYCLOAK_PASSWORD=admin  -e KEYCLOAK_IMPORT=/tmp/kogito-realm.json -v <kogito-travel-agency-tutorial_absolute_path>/06-kogito-travel-agency-auth/config/kogito-realm.json:/tmp/kogito-realm.json -p 8280:8080  jboss/keycloak
```

You should be able to access your Keycloak Server at [localhost:8280/auth](http://localhost:8280).
and verify keycloak server is running properly: log in as the admin user to access the Keycloak Administration Console. 
Username should be admin and password admin.

#### Start Kogito Data Index Service with security enabled

It can be downloaded from [Kogito Dada Index Service 0.6.0](http://repo2.maven.org/maven2/org/kie/kogito/data-index-service/0.6.0/data-index-service-0.6.0-runner.jar)

NOTE: Here can be found other versions [Kogito Dada Index Service versions](http://repo2.maven.org/maven2/org/kie/kogito/data-index-service)

After downloading the runner, create a new folder to store the .proto files that will be used by the service. 

This service works with .proto files that define the data model. Once **Kogito Travel Service** is started, /target/classes/persistence/travels.proto is generated and it has to be copied to the new proto files folder.

To start the **Kogito Data Index Service** with security enabled just past the full path of the proto files folder and execute  

```
java -jar  -Dkogito.protobuf.folder={full path to proto files folder} data-index-service-0.6.0-runner.jar -Dquarkus.profile=keycloak
```

NOTE: If we want to run 'Kogito Travels Service' and 'Kogito Visa Service' using the same Kogito Data Index Service, we will copy both files travels.proto and visaApplications.proto at the same 'kogito.protobuf.folder' that is  passed as parameter, and will start the data index service once.

### Kogito travel agency services:

To run the kogito-travel-agency with security enabled two apps are provided with different [quarkus.oidc.application-type](https://quarkus.io/guides/security-openid-connect#quarkus-oidc_quarkus.oidc.application-type) configuration:
- The kogito-travel-agency-service app configured as 'service'. 
- The kogito-travel-agency-frontend that supports 'web-app' application type and performs keycloak authentication via web 
login relying on keycloak, showing a login page when the user is not authenticated and redirecting to app after authentication


#### Compile and Run kogito-travel-agency-service 
In `kogito-travel-agency-service` directory, the service can be ran in different ways:

##### in Local Dev Mode

```
mvn clean package quarkus:dev    
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules and decision
tables and java code. No need to redeploy or restart your running application.During this workshop we will create a software system for a startup travel agency called Kogito Travel Agency. The first iteration of the system will consist of a set of services that are able to deal with travel requests and the booking of hotels and flights.


##### Compile and Run using Local Native Image

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/kogito-travel-agency-service-{version}-runner
```

#### Compile and Run kogito-travel-agency-frontend 

In `kogito-travel-agency-frontend` directory:

##### in Local Dev Mode

```
mvn clean package quarkus:dev    
```

NOTE: With dev mode of Quarkus you can take advantage of hot reload for business assets like processes, rules and decision
tables and java code. No need to redeploy or restart your running application.During this workshop we will create a software system for a startup travel agency called Kogito Travel Agency. The first iteration of the system will consist of a set of services that are able to deal with travel requests and the booking of hotels and flights.


##### Compile and Run using Local Native Image

```
mvn clean package -Pnative
```

To run the generated native executable, generated in `target/`, execute

```
./target/kogito-travel-agency-frontend-{version}-runner
```
  

## Known issues


## User interface frontend service

The frontend is configured to only work with authenticated users. Than means that when someone tries to access to http://localhost:8082
The app will show a keycloak login page. After login (joe/joe) the page that allow make request to the different kogito-travel-agency 
rest endpoints.

NOTE: The data-index service by default is configured to allow only users with the role confidential. It's because of that, the user alice,
won't be able to retrieve any data.

The UI allows:

### plan new trips

<p align="center"><img width=75% height=75% src="docs/images/new-trip.png"></p>

### list currently opened travel requests

<p align="center"><img width=75% height=75% src="docs/images/list-trips.png"></p>

### show details of selected travel request

<p align="center"><img width=75% height=75% src="docs/images/trip-details.png"></p>

### show active tasks of selected travel request

<p align="center"><img width=75% height=75% src="docs/images/tasks.png"></p>

### perform Human task: visa application

<p align="center"><img width=75% height=75% src="docs/images/visa-application.png"></p>

### cancel selected travel request

To start Kogito Travel Agency UI just point your browser to [http://localhost:8082](http://localhost:8082)


## REST API

Once the service is up and running, you can use the following examples to interact with the Kogito Travel Agency Service.

The application is using bearer token authorization and the first thing to do is obtain an access token from the Keycloak 
Server in order to access the application resources. Obtain an access token for user jdoe that have the 'confidential' 
role required by data-index security configuration.

```
export access_token=$(\                                                           
    curl -X POST http://localhost:8280/auth/realms/kogito/protocol/openid-connect/token \
    --user kogito-travel-agency-service:secret \
    -H 'content-type: application/x-www-form-urlencoded' \
    -d 'username=jdoe&password=jdoe&grant_type=password' | jq --raw-output '.access_token' \
)
```

To try the different end points the app is expecting this access token at the authorization header

### POST /travels

Send travel that requires does not require visa

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -X POST http://localhost:8082/travels -H "Authorization: Bearer "$access_token -d @- << EOF
{
	"traveller" : {
		"firstName" : "John",
		"lastName" : "Doe",
		"email" : "john.doe@example.com",
		"nationality" : "American",
		"address" : {
			"street" : "main street",
			"city" : "Boston",
			"zipCode" : "10005",
			"country" : "US"
		}
	},
	"trip" : {
		"city" : "New York",
		"country" : "US",
		"begin" : "2019-12-10T00:00:00.000+02:00",
		"end" : "2019-12-15T00:00:00.000+02:00"
	}
}
EOF

```

This will directly go to 'ConfirmTravel' user task.

Send travel request that requires does require visa

```sh
curl -X POST http://localhost:8082/travels -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Bearer "$access_token -d @- << EOF
{
	"traveller" : {
		"firstName" : "Jan",
		"lastName" : "Kowalski",
		"email" : "jan.kowalski@example.com",
		"nationality" : "Polish",
		"address" : {
			"street" : "polna",
			"city" : "Krakow",
			"zipCode" : "32000",
			"country" : "Poland"
		}
	},
	"trip" : {
		"city" : "New York",
		"country" : "US",
		"begin" : "2019-12-10T00:00:00.000+02:00",
		"end" : "2019-12-15T00:00:00.000+02:00"
	}
}
EOF
```

This will stop at 'VisaApplication' user task.

### GET /travels

Returns list of travel requests currently active:

```sh
curl -X GET http://localhost:8082/travels -H "Authorization: Bearer "$access_token
```

As response an array of travels is returned.

### GET /travels/{id}

Returns travel request with given id (if active):

```sh
curl -X GET http://localhost:8082/travels/{uuid} -H "Authorization: Bearer "$access_token
```

As response a single travel request is returned if found, otherwise no content (204) is returned.

### DELETE /travels/{id}

Cancels travel request with given id

```sh
curl -X DELETE http://localhost:8082/travels/{uuid} -H "Authorization: Bearer "$access_token
```

### GET /travels/{id}/tasks

Returns currently assigned user tasks for give travel request:

```sh
curl -X GET http://localhost:8082/travels/{uuid}/tasks -H "Authorization: Bearer "$access_token
```

### GET /travels/{id}/VisaApplication/{taskId}

Returns visa application task information:

```sh
curl -X GET http://localhost:8082/travels/{uuid}/VisaApplication/{task-uuid} -H "Authorization: Bearer "$access_token
```

### POST /travels/{id}/VisaApplication/{taskId}

Completes visa application task

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Bearer "$access_token -X POST http://localhost:8082/travels/{uuid}/VisaApplication/{task-uuid} -d @- << EOF
{
	"visaApplication" : {
		"firstName" : "Jan",
		"lastName" : "Kowalski",
		"nationality" : "Polish",
		"city" : "New York",
		"country" : "US",
		"passportNumber" : "ABC09876",
		"duration" : 25
	}
}
EOF
```

### GET /travels/{id}/ConfirmTravel/{taskId}

Returns travel (hotel, flight) task information required for confirmation:

```sh
curl -X GET http://localhost:8082/travels/{uuid}/ConfirmTravel/{task-uuid} -H "Authorization: Bearer "$access_token
```

### POST /travels/{id}/ConfirmTravel/{taskId}

Completes confirms travel task - meaning confirms (and completes) the travel request

```sh
curl -H "Content-Type: application/json" -H "Accept: application/json" -H "Authorization: Bearer "$access_token-X POST http://localhost:8082/travels/{uuid}/ConfirmTravel/{task-uuid} -d '{}'
```

### Querying the technical cache

NOTE: When running **Kogito Data Index Service** on dev mode with auth enabled, the GraphiQL UI is currently not enabled, 
regarding is configured as a service.(This can be changed in future releases)
