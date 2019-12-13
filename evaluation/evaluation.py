#!/usr/bin/env python
# coding=utf-8

import json
import numpy as np
import matplotlib.pyplot as plt
import matplotlib.mlab as mlab

def render(samples): 
    n, bins, patches = plt.hist(samples, 10, facecolor='blue', alpha=0.5)

    plt.xlabel('rj')
    plt.ylabel('count')
    plt.title('tex algorithm evaluation')
    plt.axis([-1, 8, 0, len(samples)])
    plt.grid(True)
    plt.show()


if __name__ == '__main__':
    render(json.loads("[0, 1, 2, 2, 2.5, 3]"))