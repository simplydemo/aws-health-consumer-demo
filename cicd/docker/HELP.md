# Docker 로컬 테스트

[SAM - AWS Serverless Application Model](https://aws.amazon.com/ko/serverless/sam/) AWS에서 서버리스 애플리케이션을 구축하고 실행하는 경험을 간소화하고 개선하는 오픈 소스 개발자 도구 입니다.

로컬 환경에서 빠르게 람다 이벤트를 트리거하고 디버깅 할 수 있습니다.

## Build
Kotlin 프로젝트를 빌드 합니다.

```
mvn install:install-file -Dfile=libs/json-sql-1.0.0.jar -DgroupId=io.github.thenovaworks -DartifactId=json-sql -Dversion=1.0.0 -Dpackaging=jar

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


## Send SNS Event to Local Container
```
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d @./cicd/docker/event.json -H "Content-Type: application/json"
```

