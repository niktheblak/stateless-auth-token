# stateless-auth-token

Proof of concept implementation of cryptographic, stateless authentication token generation.

* Access tokens include user ID, role ID and expiration time
* Access tokens are quick to generate and validate
* A custom binary serializer is used to minimize access token size

## Instructions

### Getting Started

Clone project from repository:

    $ git clone git@bitbucket.org:niktheblak/stateless-auth-token.git
    $ cd stateless-auth-token

### Running Unit Tests

Make sure you have working installations of JDK and SBT available on your workstation. Open terminal and execute:

    $ sbt test

### Print Token Statistics

Open terminal and execute:

    $ sbt run

Select `CLI` from the options provided by SBT

### Token Generation Web Service

Start a token generation web service:

    $ sbt
    $ > jetty:start

After this you have the following REST API available on `http://localhost:8080`:

#### Create Token

Route: `http://localhost:8080/token`

Generates an access token with the given user ID and user role encoded in it.

Parameter | Explanation
--------- | -----------
user_id   | User ID to be encoded in the token
role      | User role to be encoded in the token

Response: Encoded access token

#### Authenticate Against A Token

Route: `http://localhost:8080/auth`

Decodes an access token and greets the user if the token is valid.

Parameter | Explanation
--------- | -----------
token     | Encoded access token

Response: Greeting or error message if the token was not valid.