# Adobe PDF Extract API Demo

## Description
This application represents correspondence management system using [Adobe PDF Extract API](https://developer.adobe.com/document-services/apis/pdf-extract/).

Please refer [this movie](https://youtu.be/VHTryikm2-Y) for this app.

This app consists of two parts, frontend and backend.
Frontend is React UI app and backend is a spring boot app.

## Requirement

Frontend: Node.js is required. And this app uses Google Cloud Translation API. Please set your google api key in google-api-key.js.

Backend: Adobe Acrobat Services API credential is required to run this sample. API credential is availabe from the above url. Backend contains placeholder of pdfservices-api-credentials.json and private.key. Please replace them after getting your credential.


## Build
### Backend
~~~
cd backend
mvn package
~~~

### Frontend
~~~
cd frontend
npm install
~~~

## Run
Run backend first.
### Backend
~~~
cd backend
mvn spring-boot:run
~~~
### Frontend
~~~
cd frontend
npm start
~~~

Access to http://localhost:3000 to open the application. Sample file is in backend/samples.