#! /bin/sh

little=problemLittle
medium=problemMedium
large=problemLarge

echo "Little"
java -cp .. simulator/MakeGLPK 2 1 3 > $little.dat
glpsol -m problem.mod -d $little.dat -o $little.out > $little.log

echo "Medium"
java -cp .. simulator/MakeGLPK 10 3 20 > $medium.dat
glpsol -m problem.mod -d $medium.dat -o $medium.out > $medium.log

echo "Large"
java -cp .. simulator/MakeGLPK 20 3 40 > $large.dat
#glpsol -m problem.mod -d $large.dat -o $large.out > $large.log
