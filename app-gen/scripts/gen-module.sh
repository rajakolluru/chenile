#!/bin/bash

function usage(){
  	echo "Usage: $prog <service-name> [service-version [dest-folder]]" >&2
  	echo "service version will default to $defaultVersion"
	echo "dest-folder will default to $defaultDestFolder" >&2
}

function _exit {
	rm -f $json_file
  	exit $1
}

## Program initialization helper that sets all the environment variables required
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



function generateModulePom(){
	constructJsonfile > $json_file
	template_folder=$template_folder_base/module
  	generateModule $template_folder $dest_folder $json_file "module"
}

function constructJsonfile(){
	echo "{"
	echo "\"module\": \"$module\","
	echo "\"moduleVersion\": \"$moduleVersion\","
	echo "\"com\": \"$com\","
	echo "\"org\": \"$org\","
	echo "\"company\": \"$company\","
	echo "\"chenilePackage\": \"$chenilePackage\","
	echo "\"chenileVersion\": \"$chenileVersion\","
	echo "\"moduleList\": [ $moduleList ]"
	echo "}"
}

function makeModuleList(){
	ret=""
	for i in "$@"
	do
		[[ ! -z $ret ]] && ret="$ret,"
		ret="$ret $i "
	done
	echo $ret
}



if (( $# < 1 ))
then
	usage
	_exit 1
fi

setenv "${0}" 
json_file=/tmp/$prog.$$
module=${1}; shift
Module=$(camelCase $module)
moduleVersion=${1:-$defaultVersion}; shift
dest_folder=${1:-$defaultDestFolder}; shift
moduleList=$(makeModuleList "$@")

[[ ! -d $dest_folder ]] && mkdir $dest_folder
echo "Creating a module pom $module($moduleVersion) in $dest_folder for the following folders: $moduleList " >&2
generateModulePom
_exit 0
