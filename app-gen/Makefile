
.DEFAULT_GOAL := all

## prep-mustache: Build a file from a template file
.PHONY: prep-mustache
prep-mustache:
	if [ ! -d bin/node_modules ]; then cd bin; npm install mustache; fi 

## copy-scripts: copies all scripts from the scripts folder to bin
.PHONY: copy-scripts
copy-scripts: 
	cp scripts/* bin
	chmod +x bin/*

## create-bin: create the bin directory if it doesnt exist
.PHONY: create-bin
create-bin:
	if [ ! -d bin ]; then mkdir bin; fi

## delete-bin: Delete the bin folder
.PHONY: delete-bin
delete-bin:
	rm -rf bin

## all: 
.PHONY: all
all: create-bin copy-scripts prep-mustache

## help: type for getting this help
.PHONY: help
help: Makefile
	@echo 
	@echo " Choose a command to run in "$(PROJECTNAME)":"
	@echo
	@sed -n 's/^##//p' $< | column -t -s ':' |  sed -e 's/^/ /'
	@echo
