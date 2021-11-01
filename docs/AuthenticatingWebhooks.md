# Authenticating Verity Webhooks
Some users may wish to implement some form of authentication on their Verity webhook endpoint to increase application security. 
Instructions for authenticating webhooks can be found in this document. Currently the only form of authentication available is OAuth2,
if you require a different form of authentication please contact Evernym directly and we can discuss possible solutions. 

## Verity OAuth 2.0 flow

### How to add authorization information during update endpoint

#### OAuth2 version 1
Below is the payload to use during **update endpoint** to use the **OAuth 2 v1 flow**
Note: values mentioned in angular brackets should be replaced accordingly.
```
{
  "@id": "<uuid>",
  "@type": "did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD",
  "comMethod": {
    "id": "webhook",
    "value": "<webhook-url>",
    "type": 2,
    "packaging": {
        "pkgType": "plain"      # or "1.0" (indy)
    },
    "authentication": {
        "type":"OAuth2",
        "version":"v1",
        "data": {
            "url":"<access-token-provider-url>",
            "grant_type":"client_credentials",
            "client_id":"<client_id>",
            "client_secret":"<client_secret>"
        }
    }
  }
}
```

#### OAuth2 version 2
Below is the payload to use during **update endpoint** to use the **OAuth2 v2 flow**
Note: values mentioned in angular brackets should be replaced accordingly.
```
{
  "@id": "<uuid>",
  "@type": "did:sov:123456789abcdefghi1234;spec/configs/0.6/UPDATE_COM_METHOD",
  "comMethod": {
    "id": "webhook",
    "value": "<webhook-url>",
    "type": 2,
    "packaging": {
        "pkgType": "plain"    # or "1.0" (indy)
    },
    "authentication": {
        "type":"OAuth2",
        "version":"v2",
        "data": {
            "token":"<fixed-access-token>"
        }
    }
  }
}
```

## How OAuth2 flow works
1. Verity has to send a message to a registered endpoint
2. It checks the endpoint information, if it is:
   1. **OAuth2 v1**, it checks
      1. if it already has an unexpired 'access-token', use it 
      2. If it doesn't then 
         1. sends a POST request
            1. to the **<access-token-provider-url>** 
            2. with appropriate **form data** (which includes "grant_type", "client_id", "client_secret" fields)
         2. expects an **OK** response with **json** payload with these two fields at minimum
            1. **access_token** (the token to be used later on)
            2. **expires_in** (access token expiry time in seconds)
   2. **OAuth2 v2**
      1. use the fixed access-token provided during update endpoint
3. Once verity has the access token
   it sends the actual message to the **<webhook-url>**
   with a header called '**Authorization**' with value 'Bearer **<access-token>**'
