#!/usr/bin/python
# -*- coding: UTF-8 -*- 

#Date: 2018/4/26
#Author: dylan
#Desc: git 钩子 文件生成

import os
import subprocess
import shutil
gitHookPath = '../.git/hooks/pre-push.sample'
gitPrePushHookSrc = 'pre-push'
gitPrePushHookPythonSrc = 'pre-push.py'
gitPrePushHookDst = '../.git/hooks/pre-push'
gitPrePushHookPythonDst = '../.git/hooks/pre-push.py'

print(gitHookPath)
if os.path.exists(gitHookPath):
	print(gitHookPath)
	os.remove(gitHookPath)

print(gitPrePushHookSrc)
if not os.path.exists(gitPrePushHookDst):
	print(gitPrePushHookSrc)
	shutil.copyfile(gitPrePushHookSrc , gitPrePushHookDst)

print(gitPrePushHookPythonSrc)
if not os.path.exists(gitPrePushHookPythonDst):
	print(gitPrePushHookPythonSrc)
	shutil.copyfile(gitPrePushHookPythonSrc , gitPrePushHookPythonDst)


