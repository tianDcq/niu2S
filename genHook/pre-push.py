#!/usr/bin/python
# -*- coding: UTF-8 -*- 

#Date: 2018/4/25
#Author: dylan
#Desc: git 钩子 自动提交版本文件

import os
import subprocess
versionPath = 'src/main/resources/version.dat'

def getGitCommitCount():
	p = subprocess.Popen('git rev-list HEAD --first-parent --count' , stdout=subprocess.PIPE)
	commitCount = p.stdout.read()
	commitCount = commitCount.decode("utf-8") 
	commitCount = commitCount.strip('\n')
	return commitCount

def genVersion():
    version = int(getGitCommitCount())
    with open(versionPath , 'w' , encoding = 'UTF-8') as f:
        f.write(str(version))
    return version
    
#生成版本文件
genVersion()
#提交版本文件
os.system('git add {}'.format(versionPath))
os.system('git commit -m 钩子自动提交版本文件')

