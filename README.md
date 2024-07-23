# aws-health-delibird
This is a Lambda application for demo that analyzes AWS Health event messages from SNS Topic, creates Google Hangout card messages, and sends real-time notifications.


## Git

```
git clone https://github.com/simplydemo/aws-health-delibird.git
```

## Build Artifact

```
cd aws-health-delibird


# Install custom library (just one time)
mvn install:install-file \
  -Dfile=libs/json-sql-1.0.0.jar \
  -DgroupId=io.github.thenovaworks \
  -DartifactId=json-sql \
  -Dversion=1.0.0 \
  -Dpackaging=jar

mvn clean package -DskipTests=true
```


## Build image
```
docker build -t "aws-health-delibird:v1.0-local" -f ./cicd/docker/Dockerfile.corretto . 
```

## Run Container

```
docker run --rm \
-p 9000:8080 \
-e AWS_LAMBDA_FUNCTION_NAME=aws-health-delibird-lambda \
-e AWS_LAMBDA_FUNCTION_MEMORY_SIZE=512 \
-e AWS_LAMBDA_FUNCTION_TIMEOUT=60 \
-e AWS_REGION=ap-northeast-2 \
-e PROFILE_ACTIVE=local \
-e GCHAT_WEBHOOK_URL="<google-hangout-webhook-url>" \
--name aws-health-delibird-lambda aws-health-delibird:v1.0-local
```

## Test

```
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d @./cicd/docker/event.json -H "Content-Type: application/json"
```

 
## Appendix
- [SAM](./cicd/sam/HELP.md) 모델을 통한 로컬 테스트 가이드
- [docker](./cicd/docker/HELP.md) 컨테이너를 통한 로컬 테스트 가이드
- [sdk-for-kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/)
- [aws-doc-sdk-examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin)
- [SageMakerLambdaFunction](https://github.com/scmacdon/aws-doc-sdk-examples/blob/main/kotlin/usecases/workflow_sagemaker_lambda/src/main/kotlin/org/example/SageMakerLambdaFunction.kt)
- [JDK Releases](https://javaalmanac.io/jdk/)
