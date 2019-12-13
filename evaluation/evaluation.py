#!/usr/bin/env python
# coding=utf-8

import json
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab
import subprocess
import os

UUID = 0

def render(samples): 
    n, bins, patches = plt.hist(samples, 10, facecolor='blue', alpha=0.5)

    plt.xlabel('rj')
    plt.ylabel('count')
    UUID = UUID + 1
    plt.title('tex algorithm evaluation ' + UUID)
    plt.axis([-1, 8, 0, len(samples)])
    plt.grid(True)
    plt.show()

def load(path):
    try:
        s = os.popen("adb shell cat " + path).read()
        render(json.loads(s))
    except Exception as e:
        print e.message

if __name__ == '__main__':
    proc = subprocess.Popen(['adb','logcat'], stdout=subprocess.PIPE)
    for line in proc.stdout:
        if "EvaluationSample" in line:
            print line.split()
            load(line.split("EvaluationSample")[1].strip())