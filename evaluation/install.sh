#!/bin/bash

pip2 install --upgrade pip
pip2 uninstall matplotlib
pip2 install matplotlib==2.0.2
pip2 uninstall backports.functools_lru_cache
pip2 install backports.functools_lru_cache