# aws-health-consumer-demo
This is a Lambda application for demo that analyzes AWS Health event messages from SNS Topic, creates Google Hangout card messages, and sends real-time notifications.



## Git

```
git clone https://github.com/simplydemo/aws-health-consumer-demo.git
```

## Build Artifact

```
cd aws-health-consumer-demo
mvn clean package -DskipTests=true
```

## Build Image

```
docker build -t "aws-health-consumer-demo:local" -f ./cicd/docker/Dockerfile.corretto . 
```

## Run Container

```
docker run --rm --name=aws-health-consumer -p 8080:8080 aws-health-consumer:local
```

## Test

```
curl -X GET http://localhost:8080/lotto/v1/take
```

## Appendix

- [sdk-for-kotlin](https://docs.aws.amazon.com/sdk-for-kotlin/)
- [aws-doc-sdk-examples](https://github.com/awsdocs/aws-doc-sdk-examples/tree/main/kotlin)
- [SageMakerLambdaFunction](https://github.com/scmacdon/aws-doc-sdk-examples/blob/main/kotlin/usecases/workflow_sagemaker_lambda/src/main/kotlin/org/example/SageMakerLambdaFunction.kt)
- [JDK Releases](https://javaalmanac.io/jdk/)

```



"com.amazonaws.services.lambda.runtime.events.SNSEvent" 클래스를 대상으로 Kotlin Arrow 를 활용하여 free monad 방식으로 Query Expression 을 통해 데이터를 조회 하고 싶습니다.


AWS Health 이벤트가 SNS 토픽으로 전송되었을 때 Consumer의 SNSEvent 는 어떤 schema 구조를 가지는지 알려주세요.
```

- SNS Event

```json
{
  "Records": [
    {
      "EventSource": "aws:sns",
      "EventVersion": "1.0",
      "EventSubscriptionArn": "arn:aws:sns:REGION:ACCOUNT_ID:TOPIC_NAME:SUBSCRIPTION_ID",
      "Sns": {
        "Type": "Notification",
        "MessageId": "EXAMPLE_MESSAGE_ID",
        "TopicArn": "arn:aws:sns:REGION:ACCOUNT_ID:TOPIC_NAME",
        "Subject": "Example subject",
        "Message": "<AWS_HEALTH_EVENT>",
        "Timestamp": "2023-07-11T00:00:00.000Z",
        "SignatureVersion": "1",
        "Signature": "EXAMPLE_SIGNATURE",
        "SigningCertUrl": "https://sns.REGION.amazonaws.com/SimpleNotificationService-EXAMPLE.pem",
        "UnsubscribeUrl": "https://sns.REGION.amazonaws.com/?Action=Unsubscribe&SubscriptionArn=arn:aws:sns:REGION:ACCOUNT_ID:TOPIC_NAME:SUBSCRIPTION_ID",
        "MessageAttributes": {
          "exampleAttribute": {
            "Type": "String",
            "Value": "exampleValue"
          }
        }
      }
    }
  ]
}
```

- AWS Health Event Sample 1

```json
{
  "version": "0",
  "id": "7bf73129-1428-4cd3-a780-95db273d1602",
  "detail-type": "AWS Health Event",
  "source": "aws.health",
  "account": "123456789012",
  "time": "2023-01-26T01:43:21Z",
  "region": "ap-southeast-2",
  "resources": [],
  "detail": {
    "eventArn": "arn:aws:health:ap-southeast-2::event/AWS_ELASTICLOADBALANCING_API_ISSUE_90353408594353980",
    "service": "ELASTICLOADBALANCING",
    "eventTypeCode": "AWS_ELASTICLOADBALANCING_OPERATIONAL_ISSUE",
    "eventTypeCategory": "issue",
    "eventScopeCode": "PUBLIC",
    "communicationId": "4826e1b01e4eed2b0f117c54306d907c713586799d76d487c9132a40149ac107-1",
    "startTime": "Thu, 26 Jan 2023 13:19:03 GMT",
    "endTime": "Thu, 26 Jan 2023 13:44:13 GMT",
    "lastUpdatedTime": "Thu, 26 Jan 2023 13:44:13 GMT",
    "statusCode": "open",
    "eventRegion": "ap-southeast-2",
    "eventDescription": [
      {
        "language": "en_US",
        "latestDescription": "A description of the event will be provided here"
      }
    ],
    "affectedAccount": "123456789012",
    "page": "1",
    "totalPages": "1"
  }
}
```

```
val sql = "select id, detail.servic, detail.statusCode from HEALTH where id = '7bf73129-1428-4cd3-a780-95db273d1602'"
val query = QueryHandler("HEALTH", json)
val resultSet = query.executeQuery()
val record = resultSet.next()
```

- AWS Health Event Sample 2

```json
{
  "version": "0",
  "id": "7bf73129-1428-4cd3-a780-95db273d1602",
  "detail-type": "AWS Health Event",
  "source": "aws.health",
  "account": "123456789012",
  "time": "2023-01-27T01:43:21Z",
  "region": "us-west-2",
  "resources": ["arn:ec2-1-101002929", "arn:ec2-1-101002930", "arn:ec2-1-101002931", "arn:ec2-1-101002932"],
  "detail": {
    "eventArn": "arn:aws:health:us-west-2::event/AWS_EC2_INSTANCE_STORE_DRIVE_PERFORMANCE_DEGRADED_90353408594353980",
    "service": "EC2",
    "eventTypeCode": "AWS_EC2_INSTANCE_STORE_DRIVE_PERFORMANCE_DEGRADED",
    "eventTypeCategory": "issue",
    "eventScopeCode": "ACCOUNT_SPECIFIC",
    "communicationId": "1234abc01232a4012345678-1",
    "startTime": "Thu, 27 Jan 2023 13:19:03 GMT",
    "lastUpdatedTime": "Thu, 27 Jan 2023 13:44:13 GMT",
    "statusCode": "open",
    "eventRegion": "us-west-2",
    "eventDescription": [{
      "language": "en_US",
      "latestDescription": "A description of the event will be provided here"
    }],
    "eventMetadata": {
      "keystring1": "valuestring1",
      "keystring2": "valuestring2",
      "keystring3": "valuestring3",
      "keystring4": "valuestring4",
      "truncated": "true"
    },
    "affectedEntities": [{
      "entityValue": "arn:ec2-1-101002929",
      "lastUpdatedTime": "Thu, 26 Jan 2023 19:01:55 GMT",
      "status": "IMPAIRED"
    }, {
      "entityValue": "arn:ec2-1-101002930",
      "lastUpdatedTime": "Thu, 26 Jan 2023 19:05:12 GMT",
      "status": "IMPAIRED"
    }, {
      "entityValue": "arn:ec2-1-101002931",
      "lastUpdatedTime": "Thu, 26 Jan 2023 19:07:13 GMT",
      "status": "UNIMPAIRED"
    }, {
      "entityValue": "arn:ec2-1-101002932",
      "lastUpdatedTime": "Thu, 26 Jan 2023 19:10:59 GMT",
      "status": "RESOLVED"
    }],
    "affectedAccount": "123456789012",
    "page": "1",
    "totalPages": "10"
  }
}
```


- AWS Health Abuse Event Sample 1

```json
{
  "version": "0",
  "id": "7bf73129-1428-4cd3-a780-95db273d1602",
  "detail-type": "AWS Health Abuse Event",
  "source": "aws.health",
  "account": "123456789012",
  "time": "2018-08-02T05:30:00Z",
  "region": "global",
  "resources": [
    "arn:ec2-1-101002929",
    "arn:ec2-1-101002930",
    "arn:ec2-1-101002931",
    "arn:ec2-1-101002932"
  ],
  "detail": {
    "eventArn": "arn:aws:health:global::event/AWS_ABUSE_COPYRIGHT_DMCA_REPORT_2345235545_5323_2018_08_02_02_12_98",
    "service": "ABUSE",
    "eventTypeCode": "AWS_ABUSE_COPYRIGHT_DMCA_REPORT",
    "eventTypeCategory": "issue",
    "eventScopeCode": "ACCOUNT_SPECIFIC",
    "communicationId": "1234abc01232a4012345678-1",
    "startTime": "Thu, 02 Aug 2018 05:30:00 GMT",
    "lastUpdatedTime": "Thu, 27 Jan 2023 13:44:13 GMT",
    "statusCode": "open",
    "eventRegion": "us-west-2",
    "eventDescription": [
      {
        "language": "en_US",
        "latestDescription": "A description of the event will be provided here"
      }
    ],
    "eventMetadata": {
      "keystring1": "valuestring1",
      "keystring2": "valuestring2",
      "keystring3": "valuestring3",
      "keystring4": "valuestring4",
      "truncated": "true"
    },
    "affectedEntities": [
      {
        "entityValue": "arn:ec2-1-101002929",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:01:55 GMT",
        "status": "IMPAIRED"
      },
      {
        "entityValue": "arn:ec2-1-101002930",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:05:12 GMT",
        "status": "UNIMPAIRED"
      },
      {
        "entityValue": "arn:ec2-1-101002931",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:07:13 GMT",
        "status": "RESOLVED"
      },
      {
        "entityValue": "arn:ec2-1-101002932",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:10:59 GMT",
        "status": "PENDING"
      }
    ],
    "affectedAccount": "123456789012",
    "page": "1",
    "totalPages": "10"
  }
}
```

- AWS Health Abuse Event Sample 2

```json
{
  "version": "0",
  "id": "7bf73129-1428-4cd3-a780-95db273d1602",
  "detail-type": "AWS Health Abuse Event",
  "source": "aws.health",
  "account": "123456789012",
  "time": "2018-08-01T06:27:57Z",
  "region": "global",
  "resources": [
    "arn:ec2-1-101002929",
    "arn:ec2-1-101002930",
    "arn:ec2-1-101002931",
    "arn:ec2-1-101002932"
  ],
  "detail": {
    "eventArn": "arn:aws:health:global::event/AWS_ABUSE_DOS_REPORT_92387492375_4498_2018_08_01_02_33_00",
    "service": "ABUSE",
    "eventTypeCode": "AWS_ABUSE_DOS_REPORT",
    "eventTypeCategory": "issue",
    "eventScopeCode": "ACCOUNT_SPECIFIC",
    "communicationId": "1234abc01232a4012345678-1",
    "startTime": "Wed, 01 Aug 2018 06:27:57 GMT",
    "lastUpdatedTime": "Thu, 27 Jan 2023 13:44:13 GMT",
    "statusCode": "open",
    "eventRegion": "us-west-2",
    "eventDescription": [
      {
        "language": "en_US",
        "latestDescription": "A description of the event will be provided here"
      }
    ],
    "eventMetadata": {
      "keystring1": "valuestring1",
      "keystring2": "valuestring2",
      "keystring3": "valuestring3",
      "keystring4": "valuestring4",
      "truncated": "true"
    },
    "affectedEntities": [
      {
        "entityValue": "arn:ec2-1-101002929",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:01:55 GMT",
        "status": "IMPAIRED"
      },
      {
        "entityValue": "arn:ec2-1-101002930",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:05:12 GMT",
        "status": "UNKNOWN"
      },
      {
        "entityValue": "arn:ec2-1-101002931",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:07:13 GMT",
        "status": "UNIMPAIRED"
      },
      {
        "entityValue": "arn:ec2-1-101002932",
        "lastUpdatedTime": "Thu, 26 Jan 2023 19:10:59 GMT",
        "status": "RESOLVED"
      }
    ],
    "affectedAccount": "123456789012",
    "page": "1",
    "totalPages": "10"
  }
}
```
