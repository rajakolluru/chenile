#!/bin/bash

function usage(){
  	echo "Usage: $prog <interceptor-name> [interceptor-version [dest-folder]]" >&2
  	echo "interceptor version will default to $defaultVersion"
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



function generateInterceptor(){
	constructJsonfile > $json_file
	template_folder=$template_folder_base/interceptor-template
  	generateModule $template_folder $dest_folder $json_file "interceptorName org company InterceptorName"
  	doGitInit $dest_folder/$interceptorName $interceptorVersion
}

function constructJsonfile(){
	echo "{"
	echo "\"interceptorName\": \"$interceptorName\","
	echo "\"interceptorVersion\": \"$serviceVersion\","
	echo "\"org\": \"$org\","
	echo "\"company\": \"$company\","
	echo "\"chenilePackage\": \"$chenilePackage\","
	echo "\"chenileVersion\": \"$chenileVersion\","
	echo "\"InterceptorName\": \"$InterceptorName\""
	echo "}"
}

setenv "${0}" 
if (( $# < 1 ))
then
	usage
	_exit 1
fi


json_file=/tmp/$prog.$$
interceptorName=${1}
InterceptorName=$(camelCase $interceptorName)
interceptorVersion=${2:-$defaultVersion}
dest_folder=${3:-$defaultDestFolder}

[[ ! -d $dest_folder ]] && mkdir $dest_folder
echo "Creating interceptor ${interceptorName}(${interceptorVersion}) in folder $dest_folder" >&2
generateInterceptor
_exit 0
