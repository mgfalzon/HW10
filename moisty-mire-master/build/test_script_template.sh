#!/bin/bash

dir_name=${PWD##*/}
ext=".log"
f_name="${dir_name}${ext}"

message1="- The output of your program is as follows"
message2="- The expected output is as follows"

echo ""
echo ""


# Clean any previoulsy built files and make a fresh one
make clean
make

# Remove any created gradebook files
rm -f mygradebook
./setup -N mygradebook

# Ask the user the input the key generated by the program
echo ""
read -p "Input The Key Printed Above : " key
echo ""
echo "Key is: $key"
echo ""

./gradebookadd -N mygradebook -K $key -AS -FN John -LN Smith
./gradebookadd -N mygradebook -K $key -AS -FN Russell -LN Tyler
./gradebookadd -N mygradebook -K $key -AS -FN Ted -LN Mason


./gradebookadd -N mygradebook -K $key -AA -AN Midterm -P 100 -W 0.25
./gradebookadd -N mygradebook -K $key -AG -AN Midterm -FN John -LN Smith -G 95
./gradebookadd -N mygradebook -K $key -AG -AN Midterm -FN Russell -LN Tyler -G 80
./gradebookadd -N mygradebook -K $key -AG -AN Midterm -FN Ted -LN Mason -G 90

./gradebookadd -N mygradebook -K $key -AA -AN Final -P 200 -W 0.5
./gradebookadd -N mygradebook -K $key -AG -AN Final -FN John -LN Smith -G 180
./gradebookadd -N mygradebook -K $key -AG -AN Final -FN Russell -LN Tyler -G 190
./gradebookadd -N mygradebook -K $key -AG -AN Final -FN Ted -LN Mason -G 150

./gradebookadd -N mygradebook -K $key -AA -AN Project -P 50 -W 0.25
./gradebookadd -N mygradebook -K $key -AG -AN Project -FN John -LN Smith -G 48
./gradebookadd -N mygradebook -K $key -AG -AN Project -FN Russell -LN Tyler -G 49
./gradebookadd -N mygradebook -K $key -AG -AN Project -FN Ted -LN Mason -G 50

echo "** Test 1 **" >> $f_name
echo $message1 >> $f_name
./gradebookdisplay -N mygradebook -K $key -PA -A -AN Midterm >> $f_name
echo $message2 >> $f_name
res=$'(Mason, Ted, 90)\n(Smith, John, 95)\n(Tyler, Russell, 80)\n'
echo "$res" >> $f_name
echo""


echo "** Test 2 **" >> $f_name
echo $message1 >> $f_name
./gradebookdisplay -N mygradebook -K $key -PA -G -AN Final >> $f_name
echo $message2 >> $f_name
res=$'(Tyler, Russell, 190)\n(Smith, John, 180)\n(Mason, Ted, 150)\n'
echo "$res" >> $f_name
echo""

echo "** Test 3 **" >> $f_name
echo $message1 >> $f_name
./gradebookdisplay -N mygradebook -K $key -PS -FN John -LN Smith >> $f_name
echo $message2 >> $f_name
res=$'(Midterm, 95)\n(Final, 180)\n(Project, 48)\n'
echo "$res" >> $f_name
echo""

echo "** Test 4 **" >> $f_name
echo $message1 >> $f_name
./gradebookdisplay -N mygradebook -K $key -PF -G >> $f_name
echo $message2 >> $f_name
res=$'(Smith, John, 0.9275)\n(Tyler, Russell, 0.92)\n(Mason, Ted, 0.85)\n'
echo "$res" >> $f_name
echo""

./gradebookadd -N mygradebook -K $key -DA -AN Project

echo "** Test 5 **" >> $f_name
echo $message1 >> $f_name
./gradebookdisplay -N mygradebook -K $key -PF -A >> $f_name
echo $message2 >> $f_name
res=$'(Mason, Ted, 0.6)\n(Smith, John, 0.6875)\n(Tyler, Russell, 0.675)\n'
echo "$res" >> $f_name
echo""


./gradebookadd -N mygradebook -K $key -AA -P 50 -W 0.125 -AN Project >> $f_name
read -p "Press Any Key to Enter Hidden Tests" temp

###############################################
############ Some Hidden Tests ################
###############################################

# clean the executables
make clean
