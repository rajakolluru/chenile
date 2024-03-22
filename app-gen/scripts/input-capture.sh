# input-capture.sh - contains functions that capture inout from the user

function captureNonNullField(){
    error=1
    while (( error > 0 ))
    do
        read -p "$1:" field
        [[ ! -z $field ]] && error=0 
    done
    echo $field
}

## Usage: captureFieldWithDefaultValue prompt defaultvalue
## captures user input with a prompt. Defaults to defaultValue if the user pressed enter

function captureFieldWithDefaultValue(){
    read -p "$1 ($2)": field
    [[ -z $field ]] && field=$2
    echo $field
}


## Usage: choices "option1|Prompt1" "option2|Prompt2" ...
## Provides multiple choices to a user. Allows the user to select the 
## choice number. Upon successfully choosing a choice, the function echoes the option that 
## corresponds to the choice

function choices(){
    p="INVALID"
    while [[ $p == 'INVALID' ]]
    do
        index=1
        for x in "$@"
        do
            echo $index")" $(echo $x | cut -d"|" -f2-)>&2
            index=$((index + 1))
        done

        read -p 'Please enter your choice: (or quit to exit the program) ' p
        
        if [[ $p == quit ]]
        then
            _exit 0
        fi

        if  (( p == 0 ||  p > $# ))
        then
            echo "Invalid choice: $p. Try again" >&2
            p=INVALID
        fi
    done
    eval echo "$""${p}" | cut -d"|" -f1
    return 0
}

function chooseMultiple(){
  out=""
  for i in "$@"
  do
    read -p "$i: (ENTER/Y/y for yes)" c 
    if [[ $c == quit ]]
    then
      _exit 0
    fi

    if [[ $c == "" ]]  || [[ $c == "y" ]] || [[ $c == "Y" ]]
    then
      out="$out $i"
    fi
  done
  echo "$out"
}


