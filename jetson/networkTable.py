import time
from networktables import NetworkTables

rioIP = '10.58.06.2' # this shouldn't change
tableName = 'JetsonToRio' # should be same in rio's java NT program

def initTable():
	NetworkTables.initialize(server=rioIP)
	return NetworkTables.getTable(tableName)

def pushVals(table, jetsonVals):
	table.putNumberArray(jetsonVals)
	
	
