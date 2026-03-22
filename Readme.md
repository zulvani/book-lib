# Book-Library

## Additional Information
I put the specific configuration in the member entity to override the global or default configuration. This will be useful if you want to change the default configuration for the specific member.

## How to run
1. If you are using intellij idea, just run the main class, otherwise run the jar file
2. If you want to run using command line, please use the command: `mvn spring-boot:run`
3. If the flyway migration is not working, please run the command: `mvn flyway:migrate`, if it is still not working, please run the SQL inside the  `src/main/resources/db/migration` folder
4. The default username and password is stored inside application.properties
5. The default port is 8080

## How to test
1. Run the test class
2. Run the cURL command inside the `src/test/resources/curl` folder or using Postman

## cURL with Postman example

### POST auth/login
#### Request
```aiignore
curl --location 'http://localhost:8080/auth/login' \
--header 'Content-Type: application/json' \
--header 'Cookie: JSESSIONID=3DCAF069EF2CC4599D3F2E2BDD7380C7' \
--data '{
    "username": "guest",
    "password": "guest123"
}'
```

#### Response 
```aiignore
{
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndWVzdCIsImlhdCI6MTc3NDE0Mzc4MiwiZXhwIjoxNzc0MTQ3MzgyLCJyb2xlIjoiUk9MRV9HVUVTVCJ9.uYSYAk-6Kagh8WkYLrZ6BSHCnNo4syciJIwlujxLuWk"
}
```

### POST book/loan
#### Request
```aiignore
curl --location 'http://localhost:8080/book/loan' \
--header 'Content-Type: application/json' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndWVzdCIsImlhdCI6MTc3NDE0Mzc4MiwiZXhwIjoxNzc0MTQ3MzgyLCJyb2xlIjoiUk9MRV9HVUVTVCJ9.uYSYAk-6Kagh8WkYLrZ6BSHCnNo4syciJIwlujxLuWk' \
--header 'Cookie: JSESSIONID=26C2B4E7AD966C5098769807ED7F3486' \
--data '{
    "bookId": "44f46503-ba3e-4ba9-b1a5-8d5a262682cf",
    "memberId": "eb47e997-acff-4b22-b6b2-dc322a820e8a",
    "borrowDateTime": "2026-03-21T09:21:00"
}'
```

#### Response (Success)
````aiignore
{
    "bookLoan": {
        "id": "413d4473-f0b1-41c4-b5f7-59c8d4a820c4",
        "book": {
            "id": "44f46503-ba3e-4ba9-b1a5-8d5a262682cf",
            "title": "AI Engineering",
            "author": "Chip Huyen",
            "isbn": "1098166302",
            "totalCopies": 10,
            "availableCopies": 9
        },
        "member": {
            "id": "eb47e997-acff-4b22-b6b2-dc322a820e8a",
            "name": "Agus Zulvani",
            "email": "zulvani.note@gmail.com",
            "maxActiveLoans": null,
            "allowMemberToBorrowWhenOverdueLoan": false,
            "loanDueDays": null
        },
        "borrowedAt": "2026-03-21T09:21:00",
        "dueDate": "2026-03-28T09:21:00",
        "returnedAt": null
    },
    "message": null
}
````

#### Response (Bad Request)
```aiignore
{
    "bookLoan": null,
    "message": "Member has reached the maximum loan limit"
}
```
### PUT book/loan/returned/{loan-id}
#### Request
```aiignore
curl --location --request PUT 'http://localhost:8080/book/loan/returned/e38d9e0e-594a-4f45-a8ba-8f71c84903c8' \
--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJndWVzdCIsImlhdCI6MTc3NDE0Mzc4MiwiZXhwIjoxNzc0MTQ3MzgyLCJyb2xlIjoiUk9MRV9HVUVTVCJ9.uYSYAk-6Kagh8WkYLrZ6BSHCnNo4syciJIwlujxLuWk' \
--header 'Cookie: JSESSIONID=26C2B4E7AD966C5098769807ED7F3486' \
--data ''
```

#### Response
```aiignore
{
    "bookLoan": {
        "id": "e38d9e0e-594a-4f45-a8ba-8f71c84903c8",
        "book": {
            "id": "44f46503-ba3e-4ba9-b1a5-8d5a262682cf",
            "title": "AI Engineering",
            "author": "Chip Huyen",
            "isbn": "1098166302",
            "totalCopies": 10,
            "availableCopies": 10
        },
        "member": {
            "id": "eb47e997-acff-4b22-b6b2-dc322a820e8a",
            "name": "Agus Zulvani",
            "email": "zulvani.note@gmail.com",
            "maxActiveLoans": null,
            "allowMemberToBorrowWhenOverdueLoan": false,
            "loanDueDays": null
        },
        "borrowedAt": "2026-03-21T09:21:00",
        "dueDate": "2026-03-28T09:21:00",
        "returnedAt": "2026-03-22T08:54:10.438271"
    },
    "message": null
}
```

### GET actuator/health
#### Request
```aiignore
curl --location 'http://localhost:8080/actuator/health' \
--header 'Cookie: JSESSIONID=26C2B4E7AD966C5098769807ED7F3486'
```

#### Response
```aiignore
{
    "status": "UP"
}
```

### GET /v3/api-docs
#### Response
```aiignore
{"openapi":"3.0.1","info":{"title":"OpenAPI definition","version":"v0"},"servers":[{"url":"http://localhost:8080","description":"Generated server url"}],"paths":{"/book/loan/returned/{loan-id}":{"put":{"tags":["book-loan-controller"],"operationId":"returnLoan","parameters":[{"name":"loan-id","in":"path","required":true,"schema":{"type":"string","format":"uuid"}}],"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/BookLoanResponse"}}}}}}},"/book/loan":{"post":{"tags":["book-loan-controller"],"operationId":"requestLoan","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/BookLoanRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"$ref":"#/components/schemas/BookLoanResponse"}}}}}}},"/auth/login":{"post":{"tags":["auth-controller"],"operationId":"login","requestBody":{"content":{"application/json":{"schema":{"$ref":"#/components/schemas/AuthRequest"}}},"required":true},"responses":{"200":{"description":"OK","content":{"*/*":{"schema":{"type":"object"}}}}}}}},"components":{"schemas":{"Book":{"type":"object","properties":{"id":{"type":"string","format":"uuid"},"title":{"type":"string"},"author":{"type":"string"},"isbn":{"type":"string"},"totalCopies":{"type":"integer","format":"int32"},"availableCopies":{"type":"integer","format":"int32"}}},"BookLoanResponse":{"type":"object","properties":{"bookLoan":{"$ref":"#/components/schemas/Loan"},"message":{"type":"string"}}},"Loan":{"type":"object","properties":{"id":{"type":"string","format":"uuid"},"book":{"$ref":"#/components/schemas/Book"},"member":{"$ref":"#/components/schemas/Member"},"borrowedAt":{"type":"string","format":"date-time"},"dueDate":{"type":"string","format":"date-time"},"returnedAt":{"type":"string","format":"date-time"}}},"Member":{"type":"object","properties":{"id":{"type":"string","format":"uuid"},"name":{"type":"string"},"email":{"type":"string"},"maxActiveLoans":{"type":"integer","format":"int32"},"allowMemberToBorrowWhenOverdueLoan":{"type":"boolean"},"loanDueDays":{"type":"integer","format":"int32"}}},"BookLoanRequest":{"type":"object","properties":{"bookId":{"type":"string","format":"uuid"},"memberId":{"type":"string","format":"uuid"},"borrowDateTime":{"type":"string","format":"date-time"}}},"AuthRequest":{"type":"object","properties":{"username":{"type":"string"},"password":{"type":"string"}}}}}}
```