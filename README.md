# Statistics API

Restful API for real time statistic.

## Launch

    sbt test run -Dconfig.file=<path to config file>
         
Example of config file can be found in `conf/config.json`

## API

### Adding transaction

```
POST /transactions

{
  "amount": transaction amount, double, 
  "timestamp": transaction time in epoch in millis in UTC timezone
}
```

#### Return format
Empty body with status:
- 201 - in case of success
- 204 - if transaction is older than 60 seconds

### Retrieving statistics

Returns the statistic based on the transactions which happened in the last 60 seconds.

Executes in constant time and memory. 

```
GET /statistics
```

#### Return format
```
{
    "sum": double, 
    "avg": double, 
    "max": double, 
    "min": double, 
    "count": double
}
```