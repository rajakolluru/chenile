#!/bin/bash

function usage(){
  	echo "Usage: $prog <namespace> [namespace-version [dest-folder]]" >&2
  	echo "namespace version will default to $defaultVersion"
	echo "dest-folder will default to $defaultDestFolder" >&2
	# echo "List of columns that need to be mapped can be passed in stdin"
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



function generateQueryNamespace(){
	constructJsonfile > $json_file
	template_folder=$template_folder_base/mybatis-query-service
  	generateModule $template_folder $dest_folder $json_file "namespace com org company Namespace"
  	doGitInit $dest_folder/$namespace $namespaceVersion
}

function constructJsonfile(){
	echo "{"
	echo "\"namespace\": \"$namespace\","
	echo "\"namespaceVersion\": \"$namespaceVersion\","
	echo "\"com\": \"$com\","
	echo "\"org\": \"$org\","
	echo "\"company\": \"$company\","
	echo "\"chenilePackage\": \"$chenilePackage\","
	echo "\"chenileVersion\": \"$chenileVersion\","
	echo "\"Namespace\": \"$Namespace\""
	# echo "\"columns\": "'['" $columns "']'
	echo "}"
}

## Finds the list of columns that need to be mapped
function getColumnsList(){
	columns=$(sed 's/^\(.*\)$/"\1"/'  | tr '\n ' ',') # surround each line with double-quotes. replace new lines with commas
	# drop the trailing , 
	columns=$(echo $columns | sed 's/,$//')
}

setenv "${0}" 
if (( $# < 1 ))
then
	usage
	_exit 1
fi


json_file=/tmp/$prog.$$
namespace=${1}
Namespace=$(camelCase $namespace)
namespaceVersion=${2:-$defaultVersion}
dest_folder=${3:-$defaultDestFolder}
# getColumnsList

[[ ! -d $dest_folder ]] && mkdir $dest_folder
echo "Creating namespace ${namespace}(${namespaceVersion}) in folder $dest_folder" >&2
generateQueryNamespace
_exit 0
