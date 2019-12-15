#!/bin/bash

pip2 install --upgrade pip -i https://mirrors.ustc.edu.cn/pypi/web/simple/
pip2 uninstall numpy
pip2 install numpy -i https://mirrors.ustc.edu.cn/pypi/web/simple/ --upgrade --ignore-installed
pip2 uninstall matplotlib
pip2 install matplotlib==2.0.2 -i https://mirrors.ustc.edu.cn/pypi/web/simple/ --upgrade --ignore-installed
pip2 uninstall backports.functools_lru_cache
pip2 install backports.functools_lru_cache -i https://mirrors.ustc.edu.cn/pypi/web/simple/ --upgrade --ignore-installed