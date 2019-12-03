# Distributed lock library
This small library implements distributed lock in redis.
Can be useful for microservice approach to synchronize events on different nodes.

## Requirements
 - Java 11
 - Redis


## Integration

To integrate you need to build library 
```$bash
./gradlew clean build
```

and to add following instructions in your gradle build file you need to add remote repository

And add dependency
```
dependencies {
    compile group: "com.livekazan", name: "distributed-lock", version:"1.1"
}
```

## How to use

```$java
 @Autowired
 private ILockService lockService;

 private int releaseTimeMs  = 1000;
....

 try (IJLock lock = lockService.acquire("prefix", "lockId",releaseTimeMs)) {

    // do synchornious stuff fot all events/objects with given lockId
 } catch (LockingException  e) {
    log.error("");
    throw new UpdatedByAnotherThreadException();
 }
```