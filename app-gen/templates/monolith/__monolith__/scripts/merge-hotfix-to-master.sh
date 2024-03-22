#!/usr/bin/env bash

# Merge the hotfix to master

branch=$(git status | head -1 | awk '{print $NF}')

if [[ $branch == "master" ]]
then
    echo "Oops. You are already on master. Don't know what to merge"
    exit 1
fi

git checkout master
git merge $branch

if (( $? != 0 ))
then
    echo "Error in merge. Please check."
    exit 2
fi

read -p "Looks like the merge went through fine. Do you want to delete hotfix branch?" yes
if [[ $yes == y* ]]
then
    git branch -d $branch
    echo "Deleted branch $branch"
    exit 0
fi

exit 0
