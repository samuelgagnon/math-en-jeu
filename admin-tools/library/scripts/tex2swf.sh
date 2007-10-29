#!/bin/bash


#first argument : pass the full path of the tex file
#second argument : the directory to store the swf

ffilename=$1

filename=${ffilename%.tex}

#convert the tex file to dvi
latex ${ffilename} -interaction=batchmode

#convert the file to eps
dvips -q -E ${filename}.dvi -o ${filename}.eps

#convert the file to pdf
epstopdf ${filename}.eps

#convert the file to swf
pdf2swf -q ${filename}.pdf

mv ${filename}.swf $2
