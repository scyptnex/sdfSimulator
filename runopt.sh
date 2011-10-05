#! /bin/sh

if [ "$1lol" = "lol" ]
then
	echo "need a folder"
	exit
fi

for fn in `ls -rS $1 | grep .*\.exp`
do
	out=`echo $fn | sed 's/exp$/opt/g'`
	log=`echo $fn | sed 's/exp$/log/g'`
	if [ -f $out ]
	then
		echo "skipping $out"
	else
		echo "running $out"
		glpsol -m ~/computing/sdfSimulator/cplex/ip.mod -d $1/$fn -o $1/$out > $log
	fi
done
