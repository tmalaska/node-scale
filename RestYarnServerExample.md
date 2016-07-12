#build the project
mvn clean package

#Update jar file to edge node
scp target/NodeScale.jar root@ted-malaska-cap1-barclay-1.vpc.cloudera.com:./

#Go to edge node
ssh root@ted-malaska-cap1-barclay-1.vpc.cloudera.com

#Run the code
export JAVA_HOME=/opt/jdk1.8.0_91/
hadoop jar NodeScale.jar com.cloudera.sa.exe.NodeScale 4242 YARN


#List cluster collectors
##Request
curl ted-malaska-cap1-barclay-1.vpc.cloudera.com:4242/rest/cluster
##Answer
{
  "YARN": {
    "collectInterval": 10000,
    "additionMetadata": null,
    "collector": {
      "additionMetadata": {},
      "type": "com.cloudera.sa.nodescale.service.collector.YarnCollector"
    },
    "rules": [
      {
        "additionMetadata": {
          "waitTimeToGrow": "60000",
          "minQueueSizeToWarrantGrowth": "2",
          "baseInstanceUsableGbMemory": "128",
          "name": "usageRule",
          "waitTimeToShrink": "60000",
          "baseInstanceUsableVCores": "12",
          "minIdealSizeToWarrantShrink": "2"
        },
        "type": "com.cloudera.sa.nodescale.service.rule.UsageRule"
      },
      {
        "additionMetadata": {
          "daysOfWeekToApplyRule": "0,1,2,3,4,5,6",
          "maxNumberOfNodes": "10",
          "name": "minMaxRule1",
          "endTimeOfDay": "12:00",
          "startTimeOfDay": "0:00",
          "minNumberOfNodes": "3"
        },
        "type": "com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule"
      },
      {
        "additionMetadata": {
          "daysOfWeekToApplyRule": "0,1,2,3,4,5,6",
          "maxNumberOfNodes": "10",
          "name": "minMaxRule2",
          "endTimeOfDay": "24:00",
          "startTimeOfDay": "12:01",
          "minNumberOfNodes": "5"
        },
        "type": "com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule"
      }
    ],
    "name": "YARN"
  }
}

#Start Collector
##Request
curl -X POST ted-malaska-cap1-barclay-1.vpc.cloudera.com:4242/rest/cluster/YARN/startup

#Get Cluster Status
##Request
curl ted-malaska-cap1-barclay-1.vpc.cloudera.com:4242/rest/cluster/YARN/existingstatus

#Get Cluster Collect
##Request
curl ted-malaska-cap1-barclay-1.vpc.cloudera.com:4242/rest/cluster/YARN/collect
##Answer
{"requestedNumberOfNodesToAdd":2,"instanceIdsToKill":[]}