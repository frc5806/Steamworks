import time
from networktables import NetworkTables

rioIP = '10.58.06.2' # this shouldn't change
tableName = 'JetsonToRio' # should be same in rio's java NT program

NetworkTables.initialize(server=rioIP)

table = NetworkTables.getTable(tableName)

# visionVals should be an array of 4 ints
def pushData(visionVals):
	table.putNumberArray("jetsonVals", visionVals)




