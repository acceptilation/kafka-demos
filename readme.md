# Kafka Demos

The purpose of this project is to demonstrate two Kafka properties:

1. max.poll.interval.ms
2. max.poll.records

A Kafka listener can get stuck if the processing time of an individual record takes too long.
The same records will get processed over and over even though they don't produce individual errors.
This project's tests show how manipulating the above two properties can avoid this issue.

## Running the demos

```shell
./gradlew clean test
```

Run configuration:  
`clean-test`
