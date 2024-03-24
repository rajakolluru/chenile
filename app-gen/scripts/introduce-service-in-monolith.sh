#!/bin/bash

function bundleMonolith(){
	alterPomFile
	alterApplication
}

function alterPomFile(){
## TODO
}

function alterApplication(){
## TODO
}

function setenv(){
	curprog=${1}
    prog=${1##*/}
    
    if  [[ $curprog != */* ]] 
    then
        scripts_folder="$PWD"
    else 
        scripts_folder=${curprog%/*}
    fi
    [[ $scripts_folder != /* ]] && scripts_folder=$(pwd)/${scripts_folder} ## handle relative path invocations

    source $scripts_folder/common-functions.sh
    setBaseVars
}

setenv "${0}" 

tmpfile1=/tmp/$prog.$$.$RANDOM
tmpfile2=/tmp/$prog.$$.$RANDOM
tmpfile3=/tmp/$prog.$$.$RANDOM

setenv $0

usage="${prog}"
function usage {
	echo "$usage <monolith-directory> <chenile-service-name>" 1>&2
	echo "This script makes the monolith (defined in monolith-directory) to bundle the chenile-service." 1>&2
	echo "$prog -? for this usage message" 1>&2
}

function _exit {
	rm -f $tmpfile1 $tmpfile2 $tmpfile3
	exit $1
}

if [[ $1 == "-?" ]]
then
	usage
	_exit 1
fi
bundleMonolith "$1" "$2"

_exit 0
