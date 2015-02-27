#!/bin/sh
#SFTP script to asses baseline performance
cd /var/tmp
sftp pc2-037-l <<EOF
cd /var/tmp
get ms_filelist.xml
quit
EOF
