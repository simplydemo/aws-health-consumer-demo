

## install serverless

```
npm install -g serverless
```



```
export NODE_TLS_REJECT_UNAUTHORIZED=0
```

## Error fetching release: unable to get local issuer certificate 
```
NPM solves this:

export NODE_TLS_REJECT_UNAUTHORIZED=1

npm config set cafile="/path/to/my/cert" 

npm config set strict-ssl=false
 
this command npm i -g serverless@latest works like a charm
```

Once we have the access key and secret for this new user ready, we can store these credentials in a dedicated profile:

## Configuring the Serverless AWS credentials

```

serverless config credentials --provider aws --key <ABC> --secret <XYZ> --profile serverless-admin

```

## Maven Java Project Setup for AWS Lambda


## Deploying the AWS Lambda with Serverless

