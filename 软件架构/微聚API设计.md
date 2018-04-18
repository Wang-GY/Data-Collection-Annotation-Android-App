# API Design Document v0.1

## Table of Content

[TOC]

## Conventions

### 1. Use nouns but no verbs

| Resource | GET (read)            | POST (create)            | PATCH (update)        | DELETE            |
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
- `GET /cars/711/drivers/4` Returns driver #4 for car 711.

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
    "meta": {
        "result_set": {
            "count": 2,
            "offset": 0,
            "limit": 2,
            "total": 77
        }
    },
    "data": [
        {
        	"id": "uuid1",
            "model": "Benz"
    	},
        {
            "id": "uuid2",
            "model": "Lamborghini"
        }
    ]
}
```

**Note:** Send GET request to  `/cars?limit=0` to get a json that just contains metadata.

### 5. Versioning the API

Make the API Version mandatory and do not release an unversioned API. Use a simple ordinal number.

```http
/api/v1
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
    "errors": {
        "title": "This is a short human readable msg, MUST not be empty",
        "detail": "This is human readable detail msg, can be empty",
        "status": 404
    }
}
```

## JSON Specifications

[Specifications](http://jsonapi.org/format/)

## API
####  Template

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
- Status Code:

- Body

```Json
{
    "errors": {
        "title": "This is a short human readable msg, MUST not be empty",
        "detail": "This is human readable detail msg, can be empty",
        "status": 404
    }
}
```

### Users
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
      "data": {
          "id": "uuid",
          "email": "example@xxx.com",
          "password": "xxx"
      }
  }
  ```


**Response:**

- Status Code: 201

- Body

  ```Json
  {
      "data": {
          "id": "uuid",
          "email": "example@xxx.com",
          "password": "xxx"
      }
  }
  ```

**Errors:**

- Email exists    

- Status Code: 404  

- Body:
```JSON
{
    "errors": {
        "title": "This email has already been registered",
        "detail": "can not insert into users,violate email unique constrain",
        "status": 404
    }
}
```

####  2. Login

**Description:**

**Request:**

- URI

  ```http
  POST /api/v1/sessions
  ```


- Body

  ```Json
  {
      "data": {
          "email": "example@xxx.com",
          "password": "xxx"
      }
  }
  ```


**Response:**

- Status Code: 200

- Body

  ```Json
  {
      "data": {
          "id": "user_id",
          "token": "xxx"
      }
  }
  ```

**Errors:**

- email not registered
- Status Code: 400
- Body

```JSON

{
    "errors": {
        "title": "email not registered",
        "detail": "can not find email in users",
        "status": 400
    }
}

```

- wrong password
- Status Code: 400
- Body
```JSON
{
    "errors": {
        "title": "email password does not match",
        "detail": "email password does not match",
        "status": 400
    }
}

```
####  3. Get User Profiles

**Description:**

**Request:**

- URI

  ```http
  GET /api/v1/users/{id}
  ```


- Body

  EMPTY


**Response:**

- Status Code: 200

- Body

  ```Json
  {
      "data": {
          "id": "xxx",
          "username": "xxx",
          "email": "example@xxx.com",
          "phone": "1234567890",
          "credit": 100,
          "balance": 100,
          "level":0,
          "avatar":"URL",
          "register_date":"xxx",
          "gender":1,
          "level":0,
          "privilege":0
      }
  }
  ```

**Errors:**

- No such user

- Status Code: 404
- Body
```JSON
{
    "errors": {
        "title": "No such user",
        "detail": "No such user",
        "status": 404
    }
}
```
- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

####  4. Update User Profiles

**Description:**

- Some infomation such as email, user balance **MUST** not be updated by this way!
- **Only** the fields that appears in the JSON body should be updated!

**Request:**

- URI

  ```http
  PATCH /api/v1/users/{id}
  ```


- Body

  ```Json
  {
      "data": {
          "id": "xxx",
          "username": "xxx",
          "password": "xxx",
          "phone": 1234567890,
          "credit": 100
      }
  }
  ```


**Response:**

- Status Code: 200 OK

- Body
```Json
{
    "data": {
        "id": "xxx",
        "type": "user",
        "username": "xxx",
        "email": "example@xxx.com",
        "phone": 1234567890,
        "credit": 100,
        "balance": 100,
        "nickname":"xxx",
        "level":0,
        "avatar":"URL"
    }
}
```

**Errors:**

- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

### Pictures

####  1. Get a Picture

**Description:**

**Request:**

- URI

  ```http
  GET /api/pictures/{picture_url}
  ```


- Body

  EMPTY


**Response:**

- Status Code:  200 OK

- Body

  Binary image file

**Errors:**

- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

- File not found

- Status Code: 404

- Body

```Json
{
    "errors": {
        "title": "File not found",
        "detail": "File not found",
        "status": 404
    }
}
```

####  2. Batch Get Pictures

**Step 1**

**Request:**

- URI

  ```http
  POST /api/v1/batchasks
  ```


- Body

  ```Json
  {
      "data": [
          {
              "method": "GET",
              "url": "/api/v1/pictures/{picture_url1}"
          },
          {
              "method": "GET",
              "url": "/api/v1/pictures/{picture_url1}"
          },
      ]    
  }
  ```


**Response:**

- Status Code: 201 Created

- Body

  ```Json
  {
      "data": {
          "url": "/api/v1/batchasks/{ask_id}"
      }
  }
  ```

**Step 2**

**Request:**

- URI

  ```http
  GET http://example.com/api/batchtask/{ask_id}
  ```

- Body

  EMPTY

**Response:**

- Status Code: 200 OK

- Body

  A **zip file** that contains the resources asked by user.

**ERRORS:**
- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

- File not found

- Status Code: 404

- Body

```Json
{
    "errors": {
        "title": "File not found",
        "detail": "File not found",
        "status": 404
    }
}
```

####  3. Delete a picture

**Description:**

**Request:**

- URI

  ```http
  DELETE /api/v1/pictures/{picture_url}
  ```


- Body

  EMPTY


**Response:**

- Status Code: 204 DELETED

- Body

  EMPTY

**Errors:**

- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

- File not found

- Status Code: 404

- Body

```Json
{
    "errors": {
        "title": "File not found",
        "detail": "File not found",
        "status": 404
    }
}
```

### Tasks

#### Time Format

The time string in JSON should be like this:

```Java
import java.time.Instant;
Instant.now().toString();
```

> 2017-01-23T12:34:56.123456789Z

Recover from string

```Java
Instant instant = Instant.parse( "2017-01-23T12:34:56.123456789Z" );
```

####  1. Create a Task

**Description:**
create a collection task or annnotation task.  
TODO: XML standard (URL of pictures,task labels)

**Request:**

- URI

  ```http
  POST /api/v1/tasks
  ```


- Body

  ```Json
  {
      "data": {
          "id": "uuid",
          "formater": {
            "tags":[
                {
                  "nsme":"tag1",
                  "description":"some description about tag1",
                  "attributes":[
                    {
                      "name":"attr1",
                      "description":"some description about attr1",
                      "values":["option1","option2"]
                    }
                  ]
                }
            ]

          },
          "title": "xxx",
          "start": "xxxx",
          "end": "xxx",
          "descriptions": "xxx"
      }
  }
  ```

  formater:
  1. APP对输入的属性取值(Json中的values字段)**不做类型区分**，一律存成String。
  2. 属性的values字段设置为空表示可以任意取值。否则标注用户只能从中选择(一个或者多个)。
  3. 由于标注方式太灵活，每个标签和属性均需要提供description字段用于指导用户如何进行标注。

**Response:**

- Status Code:  201 Created

- Body

  EMPTY

**Errors:**

- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

- File not found

- Status Code: 404

- Body

```Json
{
    "errors": {
        "title": "File not found",
        "detail": "picture url not exists",
        "status": 404
    }
}
```

####  2. Get Pictures Related to a Task

The links of the pictures should be contained in the xml file of the task.
#### 3. Get tasks id multiple
Init task window.
初始化界面时可能会需要请求任务信息，后端返回任务id，需要完善。

**Request:**
- URI
```HTTP
GET /aop/v1/tasks?limit=10
```

**Response:**
- Status code: 200
- Body

```json
{
  "meta":{
  "result_set":{
    "total":100,
    "count":10,
    "next":"...&offset=10"
  }
  },
  "data":{
    "tasks":[
      {
          "id": 1,
          "name": "xxx",
          "description": "xxx",
          "start_time": "1234567890",
          "type": 0,
          "size": 100,
          "data_path":"xxx",
          "creator":0,
          "progress":20,
          "pictures":["url1","url2"],
          "formater":{}
      }
    ]

  }
}
```
pictures: 提供最多10张此任务的图片方便前端预览
pictures 不是tasks 表中的列

Also support /api/v1/tasks?task_id={task_id}
meta 如果"offset=10"中加"prev":"...&offset=0"
见 https://developer.digitalchalk.com/document/rest-api-v5/limit-and-offset/
####  4. Get task Profiles

**Description:**

**Request:**

- URI

  ```http
  GET /api/v1/tasks/task_id=1
  ```


- Body

  EMPTY


**Response:**

- Status Code: 200

- Body

  ```Json
  {   
      "data": {
          "id": 1,
          "name": "xxx",
          "description": "xxx",
          "start_time": "1234567890",
          "type": 0,
          "size": 100,
          "data_path":"xxx",
          "creator":0,
          "progress":20,
          "pictures":["url1","url2"],
          "formater":{}
      }
  }
  ```

**Errors:**

- No such task

- Status Code: 404
- Body
```JSON
{
    "errors": {
        "title": "No such task",
        "detail": "No such task",
        "status": 404
    }
}
```
- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

####  5. Update task Profiles

**Description:**

- Some infomation such as progress, labels **MUST** not be updated by this way!
- **Only** the fields that appears in the JSON body should be updated!

**Request:**

- URI

  ```http
  PATCH /api/v1/tasks/{id}
  ```


- Body

  ```Json
  {
      "data": {
          "id": "xxx",
          "name": "xxx",
          "description": "xxx",
          "size": 100
      }
  }
  ```
要检查{id}和"id"是否一致，不能更新id

**Response:**

- Status Code: 200 OK

- Body
```Json
{    
    "data": {
        "id": "xxx",
        "type": "task",
        "name": "xxx",
        "description": "xxx",
        "start_time": "1234567890",
        "type": 0,
        "size": 100,
        "data_path":"xxx",
        "creator":0,
        "progress":20,
        "formater":{}
    }
}
```

**Errors:**

- Permission denied

- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```
#### 6. Apply  a task

**Description:**
申请一个任务，后端发放一部分数据
**Request:**
- URI
```http
POST /api/tasks/apply
```
- Body
```JSON
{
  "data":{
    "taskid":1,
    "applyer":0
  }
}
```
applyer: userid


### Commits

####  ~~1. Upload a commit~~

**Description:**
Finish a subtask and upload.

**Request:**

- URI

  ```http
  POST /api/commits
  ```


- Body

  ```Json
  {
      "data": {
          "id": 0,
          "task_id": 1,
          "commiter": 1,
          "result":[
              {
                 "picture_url": "URL",
          		   "xml": "xxx"
              }
          ]
      }
  }
  ```
xxx:
```json
{
    "tags": [
        {
            "name": "tag1",
            "position": [
                [0, 0],
                [100, 100]
            ]
            "attributes": [
                {
                    "name": "attr1",
                    "values": ["option1", "option2"]
                }
            ]
        }
    ]
}
```
1. position字段指定标签的位置。
   - 框图标注：提供左上角，右下角坐标。
   - 点标注(Optional): 提供所有点坐标。
2. APP对用户的输入字段**不做检查**。将处理过程交给任务发布方或者管理员。

**Response:**

- Status Code:  201 CREATED

- Body

  EMPTY

**Errors:**
- Permission denied
- Status Code: 401
- Body
```JSON
{
    "errors": {
        "title": "Please login",
        "detail": "Permission denied",
        "status": 401
    }
}
```

####  2. Get User Commits

**Description:**

**Request:**

- URI

  ```http
  GET /api/commits?user={user_id}&task={task_id}&limit=3
  ```


- Body

  EMPTY


**Response:**

- Status Code: 200 OK

- Body

  ```Json
  {   
    "meta":{

       "result_set": {
               "count": 3,
               "offset": 0,
               "limit": 3,
               "total": 77,
               "next":"/api/commits?user={user_id}&task={task_id}&limit=3&offset=3"
           }
       },

      "data": {
        "commits":[
          {
          "id": 0,
          "task_id": 2,
          "author_id": 1,
          "result": [
              {
                	 "picture_url": "url",
          		     "xml": "url"  
              }
                  ]
          }

      ]
      }
  }
  ```
  备注：
  如果下一次请求这个offset=3,在meta中加上"prev"=".....&offset=0"

**Errors:**

- Commits not found

- Status Code:404

- Body

```Json
{
    "errors": {
        "title": "You haven't take this task",
        "detail": "can not find commits of this task",
        "status": 404
    }
}
```
