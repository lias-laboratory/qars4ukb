#!/bin/bash

if [ $# -eq 0 ]; then
	echo "N-Triples file is required (i.e. watdiv100K.nt)."
	exit 1
fi

echo Start
while read IN; do 
	S="${IN::-1}"
	
	trustvalue=$(( ( RANDOM % 10 )  + 1 ))
	div=10
	trustvalue=$(bc -l <<< "scale = 2; ($trustvalue /  $div)")
	
	trustline="$S <$trustvalue> ."

  	echo $trustline 
done < $1
