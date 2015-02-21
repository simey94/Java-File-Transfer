HOST = 'ms255.host.cs.st-andrews.ac.uk'
USER = 'ms255@st-andrews.ac.uk'
PASSWORD = 'hmrd5796$'
SRC_DIR = '/cs/home/ms255/Documents/Files/pg44823.txt'
THEFILES ='ls -p $SRC_DIR | grep -v "/"'
TRGT_DIR = '/var/tmp'

for file in $THEFILES
do
    echo "Processing $file"
    sftp -u login,password -e "put $THEFOLDER/$file;quit" theftp/sub/folder
done

SFTP -b mybatch.txt ms255@st-andrews.ac.uk 