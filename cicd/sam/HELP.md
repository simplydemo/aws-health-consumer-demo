# SAM 로컬 테스트

[SAM - AWS Serverless Application Model](https://aws.amazon.com/ko/serverless/sam/) AWS에서 서버리스 애플리케이션을 구축하고 실행하는 경험을 간소화하고 개선하는 오픈 소스 개발자 도구 입니다.

로컬 환경에서 빠르게 람다 이벤트를 트리거하고 디버깅 할 수 있습니다.

## Build
Kotlin 프로젝트를 빌드 합니다.

```
mvn install:install-file -Dfile=libs/json-sql-1.0.0.jar -DgroupId=io.github.thenovaworks -DartifactId=json-sql -Dversion=1.0.0 -Dpackaging=jar

mvn clean package -DskipTests=true
```

## Mock 설정 

- [env.json](./env.json) Lambda 환경 변수를 위한 Mock 을 구성합니다.

```json
{
  "AwsHealthDelibird": {
    "AWS_REGION": "<region>",
    "PROFILE_ACTIVE": "<profile>",
    "GCHAT_WEBHOOK_URL": "<google-hangout-webhook-url>"
  }
}

```

- [event.json](./event.json) 람다 이벤트를 위한 SNS Mock 메시지를 구성합니다. 


## SAM local Test

```
cd ./cicd/sam

sam local invoke -t ./template.yaml --env-vars env.json --event event.json AwsHealthDelibird
```

