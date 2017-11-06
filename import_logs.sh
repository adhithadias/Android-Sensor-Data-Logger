#!/bin/sh

adb push ./export/* /sdcard/DCIM/FML/paths/

#!/bin/sh

# Import the FML recordings.
adb pull /sdcard/DCIM/FML/recordings

adb shell mkdir -p /sdcard/DCIM/FML/old_recordings/

# Move the old files to the old_recordings folder.
adb shell mv "/sdcard/DCIM/FML/recordings/*" /sdcard/DCIM/FML/old_recordings/

#!/bin/sh

# Import the data log file from
adb pull /storage/emulated/0/Android/data/com.example.adhithadias27.gyrocheck/files/output.csv

# Remove the data log file
adb shell rm -i /storage/emulated/0/Android/data/com.example.adhithadias27.gyrocheck/files/output.csv


# Import the data log file from
adb pull /storage/emulated/0/GyroApp

# Remove the data log file
adb shell rm -i /storage/emulated/0/GyroApp/logdata/output.csv