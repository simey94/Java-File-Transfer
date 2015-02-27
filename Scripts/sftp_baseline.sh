#!/bin/sh
#SFTP script to asses baseline performance
echo "Starting transfer now..."
H=`hostname`	
for i in `seq 1 5`;
do
/bin/time -a -o /cs/home/ms255/workspace_linux/CS3102_Practical_1/Scripts/$H-5.data -f %e /cs/home/ms255/workspace_linux/CS3102_Practical_1/Scripts/sftp_runner.sh
done
