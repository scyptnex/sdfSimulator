#! /bin/sh

if [ "$1lol" = "lol" ]
then
	echo "need a folder"
	exit
fi

for fn in `ls -rS $1 | grep .*\.exp`
do
	heurist=`echo $fn | sed 's/exp$/5.heu/g'`
	if [ -f $heurist ]
	then
		echo "skipping $heurist"
	else
		echo "running $fn"
		java -cp ~/computing/sdfSimulator/ simulator.Mapper $fn
		
	fi
done
