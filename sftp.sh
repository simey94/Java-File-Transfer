#SFTP script to asses baseline performance
HOST = ''
USER = 'ms255@st-andrews.ac.uk'
PASSWORD = 'hmrd5796$'
SRC_DIR = '/cs/home/ms255/Documents/Files/pg44823.txt'
TRGT_DIR = '/var/tmp'

SFTP -b mybatch.txt ms255@st-andrews.ac.uk 