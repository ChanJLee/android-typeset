#!/bin/bash

adb logcat -c
count=0
rm -rf temp
mkdir temp
adb logcat | while read line;
do 
    if [[ ${line} != *EvaluationSample* ]]; then
        continue
    fi
    echo ${line}
    ((count++))
    path=`echo ${line} | awk -F ' ' '{print $NF}'`
    if [[ ${path} == "" ]]; then
        echo "read path failed"
        continue
    fi

    file="./temp/data-${count}"
    adb pull ${path} ${file}
    python2 ./evaluation.py "figure-${count}" ${file} &
done