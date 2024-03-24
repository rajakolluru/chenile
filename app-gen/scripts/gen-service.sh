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



function generateService(){
	constructJsonfile > $json_file
	template_folder=$template_folder_base/service
  	generateModule $template_folder $dest_folder $json_file "service org company Service"
  	doGitInit $dest_folder/$service $serviceVersion
}

function constructJsonfile(){
	echo "{"
	echo "\"service\": \"$service\","
	echo "\"serviceVersion\": \"$serviceVersion\","
	echo "\"org\": \"$org\","
	echo "\"company\": \"$company\","
	echo "\"chenilePackage\": \"$chenilePackage\","
	echo "\"chenileVersion\": \"$chenileVersion\","
	echo "\"Service\": \"$Service\""
	echo "}"
}

setenv "${0}" 
if (( $# < 1 ))
then
	usage
	_exit 1
fi


json_file=/tmp/$prog.$$
service=${1}
Service=$(camelCase $service)
serviceVersion=${2:-$defaultVersion}
dest_folder=${3:-$defaultDestFolder}

[[ ! -d $dest_folder ]] && mkdir $dest_folder
echo "Creating service ${service}(${serviceVersion}) in folder $dest_folder" >&2
generateService
_exit 0
