#!/bin/sh
#SFTP script to asses baseline performance
echo "Starting transfer now..."
sftp 138.251.204.31 <<EOF
cd /var/tmp
put /cs/home/ms255/Documents/Files/lol.txt
quit
EOF
