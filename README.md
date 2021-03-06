IPFS-Store
======

**IPFS-Store** aim is to provide an easy to use IPFS storage service with search capability for your project.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. 

### Prerequisites

- Java 8
- Maven 
- Docker (optional)


### Build

1. After checking out the code, navigate to the root directory
```
$ cd /path/to/ipfs-store/ipfs-store-service/
```

2. Compile, test and package the project
```
$ mvn clean package
```

3. Run the project

a. If you have a running instance of IPFS and ElasticSearch 

**Executable JAR:**

```
$ export IPFS_HOST=localhost
$ export IPFS_PORT=5001
$ export ELASTIC_HOST=localhost
$ export ELASTIC_PORT=9300
$ export ELASTIC_CLUSTERNAME=elasticsearch

$ java -jar target/ipfs-store.jar
```

**Docker:**

```
$ docker build  . -t kauri/ipfs-store:latest

$ export IPFS_HOST=localhost
$ export IPFS_PORT=5001
$ export ELASTIC_HOST=localhost
$ export ELASTIC_PORT=9300
$ export ELASTIC_CLUSTERNAME=elasticsearch

$ docker run -p 8040:8040 kauri/ipfs-store
```

b. If you prefer build all-in-one with docker-compose
```
$ docker-compose -f docker-compose.yml build
$ docker-compose -f docker-compose.yml up
```




## API Documentation



### Overview

| Operation | Description | Method | URI |
| -------- | -------- | -------- | -------- |
| store | Store content into IPFS |POST | /ipfs-store/store |
| index | Index content |POST | /ipfs-store/index |
| store_index | Store & Index content | POST | /ipfs-store/store_index |
| fetch | Get content | GET | /ipfs-store/fetch/{index}/{hash} |
| search | Search content | POST | /ipfs-store/search/{index} |
| search | Search content | GET | /ipfs-store/search/{index} |

### Details

#### Store content

Store a content (any type) in IPFS 

-   **URL:** `/ipfs-store/store`    
-   **Method:** `POST`
-   **Header:** `N/A`
-   **URL Params:** `N/A`
-   **Data Params:** 
    -   `file: [content]`

-   **Sample Request:**
```
$ curl -X POST \
    'http://localhost:8040/ipfs-store/store' \
    -F 'file=@/home/gjeanmart/hello.pdf'
```

-   **Success Response:**
    -   **Code:** 200  
        **Content:** 
```
{
    "hash": "QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o"
}
```

---------------------------

#### Index content

Index IPFS content into the search engine

-   **URL** `/ipfs-store/index`
-   **Method:** `POST`
-   **Header:**  

| Key | Value | 
| -------- | -------- |
| content-type | application/json |


-   **URL Params** `N/A`
-   **Data Params**

    - `request:`
    
| Name | Type | Mandatory | Default | Description |
| -------- | -------- | -------- | -------- | -------- |
| index | String | yes |  | Index name |
| id | String | no |  | Identifier of the document in the index. id null, autogenerated |
| content_type | String | no |  | Content type (MIMETYPE) |
| hash | String | yes |  | IPFS Hash of the content |
| index_fields | Key/Value[] | no |  | Key/value map presenting IPFS content metadata|


```
{
  "index": "documents", 
  "id": "hello_doc",
  "content_type": "application/pdf",
  "hash": "QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o",
  "index_fields": [
    {
      "name": "title",
      "value": "Hello Doc"
    }, 
    {
      "name": "author",
      "value": "Gregoire Jeanmart"
    }, 
    {
      "name": "votes",
      "value": 10
    }, 
    {
      "name": "date_created",
      "value": 1518700549
    }
  ]
}
```
   
-   **Sample Request:**
    
```
curl -X POST \
    'http://localhost:8040/ipfs-store/index' \
    -H 'content-type: application/json' \  
    -d '{"index":"documents","id":"hello_doc","content_type":"application/pdf","hash":"QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o","index_fields":[{"name":"title","value":"Hello Doc"},{"name":"author","value":"Gregoire Jeanmart"},{"name":"votes","value":10},{"name":"date_created","value":1518700549}]}'
``` 
   
-   **Success Response:**
    
    -   **Code:** 200  
        **Content:** 
```
{
    "index": "documents",
    "id": "hello_doc",
    "hash": "QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o"
}
```

---------------------------

#### Store & Index content

Store content in IPFS and index it into the search engine

-   **URL** `/ipfs-store/store_index`
-   **Method:** `POST`
-   **Header:** `N/A`
-   **URL Params** `N/A`
-   **Data Params**

    
    -   `file: [content]`
    -   `request: `

| Name | Type | Mandatory | Default | Description |
| -------- | -------- | -------- | -------- | -------- |
| index | String | yes |  | Index name |
| id | String | no |  | Identifier of the document in the index. id null, autogenerated |
| content_type | String | no |  | Content type (MIMETYPE) |
| index_fields | Key/Value[] | no |  | Key/value map presenting IPFS content metadata|


```
{
  "index": "documents", 
  "id": "hello_doc",
  "content_type": "application/pdf",
  "index_fields": [
    {
      "name": "title",
      "value": "Hello Doc"
    }, 
    {
      "name": "author",
      "value": "Gregoire Jeanmart"
    }, 
    {
      "name": "votes",
      "value": 10
    }, 
    {
      "name": "date_created",
      "value": 1518700549
    }
  ]
}
```
   
-   **Sample Request:**
    
```
curl -X POST \
  http://localhost:8040/ipfs-store/store_index \
  -H 'content-type: multipart/form-data; boundary=----WebKitFormBoundary7MA4YWxkTrZu0gW' \
  -F file=@/home/gjeanmart/hello.pdf \
  -F 'request={"index":"documents","id":"hello_doc","content_type":"application/pdf","index_fields":[{"name":"title","value":"Hello Doc"},{"name":"author","value":"Gregoire Jeanmart"},{"name":"votes","value":10},{"name":"date_created","value":1518700549}]}'
``` 
   
-   **Success Response:**
    
    -   **Code:** 200  
        **Content:** 
```
{
    "index": "documents",
    "id": "hello_doc",
    "hash": "QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o"
}
```

---------------------------

#### Get content

Get content on IPFS by hash

-   **URL** `http://localhost:8040/ipfs-store/fetch/{index}/{hash}`
-   **Method:** `GET`
-   **Header:**  `N/A`
-   **URL Params** `N/A`
    
-   **Sample Request:**
    
```
$ curl \
    'http://localhost:8040/ipfs-store/fetch/documents/QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o' \
    -o hello_doc.pdf 
``` 
    
-   **Success Response:**
    
    -   **Code:** 200  
        **Content:** (file)

---------------------------

#### Search contents

Search content accross an index using a dedicated query language

-   **URL** `http://localhost:8040/ipfs-store/search/{index}`
-   **Method:** `GET` or `POST` 
-   **Header:**  

| Key | Value | 
| -------- | -------- |
| content-type | application/json |

-   **URL Params** 

| Name | Type | Mandatory | Default | Description |
| -------- | -------- | -------- | -------- | -------- |
| pageNo | Int | no | 0 | Page Number |
| pageSize | Int | no | 20 | Page Size / Limit |
| sort | String | no |  | Sorting attribute |
| dir | ASC/DESC | no | ASC | Sorting direction |
| query | String | no |  | Query URL encoded (for GET call) |


-   **Data Params** 

The `search` operation allows to run a multi-criteria search against an index. The body combines a list of filters : 

| Name | Type | Description |
| -------- | -------- | -------- | 
| name | String | Index field to perform the search | 
| names | String[] | Index fields to perform the search |
| operation | See below | Operation to run against the index field | 
| value | Any | Value to compare with |



| Operation | Description |
| -------- | -------- |
| full_text | Full text search |
| equals | Equals |
| not_equals | Not equals | 
| contains | Contains the word/phrase | 
| in | in the following list | 
| gt | Greater than | 
| gte | Greater than or Equals | 
| lt | Less than  | 
| lte | Less than or Equals | 


```
{
  "query": [
    {
      "name": "title",
      "operation": "contains",
      "value": "Hello"
    },
    {
      "names": ["author", "title"],
      "operation": "full_text",
      "value": "Gregoire"
    },
    {
      "name": "votes",
      "operation": "lt",
      "value": "5"
    }
  ]
}
```

-   **Sample Request:**
    
```
curl -X POST \
    'http://localhost:8040/ipfs-store/search/documents' \
    -H 'content-type: application/json' \  
    -d '{"query":[{"name":"title","operation":"contains","value":"Hello"},{"name":"author","operation":"equals","value":"Gregoire Jeanmart"},{"name":"votes","operation":"lt","value":"5"}]}'
``` 
   
    
```
curl -X GET \
  'http://localhost:8040/ipfs-store/search/documents?page=1&size=2&query=%7B%22query%22%3A%5B%7B%22name%22%3A%22votes%22%2C%22operation%22%3A%22lt%22%2C%22value%22%3A%225%22%7D%5D%7D' \
  -H 'Accept: application/json' \
  -H 'Content-Type: application/json' \
``` 

-   **Success Response:**
    
    -   **Code:** 200  
        **Content:** 
        
```
{
  "content": [
    {
      "index": "documents",
      "id": "hello_doc",
      "hash": "QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o",
      "content_type": "application/pdf",
      "index_fields": [
        {
          "name": "__content_type",
          "value": "application/pdf"
        },
        {
          "name": "__hash",
          "value": "QmWPCRv8jBfr9sDjKuB5sxpVzXhMycZzwqxifrZZdQ6K9o"
        },
        {
          "name": "title",
          "value": "Hello Doc"
        },
        {
          "name": "author",
          "value": "Gregoire Jeanmart"
        },
        {
          "name": "votes",
          "value": 10
        },
        {
          "name": "date_created",
          "value": 1518700549
        }
      ]
    }
  ]
}
],
"sort": null,
"firstPage": false,
"totalElements": 4,
"lastPage": true,
"totalPages": 1,
"numberOfElements": 4,
"size": 20,
"number": 1
}
```





## Advanced Configuration

The following section shows how to tweak IPFS-store. Any of these properties can be overwritten using command arguments ``--{property_name}={property_value}`` (for example `--server.port=8888`)


**Full configuration file:**

```
server:
  port: 8040

logging:
  level:
    net.consensys: ${LOG_LEVEL:INFO}

ipfs-store:
  storage:
    type: IPFS
    host: ${IPFS_HOST:localhost}
    port: ${IPFS_PORT:5001}
    
  index:
    type: ELASTICSEARCH
    host: ${ELASTIC_HOST:localhost}
    port: ${ELASTIC_PORT:9300}
    additional:
      clusterName: ${ELASTIC_CLUSTERNAME:docker-cluster}
      indexNullValue: true
      
  pinning:
    strategies:
      - 
        id: ipfs_node
        type: native
        host: ${IPFS_HOST:localhost}
        port: ${IPFS_PORT:5001}
      - 
        id: ipfs_cluster
        type: ipfs_cluster
        enable: ${IPFS_CLUSTER_ENABLE:false}
        host: ${IPFS_CLUSTER_HOST:localhost}
        port: ${IPFS_CLUSTER_PORT:9094}

  api-spec:
     base: /ipfs-store
     store:
        uri: /store
     index:
        uri: /index
     store_index:
        uri: /store_index
     search:
        uri: /search/{index}
     fetch:
        uri: /fetch/{index}/{hash}
     config_index:
        uri: /config/index/{index}
        
```


### Storage layer

The storage layer is built in a generic way where different storage technologies could be used.  

*At the moment, only IPFS is supported.*

#### IPFS

| Property | Type | Sample value | Description |
| -------- | -------- | -------- | -------- | 
| ipfs-store.storage.type | String | IPFS | Select IPFS as a storage layer |
| ipfs-store.storage.host | String | localhost | Host to connect to the node |
| ipfs-store.storage.port | Integer | 5001 | Port to connect to the node |



#### SWARM

*Coming soon*



### Index layer

The Index layer is built in a generic way where different search engine technologies could be used.  

*At the moment, only ElasticSearch is supported.*

#### ELASTICSEARCH

| Property | Type | Sample value | Description |
| -------- | -------- | -------- | -------- | 
| ipfs-store.index.type | String | ELASTICTSEARCH | Select ELASTICTSEARCH as a index layer |
| ipfs-store.index.host | String | localhost | Host to connect to ElasticSearch
| ipfs-store.index.port | Integer | 9300 | Port to connect to ElasticSearch |
| ipfs-store.index.additional.clusterName | String | es-cluster | Name of the cluster |
| ipfs-store.index.additional.indexNullValue | Boolean | true | Index empty/null value with a default value `NULL` (useful to search on empty field) |



### Pinning strategy

A pinning strategy defines the way you want to pin (permanently store) your content. Whilst a `native` pinning strategy consists in pinning the content directly in a node. `ipfs_cluster` consists in pinning an IPFS-cluster to replicate the content across the cluster of nodes. Other implementations could be imagined.

Pinning stategies can be combined together and are executed asynchronously from the main thread.

**Strategies**

| Name (type) | Description |
| -------- | -------- | 
| native | Pin the node |
| ipfs_cluster | Pin an ipfs cluster node that replicates the content |


| Property | Type | Sample value | Description |
| -------- | -------- | -------- | -------- | 
| ipfs-store.pinning.strategies | Strategy[] | List of strategies|
| ipfs-store.pinning.strategies[0].id | String | Unique identifier of the stratefy |
| ipfs-store.pinning.strategies[0].type | String | Type of the strategy (`native`, `ipfs_cluster`) |
| ipfs-store.pinning.strategies[0].enable | Boolean | Enable/Disable the strategy |
| ipfs-store.pinning.strategies[0].host | String | Basic configuration (host)  
| ipfs-store.pinning.strategies[0].port | String | Basic configuration (port) | 
| ipfs-store.pinning.strategies[0].additional | Map | Basic configuration (additional) |




## Clients


### CLI


### Java


### spring-data


### JavaScript

