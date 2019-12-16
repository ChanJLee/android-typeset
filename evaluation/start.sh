#!/bin/bash

adb logcat -c
count=0
adb logcat | while read line;
do 
    if [[ ${line} != *EvaluationSample* ]]; then
        continue;
    fi
    echo ${line};
    ((count++));
    path=`echo ${line} | awk -F ' ' '{print $10}'`;
    echo "read ${path}"
    python2 ./evaluation.py "figure-${count}" ${path} &
done