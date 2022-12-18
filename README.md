# OpenTable Coding Challenge

Thank you for accepting our coding challenge. Please take as much time as required to demonstrate your coding strengths, as your application will provide topics of conversation in the following interview stage.

## Summary

We would like you to build a non-blocking Web API using Java, Spring Boot (WebFlux), & MongoDB.

## Implementation Requirements

- Service:
  - Generate a unique "short-URL" (e.g. `AaNrWqQ`) given an original, long URL (e.g. `http://www.opentable.com`)
  - The short-URL must be a string, seven digits in length, containing only upper/lower case alpha characters `[A-Z][a-z].`
- Repository:
  - Persist the short & long URL mappings in MongoDB.
  - Fetch documents given a short URL as a key.
  - Allow for update & delete operations.
- Controller:
  - POST: Returns a short URL from a provided long URL.
  - GET: Returns an HTTP Redirect response to the original long URL for a given short URL.
  - DELETE: delete the record associated with a short URL.
  - PUT: update the record associated with a short URL.
- Tests:
  - Perform CRUD operations & verify the results.
  - Verify the short-URL generation behavior.

## Submission

- `mvn clean install` should return without errors.
- The app should launch on `http://localhost:8080`
- The app should connect to a local `test` MongoDB instance on port `27017`.

Please submit your program either by sending a zip file or by providing a link to a repository to your point of contact at OpenTable. If sharing a repository, please ensure it's private to prevent sharing with the general public.
