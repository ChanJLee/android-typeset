#!/bin/bash

adb logcat -c
cout=0
adb logcat | while read line;
do 
    if [[ ${line} != *EvaluationSample* ]]; then
        continue;
    fi
    echo ${line};
    ((i++));
    path=`echo ${line} | awk -F ' ' '{print $10}'`;
    python2 ./evaluation.py "figure-${i}" ${path} &
done