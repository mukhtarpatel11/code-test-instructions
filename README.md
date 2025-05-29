# Simple URL Shortener

## Run application locally

- Install docker
- clone repository
- Launch a terminal, from the root of the project _run docker-compose up --build_

## To create a new short url using UI

- On a browser navigate to http://localhost:8080/
- Enter a fully qualified url, alias is optional
- Select Shorten to generate a short url
- The new url will be displayed at the bottom, click on it to navigate.

## Using API

There are several options when it comes to invoking REST API, for this example we use curl

- To list existing shortened endpoints

  _curl --location --request GET 'localhost:8080/urls'_


- To shorten url with alias

  _curl --location --request POST 'localhost:8080/shorten' \
--header 'Content-Type: application/json' \
--data-raw '{
    "fullUrl" : "http://www.itv.co.uk",
    "customAlias" : "i"
}'_


- To shorten url without alias

  curl --location --request POST 'localhost:8080/shorten' \
_--header 'Content-Type: application/json' \
--data-raw '{
    "fullUrl" : "http://www.itv.co.uk"
}'_


- To redirect to a shortened url supply the alias as a path parameter via GET call. Alias can be obtained by invoking the urls endpoint

  curl --location --request GET 'localhost:8080/47SDa'


- To delete a existing shortened url supply the alias as a path parameter via DELETE call. Alias can be obtained by invoking the urls endpoint

  curl --location --request DELETE 'localhost:8080/47SDa'

## Assumptions

- Basic client side URL validation is performed.
- For redirects a 302 response code is used, which as per Open API spec will handle redirects.
- Does not implement race condition handling
- Does not implement security, it is assumed the application will only run locally.
- For the sake of this example an embedded H2 database is used