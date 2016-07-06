#Get Clusters
##Command
curl localhost:4243/rest/cluster
##Answer
{"mock":{"name":"mock","additionMetadata":null,"rules":[{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"12:00","startTimeOfDay":"0:00","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"24:00","startTimeOfDay":"12:01","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.UsageRule","additionMetadata":{"waitTimeToGrow":"60000","minQueueSizeToWarrantGrowth":"2","baseInstanceUsableGbMemory":"128","waitTimeToShrink":"60000","baseInstanceUsableVCores":"12","minIdealSizeToWarrantShrink":"2"}}],"collectInterval":10000,"collector":{"type":"com.cloudera.sa.nodescale.service.collector.MockCollector","additionMetadata":{}}}}

#Create a cluster
##Command
curl -X PUT -H "Content-Type: application/json" -d '{"name":"mock1","additionMetadata":null,"rules":[{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"12:00","startTimeOfDay":"0:00","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"24:00","startTimeOfDay":"12:01","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.UsageRule","additionMetadata":{"waitTimeToGrow":"60000","minQueueSizeToWarrantGrowth":"2","baseInstanceUsableGbMemory":"128","waitTimeToShrink":"60000","baseInstanceUsableVCores":"12","minIdealSizeToWarrantShrink":"2"}}],"collectInterval":10000,"collector":{"type":"com.cloudera.sa.nodescale.service.collector.MockCollector","additionMetadata":{}}}' localhost:4243/rest/cluster
##Answer
{"mock1":{"name":"mock1","additionMetadata":null,"rules":[{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"12:00","startTimeOfDay":"0:00","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"24:00","startTimeOfDay":"12:01","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.UsageRule","additionMetadata":{"waitTimeToGrow":"60000","minQueueSizeToWarrantGrowth":"2","baseInstanceUsableGbMemory":"128","waitTimeToShrink":"60000","baseInstanceUsableVCores":"12","minIdealSizeToWarrantShrink":"2"}}],"collectInterval":10000,"collector":{"type":"com.cloudera.sa.nodescale.service.collector.MockCollector","additionMetadata":{}}},"mock":{"name":"mock","additionMetadata":null,"rules":[{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"12:00","startTimeOfDay":"0:00","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"24:00","startTimeOfDay":"12:01","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.UsageRule","additionMetadata":{"waitTimeToGrow":"60000","minQueueSizeToWarrantGrowth":"2","baseInstanceUsableGbMemory":"128","waitTimeToShrink":"60000","baseInstanceUsableVCores":"12","minIdealSizeToWarrantShrink":"2"}}],"collectInterval":10000,"collector":{"type":"com.cloudera.sa.nodescale.service.collector.MockCollector","additionMetadata":{}}}}

#Get Cluster by Name
##Command
curl localhost:4243/rest/cluster/mock
##Answer
{"name":"mock1","additionMetadata":null,"rules":[{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"12:00","startTimeOfDay":"0:00","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"24:00","startTimeOfDay":"12:01","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.UsageRule","additionMetadata":{"waitTimeToGrow":"60000","minQueueSizeToWarrantGrowth":"2","baseInstanceUsableGbMemory":"128","waitTimeToShrink":"60000","baseInstanceUsableVCores":"12","minIdealSizeToWarrantShrink":"2"}}],"collectInterval":10000,"collector":{"type":"com.cloudera.sa.nodescale.service.collector.MockCollector","additionMetadata":{}}}

#Start Cluster Collector
##Command
curl -X POST localhost:4243/rest/cluster/mock/startup
##Answer
{"name":"mock","collector":{"type":"com.cloudera.sa.nodescale.service.collector.MockCollector","additionMetadata":{}},"rules":[{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"12:00","startTimeOfDay":"0:00","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.MinMaxTimeRangeRule","additionMetadata":{"daysOfWeekToApplyRule":"0,1,2,3,4,5,6","maxNumberOfNodes":"10","endTimeOfDay":"24:00","startTimeOfDay":"12:01","minNumberOfNodes":"5"}},{"type":"com.cloudera.sa.nodescale.service.rule.UsageRule","additionMetadata":{"waitTimeToGrow":"60000","minQueueSizeToWarrantGrowth":"2","baseInstanceUsableGbMemory":"128","waitTimeToShrink":"60000","baseInstanceUsableVCores":"12","minIdealSizeToWarrantShrink":"2"}}],"collectInterval":10000,"additionMetadata":null}

#Get Cluster Status
##Command
curl localhost:4243/rest/cluster/mock/existingstatus
##Answer
{"totalVCoresAvaliable":12,"usedGbMemory":0,"totalGbMemoryAvaliable":128,"usedVCores":0,"queuedVCores":0,"queuedGbMemory":0,"instanceStatuseList":[],"pastUsedVCoreList":[],"pastUsedGbMemoryList":[],"pastQueuedVCoreList":[],"pastQueuedGbMemoryList":[]}

#Set Mock ClusterStatus
##Command
curl -X POST -H "Content-Type: application/json" -d '{"totalVCoresAvaliable":12,"usedGbMemory":64,"totalGbMemoryAvaliable":128,"usedVCores":6,"queuedVCores":0,"queuedGbMemory":0,"instanceStatuseList":[],"pastUsedVCoreList":[],"pastUsedGbMemoryList":[],"pastQueuedVCoreList":[],"pastQueuedGbMemoryList":[]}' localhost:4243/rest/cluster/mock/mock
##Answer


#Collect form Cluster by Name
##Command
curl localhost:4243/rest/cluster/mock/collect
##Answer
{"requestedNumberOfNodesToAdd":2,"instanceIdsToKill":[]}





