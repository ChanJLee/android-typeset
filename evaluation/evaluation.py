#!/usr/bin/env python
# coding=utf-8

import json
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab
import subprocess
import sys
import os

def render(title, samples): 
    global UUID
    n, bins, patches = plt.hist(samples, 10, facecolor='blue', alpha=0.5)
    plt.xlabel('rj')
    plt.ylabel('count')
    plt.title("tex algorithm " + title)
    plt.axis([-1, 8, 0, len(samples)])
    plt.grid(True)
    plt.show()

def load(title, path):
    print "title, ", title
    print "path, ", path

    try:
        s = os.popen("adb shell cat " + path).read()
        print s
        render(title, json.loads(s))
    except Exception as e:
        print e.message

if __name__ == '__main__':
    load(sys.argv[1], sys.argv[2])