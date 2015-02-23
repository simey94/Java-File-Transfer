#!/bin/sh
#SFTP script to asses baseline performance
echo "Starting transfer now..."
sftp 138.251.212.113 <<EOF
cd /var/tmp
put /cs/home/ms255/Documents/Files/TheFastandtheFuriousJohnIreland1954goofyrip_512kb.mp4
quit
EOF
