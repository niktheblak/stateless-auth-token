stateless-auth-token
====================

Proof of concept implementation of cryptographic, stateless authentication token generation.

* Access tokens include user ID, role ID and expiration time
* Access tokens are quick to generate and validate
* A custom binary serializer is used to minimize access token size
