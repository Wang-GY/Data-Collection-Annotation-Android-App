# API Design Document

[TOC]

## Conventions

### 1. Use nouns but no verbs

| Resource | GET (read)            | POST (create)            | PUT (update)        | DELETE            |
| -------- | --------------------- | ------------------------ | ------------------- | ----------------- |
| /cars    | Return a list of cars | Create a new car         | Bulk update of cars | Delete all cars   |
| /cars/1  | Return car id = 1     | Method not allowed (405) | Update car id = 1   | Delete car id = 1 |

Do not use verbs:

**Bad Examples!**

```http
/getAllCars
/createNewCar
/deleteAllCar
```

### 2. Use plural nouns

```http
/cars	YES
/car 	NO!!!!
```

### 3. Use sub-resources for relations

- `GET /cars/711/drivers/`  Returns a list of drivers for car 711.
- `GET /cars/711/drivers/4` Returns driver #4 for car 711

### 4. Provide filtering, sorting and paging for collections 

**Filtering:**

- `GET /cars?color=red` Returns a list of red cars.
- `GET /cars?seats<=2` Returns a list of cars with a maximum of 2 seats

**Sorting:**

Allow ascending and descending sorting over multiple fields.

```http
GET /cars?sort=-manufactorer,+model
```

 Returns a list of cars sorted by descending manufacturers and ascending models.

**Paging:**

**Do not allow user to retrieve all the data in one request by default**. Instead, GET operations, which return a list of requested items, return only the first 25(or another value) items.

| URL                     | Description                     |
| ----------------------- | ------------------------------- |
| /cars                   | Return the first 25 cars        |
| /cars?limit=10          | Return the first 10 cars        |
| /cars?offset=5          | Return cars from No.6 to No. 31 |
| /cars?offset=10&limit=5 | Return cars from No.6 to No. 10 |

To page through all available items, use the metadata section of the JSON response to get total number of items. For example, `"total": 77` means there are 77 cars in the database.

```JSON
{
    "metadata": {
        "result_set": {
            "count": 25,
            "offset": 0,
            "limit": 25,
            "total": 77 
        }
    }
}
```

**Note:** Send GET request to  `/cars?limit=0` to get a json that just contains metadata.

### 5. Version the API

Make the API Version mandatory and do not release an unversioned API. Use a simple ordinal number.

```http
/app/api/v1
```

### 6. Handle Errors with HTTP status codes

**Useful status codes**

- 200 – OK – Eyerything is working
- 201 – OK – New resource has been **created**
- 204 – OK – The resource was successfully **deleted**
- 304 – **Not Modified** – The client can use cached data
- 401 – **Unauthorized** – The request requires an user authentication
- 403 – **Forbidden** – The server understood the request, but is refusing it or the access is not allowed.
- 404 – **Not found** – There is no resource behind the URI.
- 500 – **Internal Server Error** –  You should avoid this error. If an error occurs in the global catch blog, the stracktrace should be logged and not returned as response.
- 400 – **Bad Request** – The request was invalid or cannot be served. **The exact error should be explained in the error payload.** 

**Error payloads**

All exceptions should be mapped in an error payload. Here is an example how a JSON payload should look like.

```Json
{
    "errors": [{
        "userMessage": "Sorry, the requested resource does not exist",
        "internalMessage": "No car which id = 711 is found in the database",
        "code": 404
    }]
}
```

## API

### User
####  0. Template

**Description:**

**Request:**

- URI

  ```http
  
  ```


- Body

  ```Json
  {
      
  }
  ```


**Response:**

- Status Code: 

- Body

  ```Json
  {
      
  }
  ```

**Errors:**


#### 1. Registration

**Description:**

**Request:**

- URI

  ```http
  POST /api/v1/users/
  ```


- Body

  ```Json
  {
      "email": "example@xxx.com",
      "password": "123456"
  }
  ```


**Response:**

- Status Code: 201

- Body

  ```Json
  {
      "meta": {
      },
      "data": {
          "uri": "/api/v1/users/id/22"
      }
  }
  ```

**Errors:**

- Username exists

### Task

