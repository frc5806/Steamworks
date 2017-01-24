from networktables import NetworkTables

class NetworkInterface(object):
	"""docstring for NetworkInterface."""
	rioIP = '10.58.06.2' # this shouldn't change
	tableName = 'SmartDashboard' # should be same in rio's java NT program
	table = None

	def __init__(self):
		super(NetworkInterface, self).__init__()
		NetworkTables.initialize(server=rioIP)
		self.table = NetworkTables.getTable(tableName)

	def pushVals(jetsonVals):
		table.putNumberArray("JetsonVals",jetsonVals)
