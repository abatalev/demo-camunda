#!/bin/bash

USE_CACHE=1
USE_COMPOSE=1
USE_MINIKUBE=0
USE_OPENSHIFT=0
USE_PODMAN=0

HELM_NAME=stand

HELM_STATE="install"
#HELM_STATE="delete"

PRJ_IMAGEGROUP="abatalev"
PRJ_IMAGEVERSION="2021-10-28"

source mk-setup.sh

build_all "process process-mock task1"