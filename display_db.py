#! /bin/python
import os
from environment import *

DB_PATH = '/data/data/' + PREFIX + '/databases/' + DB_NAME

for i in 5554, 5556, 5558, 5560, 5562:

	cmd = 'adb -s emulator-' + str(i) + ' pull ' + DB_PATH + ' ' + str(i) + '.db'
	os.system(cmd)

	print "Contents of eumulator", i
	display_db_cmd = 'sqlite3 ' + str(i) + '.db ' + '"SELECT * FROM ' + TABLE_NAME + '"'
	os.system(display_db_cmd)
