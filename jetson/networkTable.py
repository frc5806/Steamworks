import time
from networktables import NetworkTables

rioIP = '10.58.06.2' #this shouldn't change
tableName = 'JetsonToRio' #should be same in rio's java NT program
updateRateSecs = 1 

#initialize Jetson as client to the roborio server
NetworkTables.initialize(server=rioIP)

table = NetworkTables.getTable(tableName)

while True:
	
	#assuming for now that the opencv script returns an array of 4 ints
	visionOutputs = [1,2,3,4] #fill this in later
	
	table.putNumberArray("jetsonVals", visionOutputs)
	
	time.sleep(updateRateSecs)





